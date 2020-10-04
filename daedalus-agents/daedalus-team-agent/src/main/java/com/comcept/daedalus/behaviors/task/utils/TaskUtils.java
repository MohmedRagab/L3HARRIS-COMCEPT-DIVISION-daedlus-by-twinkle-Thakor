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
package com.comcept.daedalus.behaviors.task.utils;

import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.behaviors.task.data.FusionTaskPlan;
import com.comcept.daedalus.behaviors.task.data.PilotTaskPlan;
import com.comcept.daedalus.behaviors.task.data.TeamTaskPlan;
import com.comcept.daedalus.msglib.TeamTaskMsg;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;

public class TaskUtils {

    private TaskUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static class TaskUpdate {
        public enum Action {
            ADD, UPDATE, REMOVE, NONE
        }

        private final Optional<TeamTaskPlan> teamTaskPlan;
        private final Action action;
        private final List<Task> sendQueue = new ArrayList<>();

        public TaskUpdate(Action action) {
            this.teamTaskPlan = Optional.empty();
            this.action = action;
        }

        public TaskUpdate(TeamTaskPlan teamTaskPlan, Action action) {
            this.teamTaskPlan = Optional.of(teamTaskPlan);
            this.action = action;
        }

        public void addToQueue(Task msg) {
            sendQueue.add(msg);
        }

        public void addAllToQueue(List<Task> msgs) {
            sendQueue.addAll(msgs);
        }

        public Optional<TeamTaskPlan> teamTaskPlan() {
            return teamTaskPlan;
        }

        public Action action() {
            return action;
        }

        public List<Task> sendQueue() {
            return sendQueue;
        }

    }

    /**
     * Handle incoming task.
     *
     * @param msg Incoming task
     * @param teamTaskPlan Associated plan (if available)
     * @param logger Logger
     * @param estimateTaskFeasibility Function to estimate feasability
     * @param createTeamTaskPlan Function to create a new team task plan from a task
     * @return
     */
    public static Optional<TaskUpdate> handleTask(
            TaskMessages.TeamTask msg,
            Optional<TeamTaskPlan> teamTaskPlan,
            Logger logger,
            Function<TaskMessages.TeamTask, TaskMessages.TeamTaskStatus> estimateTaskFeasibility,
            Function<TaskMessages.TeamTask, Optional<TeamTaskPlan>> createTeamTaskPlan) {

        Optional<TaskUpdate> updated = Optional.empty();

        TeamTaskMsg teamTaskMsg = msg.teamTaskMsg();
        switch (teamTaskMsg.getMessageState().getValue()) {
            case MS_NEW:
                updated = Optional.of(
                        createTask(msg, logger, estimateTaskFeasibility, createTeamTaskPlan)
                );
                break;
            case MS_UPDATE:
                updated = Optional.of(updateTask(msg, teamTaskPlan, createTeamTaskPlan));
                break;
            case MS_REMOVE:
                updated = Optional.of(removeTask(teamTaskPlan));
                break;
            default:
                logger.warn("Received TeamTaskMsg ({}) with invalid message state {}",
                                msg.taskId(), teamTaskMsg.getMessageState().getValue());
        }

        return updated;
    }

    private static TaskUpdate createTask(
            TaskMessages.TeamTask msg,
            Logger logger,
            Function<TaskMessages.TeamTask, TaskMessages.TeamTaskStatus> estimateTaskFeasibility,
            Function<TaskMessages.TeamTask, Optional<TeamTaskPlan>> createTeamTaskPlan) {

        TaskUpdate taskUpdate = new TaskUpdate(TaskUpdate.Action.NONE);

        TaskMessages.TeamTaskStatus teamTaskStatus = estimateTaskFeasibility.apply(msg);

        switch (teamTaskStatus.taskStatus()) {
            case TST_ASSESSING:
                logger.info("Assessing TeamTaskMsg ({})", msg.taskId());

                Optional<TeamTaskPlan> teamTaskPlan = createTeamTaskPlan.apply(msg);
                if (teamTaskPlan.isPresent()) {
                    taskUpdate = new TaskUpdate(teamTaskPlan.get(), TaskUpdate.Action.ADD);
                    taskUpdate.addAllToQueue(sendTeamTaskPlans(teamTaskPlan.get()));
                }
                break;
            case TST_REJECT:
                logger.info("Rejected TeamTaskMsg ({})", msg.taskId());
                break;
            default:
        }

        taskUpdate.addToQueue(teamTaskStatus);

        return taskUpdate;
    }

    private static TaskUpdate updateTask(
            TaskMessages.TeamTask msg,
            Optional<TeamTaskPlan> teamTaskPlan,
            Function<TaskMessages.TeamTask, Optional<TeamTaskPlan>> createTeamTaskPlan) {

        Optional<TeamTaskPlan> updatedPlan = createTeamTaskPlan.apply(msg);

        TaskUpdate taskUpdate = new TaskUpdate(TaskUpdate.Action.NONE);
        if (updatedPlan.isPresent()) {
            taskUpdate = new TaskUpdate(updatedPlan.get(), TaskUpdate.Action.UPDATE);
        } else if (teamTaskPlan.isPresent()) {
            taskUpdate = new TaskUpdate(teamTaskPlan.get(), TaskUpdate.Action.NONE);
        }
        return taskUpdate;
    }

    private static TaskUpdate removeTask(Optional<TeamTaskPlan> teamTaskPlan) {

        TaskUpdate taskUpdate = new TaskUpdate(TaskUpdate.Action.NONE);
        if (teamTaskPlan.isPresent()) {
            taskUpdate = new TaskUpdate(teamTaskPlan.get(), TaskUpdate.Action.REMOVE);
            taskUpdate.addAllToQueue(sendTeamTaskPlans(teamTaskPlan.get()));
        }
        return taskUpdate;
    }

    private static List<Task> sendTeamTaskPlans(TeamTaskPlan msg) {
        List<Task> sendQueue = new ArrayList<>();

        for (PilotTaskPlan plan : msg.pilotTaskPlans().values()) {
            sendQueue.add(plan.pilotTask());
        }
        for (FusionTaskPlan plan : msg.fusionTaskPlans().values()) {
            sendQueue.add(plan.fusionTask());
        }

        return sendQueue;
    }
}
