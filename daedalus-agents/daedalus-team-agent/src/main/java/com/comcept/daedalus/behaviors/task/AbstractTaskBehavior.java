/*
 * This technical data is controlled under the International Traffic in Arms Regulation (ITAR) and may not be exposed to a foreign person, either in the U.S., or abroad, without proper authorization by the U.S. Department of State.
 *
 * RESTRICTED RIGHTS
 * Contract No.: UAS and Net-Centric Resource Management Teaming IR&D
 * Contractor Name: L3HARRIS, COMCEPT DIVISION
 * Contractor Address: 1700 Science Place, Rockwall, TX 75032
 * The Government's rights to use, modify, reproduce, release, perform, display, or disclose this software are restricted by paragraph (b)(3) of the Rights in Noncommercial Computer Software and Noncommercial Computer Software Documentation clause contained in the above identified contract.  Any reproduction of computer software or portions thereof marked with this legend must also reproduce the markings.  Any person, other than the Government, who has been provided access to such software, must promptly notify the above named Contractor.
 *
 * COPYRIGHT 2018, 2020 L3 TECHNOLOGIES INC., COMCEPT DIVISION, A SUBSIDIARY OF L3HARRIS TECHNOLOGIES, INC. (L3HARRIS).  ALL RIGHTS RESERVED.
 */
package com.comcept.daedalus.behaviors.task;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.TimerScheduler;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.task.TaskEvents;
import com.comcept.daedalus.api.task.TaskId;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.api.team.TeamEvents;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.behaviors.task.data.TeamTaskPlan;
import com.comcept.daedalus.behaviors.task.utils.FusionTaskUtils;
import com.comcept.daedalus.behaviors.task.utils.PilotTaskUtils;
import com.comcept.daedalus.behaviors.task.utils.TaskStatusUtils;
import com.comcept.daedalus.behaviors.task.utils.TaskUtils;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.daedalus.msglib.MessageState;
import com.comcept.daedalus.msglib.TeamTaskDetails;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

public abstract class AbstractTaskBehavior extends AbstractBehavior<Task> {

    @Value
    @Accessors(fluent = true)
    private static class TaskTimer implements Task {

        @NonNull TaskId taskId;

    }

    @Value
    @Accessors(fluent = true)
    private static class AdaptedResponse implements Task {

        @NonNull TeamEvents.TeamData teamData;

    }

    private static final Duration TASK_EXPIRATION = Duration.ofSeconds(60);

    private final TimerScheduler<Task> timers;

    private final ActorRef<Task> taskConnector;
    private final EventDepot eventDepot;
    private final ActorRef<TeamEvents.TeamData> teamDataAdapter;

    private final Map<TaskId, TeamTaskPlan> teamTaskPlans = new HashMap<>();

    AbstractTaskBehavior(ActorContext<Task> context,
                         TimerScheduler<Task> timers,
                         ActorRef<Task> taskConnector,
                         EventDepot eventDepot) {
        super(context);

        this.timers = timers;
        this.taskConnector = taskConnector;
        this.eventDepot = eventDepot;

        teamDataAdapter = context.messageAdapter(TeamEvents.TeamData.class, AdaptedResponse::new);

        taskConnector.tell(TaskEvents.RegisterListener.builder()
                .subscriptionHandler(TaskMessages.TeamTask.class, context.getSelf())
                .subscriptionHandler(TaskMessages.PilotTaskStatus.class, context.getSelf())
                .subscriptionHandler(TaskMessages.FusionTaskStatus.class, context.getSelf())
                .build());
    }

    protected abstract TaskMessages.TeamTaskStatus estimateTaskFeasibility(
            TaskMessages.TeamTask msg);

    protected abstract Optional<TeamTaskPlan> createTeamTaskPlan(TaskMessages.TeamTask msg);

    protected void requestTeamData() {
        eventDepot.teamEventBus().publish(buildTeamDataRequest());
    }

    @Override
    public Receive<Task> createReceive() {
        return newReceiveBuilder()
                .onMessage(TaskMessages.TeamTask.class, this::onTeamTask)
                .onMessage(TaskMessages.PilotTaskStatus.class, this::onPilotTaskStatus)
                .onMessage(TaskMessages.FusionTaskStatus.class, this::onFusionTaskStatus)
                .onMessage(TaskTimer.class, this::onTaskTimer)
                .onMessage(AdaptedResponse.class, this::onTeamData)
                .build();
    }

    private Behavior<Task> onTeamTask(TaskMessages.TeamTask msg) {
        Optional<TaskUtils.TaskUpdate> taskUpdate = TaskUtils.handleTask(
                msg,
                Optional.ofNullable(teamTaskPlans.get(msg.taskId())),
                getContext().getLog(),
                this::estimateTaskFeasibility,
                this::createTeamTaskPlan);

        if (taskUpdate.isPresent()) {
            TaskUtils.TaskUpdate update = taskUpdate.get();
            switch (update.action()) {
                case ADD:
                    // Fall through
                case UPDATE:
                    update.teamTaskPlan().ifPresent(plan -> teamTaskPlans.put(plan.taskId(), plan));
                    break;
                case REMOVE:
                    update.teamTaskPlan().ifPresent(plan -> teamTaskPlans.remove(plan.taskId()));
                    break;
                case NONE:
                    // Fall through
                default:
                    // Nothing to do
                    break;
            }
            update.sendQueue().forEach(taskConnector::tell);

            if (msg.teamTaskMsg().getMessageState().getValue().equals(MessageState.MS_NEW)) {
                // Start timer for expiring task
                scheduleTaskTimer(new AbstractTaskBehavior.TaskTimer(msg.taskId()));
            }
        }

        return Behaviors.same();
    }

    private Behavior<Task> onPilotTaskStatus(TaskMessages.PilotTaskStatus msg) {
        if (! PilotTaskUtils.isValid(msg, teamTaskPlans)) {
            getContext().getLog()
                    .warn("Received unrecognized PilotTaskStatusMsg ({})",
                            msg.taskId());
            return Behaviors.same();
        }

        TeamTaskPlan plan = teamTaskPlans.getOrDefault(
                msg.taskId(),
                TeamTaskPlan.builder()
                        .taskId(msg.taskId())
                        .messageState(MessageState.UNRECOGNIZED)
                        .teamTaskDetails(TeamTaskDetails.newBuilder().build())
                        .build());
        TeamTaskPlan updated = PilotTaskUtils.updateTaskStatus(plan, msg);
        teamTaskPlans.put(plan.taskId(), updated);

        Optional<TaskStatusUtils.StatusUpdate> statusUpdate
                = TaskStatusUtils.handleTaskStatus(msg.taskStatus(), plan, msg.classification());
        statusUpdate.ifPresent(this::applyStatusUpdate);

        return Behaviors.same();
    }

    private Behavior<Task> onFusionTaskStatus(TaskMessages.FusionTaskStatus msg) {
        if (!FusionTaskUtils.isValid(msg, teamTaskPlans)) {
            getContext().getLog()
                    .warn("Received unrecognized FusionTaskStatusMsg ({})",
                            msg.taskId());
            return Behaviors.same();
        }

        TeamTaskPlan plan = teamTaskPlans.getOrDefault(
                msg.taskId(),
                TeamTaskPlan.builder()
                        .taskId(msg.taskId())
                        .messageState(MessageState.UNRECOGNIZED)
                        .teamTaskDetails(TeamTaskDetails.newBuilder().build())
                        .build());
        TeamTaskPlan updated = FusionTaskUtils.updateTaskStatus(plan, msg);
        teamTaskPlans.put(plan.taskId(), updated);

        Optional<TaskStatusUtils.StatusUpdate> statusUpdate
                = TaskStatusUtils.handleTaskStatus(msg.taskStatus(), plan, msg.classification());
        statusUpdate.ifPresent(this::applyStatusUpdate);

        return Behaviors.same();
    }

    private Behavior<Task> onTaskTimer(TaskTimer msg) {
        // Check if the task given by msg.taskId() has a status
        if (!teamTaskPlans.containsKey(msg.taskId())) {
            return Behaviors.same();
        }

        // If no status, remove the task; else do nothing
        if (teamTaskPlans.get(msg.taskId()).teamTaskStatus() == null) {
            teamTaskPlans.remove(msg.taskId());
        }

        return Behaviors.same();
    }

    private Behavior<Task> onTeamData(AdaptedResponse msg) {
        //TODO: Why are we requesting this? What should we do with it?
        msg.teamData().teamId();
        return Behaviors.same();
    }

    private void scheduleTaskTimer(TaskTimer msg) {
        timers.startSingleTimer(msg.taskId(), msg, TASK_EXPIRATION);
    }

    private void applyStatusUpdate(TaskStatusUtils.StatusUpdate statusUpdate) {
        teamTaskPlans.put(statusUpdate.teamTaskPlan().taskId(), statusUpdate.teamTaskPlan());
        statusUpdate.sendQueue().forEach(taskConnector::tell);
    }

    private TeamEvents.TeamDataRequest buildTeamDataRequest() {
        return TeamEvents.TeamDataRequest.builder()
                .teamId(TeamId.UNKNOWN)
                .replyTo(teamDataAdapter)
                .build();
    }

}
