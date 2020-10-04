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
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.TimerScheduler;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.behaviors.task.data.TeamTaskPlan;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.daedalus.msglib.TaskStatusType;
import com.comcept.daedalus.msglib.TaskStatusTypeValue;
import com.comcept.daedalus.msglib.TeamTaskStatusMsg;
import com.comcept.daedalus.utils.TaskUtils;
import java.util.Optional;

public class TargetAcquisitionTaskBehavior extends AbstractTaskBehavior {

    /**
     * Create a TargetAcquisitionTaskBehavior.
     *
     * @param taskConnector Connector to send/receive task events
     * @param eventDepot Event buses
     * @return TargetAcquisitionTaskBehavior
     */
    public static Behavior<Task> create(
            ActorRef<Task> taskConnector,
            EventDepot eventDepot) {
        Behavior<Task> behavior =
                Behaviors.setup(context ->
                        Behaviors.withTimers(timers ->
                                new TargetAcquisitionTaskBehavior(
                                        context,
                                        timers,
                                        taskConnector,
                                        eventDepot)
                        )
                );

        return Behaviors.supervise(behavior)
                .onFailure(SupervisorStrategy.restart());
    }

    private TargetAcquisitionTaskBehavior(
            ActorContext<Task> context,
            TimerScheduler<Task> timers,
            ActorRef<Task> taskConnector,
            EventDepot eventDepot) {
        super(context, timers, taskConnector, eventDepot);
    }

    @Override
    protected TaskMessages.TeamTaskStatus estimateTaskFeasibility(TaskMessages.TeamTask msg) {
        TaskStatusTypeValue status = TaskStatusTypeValue.newBuilder()
                .setValue(TaskStatusType.TST_ASSESSING)
                .build();
        return TaskMessages.TeamTaskStatus.builder()
                .classification(msg.classification())
                .teamTaskStatusMsg(TeamTaskStatusMsg.newBuilder()
                        .setTeamTaskId(TaskUtils.toNcctId(msg.taskId()))
                        .setTeamTaskStatus(status)
                        .build())
                .taskId(msg.taskId())
                .taskStatus(status.getValue())
                .build();
    }

    @Override
    protected Optional<TeamTaskPlan> createTeamTaskPlan(TaskMessages.TeamTask msg) {
        return Optional.of(TeamTaskPlan.builder()
                .taskId(msg.taskId())
                .messageState(msg.teamTaskMsg().getMessageState().getValue())
                .teamTaskDetails(msg.teamTaskMsg().getTaskDetails())
                .build());
    }

}
