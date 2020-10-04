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
import com.comcept.daedalus.behaviors.task.data.TeamTaskPlan;
import com.comcept.daedalus.msglib.PilotTaskCommandMsg;
import com.comcept.daedalus.msglib.TaskCommandType;
import com.comcept.daedalus.msglib.TaskCommandTypeValue;
import com.comcept.daedalus.msglib.TaskStatusType;
import com.comcept.daedalus.msglib.TaskStatusTypeValue;
import com.comcept.daedalus.msglib.TeamTaskStateTypeValue;
import com.comcept.daedalus.msglib.TeamTaskStatusMsg;
import com.comcept.ncct.typed.api.common.SecurityClassification;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskStatusUtils {

    private TaskStatusUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static class StatusUpdate {
        private final TeamTaskPlan teamTaskPlan;
        private final List<Task> sendQueue = new ArrayList<>();

        public StatusUpdate(TeamTaskPlan teamTaskPlan) {
            this.teamTaskPlan = teamTaskPlan;
        }

        public void addToQueue(Task msg) {
            sendQueue.add(msg);
        }

        public void addAllToQueue(List<Task> msgs) {
            sendQueue.addAll(msgs);
        }

        public TeamTaskPlan teamTaskPlan() {
            return teamTaskPlan;
        }

        public List<Task> sendQueue() {
            return sendQueue;
        }

    }

    /**
     * Handle incoming task status.
     *
     * @param status Incoming task status
     * @param plan Associated task plan
     * @param classification Classification level
     * @return Updates to the status if available
     */
    public static Optional<StatusUpdate> handleTaskStatus(
            TaskStatusType status,
            TeamTaskPlan plan,
            SecurityClassification classification) {

        Optional<StatusUpdate> updated = Optional.empty();
        switch (status) {
            case TST_ACCEPTED:
                updated = Optional.of(handleAssignedTaskAccepted(plan, classification));
                break;
            case TST_REJECT:
                // TODO (FUTURE): Re-plan based on pilot task status msg details
                break;
            case TST_EXECUTING:
                // If all assigned tasks are being executed, report the team task as such
                updated = Optional.of(handleAssignedTaskExecuting(plan, classification));
                break;
            case TST_FAILED:
                // TODO (FUTURE): Reassess and possibly re-plan or fail the task
                break;
            case TST_CANCELLED:
                updated = Optional.of(handleAssignedTaskCancelled(plan, classification));
                break;
            default:
                // Nothing to do
                break;
        }

        return updated;
    }

    private static StatusUpdate handleAssignedTaskAccepted(
            TeamTaskPlan plan,
            SecurityClassification classification) {

        StatusUpdate statusUpdate = new StatusUpdate(plan);

        // Fusion tasks don't have an ACCEPTED state - they move directly to EXECUTING
        if (PilotTaskUtils.allPlansHaveStatus(
                plan.pilotTaskPlans().values(),
                TaskStatusType.TST_ACCEPTED)) {

            statusUpdate.sendQueue.add(buildPilotTaskCommand(plan, classification));
        }

        return statusUpdate;
    }

    private static StatusUpdate handleAssignedTaskExecuting(
            TeamTaskPlan plan,
            SecurityClassification classification) {

        StatusUpdate statusUpdate = new StatusUpdate(plan);

        if (PilotTaskUtils.allPlansHaveStatus(
                plan.pilotTaskPlans().values(),
                TaskStatusType.TST_EXECUTING)
                && FusionTaskUtils.allPlansHaveStatus(
                plan.fusionTaskPlans().values(),
                TaskStatusType.TST_EXECUTING)) {

            statusUpdate = new StatusUpdate(plan.toBuilder()
                    .teamTaskStatus(TaskStatusType.TST_EXECUTING)
            .build());
            statusUpdate.addToQueue(buildTeamTaskStatus(
                    statusUpdate.teamTaskPlan,
                    TaskStatusType.TST_EXECUTING,
                    classification));
        }

        return statusUpdate;
    }


    private static StatusUpdate handleAssignedTaskCancelled(
            TeamTaskPlan plan,
            SecurityClassification classification) {

        StatusUpdate statusUpdate = new StatusUpdate(plan);

        if (PilotTaskUtils.allPlansHaveStatus(
                plan.pilotTaskPlans().values(),
                TaskStatusType.TST_CANCELLED)
                && FusionTaskUtils.allPlansHaveStatus(
                plan.fusionTaskPlans().values(),
                TaskStatusType.TST_CANCELLED)) {

            statusUpdate = new StatusUpdate(plan.toBuilder()
                    .teamTaskStatus(TaskStatusType.TST_CANCELLED)
            .build());
            statusUpdate.addToQueue(buildTeamTaskStatus(
                    statusUpdate.teamTaskPlan,
                    TaskStatusType.TST_CANCELLED,
                    classification));
        }

        return statusUpdate;
    }

    private static TaskMessages.PilotTaskCommand buildPilotTaskCommand(
            TeamTaskPlan plan,
            SecurityClassification classification) {

        return TaskMessages.PilotTaskCommand.builder()
                .classification(classification)
                .pilotTaskCommandMsg(PilotTaskCommandMsg.newBuilder()
                        .setCommandType(TaskCommandTypeValue.newBuilder()
                                .setValue(TaskCommandType.TCT_EXECUTE)
                                .build())
                        .build())
                .taskId(plan.taskId())
                .build();
    }

    private static TaskMessages.TeamTaskStatus buildTeamTaskStatus(
            TeamTaskPlan plan,
            TaskStatusType status,
            SecurityClassification classification) {

        TeamTaskStatusMsg.Builder teamTaskStatusMsgBuilder = TeamTaskStatusMsg.newBuilder()
                .setTeamTaskStatus(TaskStatusTypeValue.newBuilder()
                        .setValue(status)
                        .build());
        if (plan.teamTaskState() != null) {
            teamTaskStatusMsgBuilder
                    .setTeamTaskState(TeamTaskStateTypeValue.newBuilder()
                            .setValue(plan.teamTaskState())
                            .build());
        }
        if (plan.teamTaskStatusDetails() != null) {
            teamTaskStatusMsgBuilder.setTeamTaskStatusDetails(plan.teamTaskStatusDetails());
        }
        return TaskMessages.TeamTaskStatus.builder()
                .taskId(plan.taskId())
                .taskStatus(status)
                .classification(classification)
                .teamTaskStatusMsg(teamTaskStatusMsgBuilder.build())
                .build();
    }

}
