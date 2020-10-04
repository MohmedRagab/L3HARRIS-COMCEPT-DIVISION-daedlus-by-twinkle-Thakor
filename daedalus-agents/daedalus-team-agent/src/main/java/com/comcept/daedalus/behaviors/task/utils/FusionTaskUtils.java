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

import com.comcept.daedalus.api.task.TaskId;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.behaviors.task.data.FusionTaskPlan;
import com.comcept.daedalus.behaviors.task.data.TeamTaskPlan;
import com.comcept.daedalus.msglib.FusionTaskMsg;
import com.comcept.daedalus.msglib.TaskStatusType;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class FusionTaskUtils {

    private FusionTaskUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks if the fusion task is valid.
     *
     * @param msg Fusion task msg
     * @param teamTaskPlans Set of all task plans
     * @return True if the fusion task is in the set of tasks and has a valid status
     */
    public static boolean isValid(
            TaskMessages.FusionTaskStatus msg,
            Map<TaskId, TeamTaskPlan> teamTaskPlans) {

        boolean msgValid = (teamTaskPlans.containsKey(msg.taskId()));

        boolean statusValid = false;
        if (msgValid) {
            TaskStatusType fusionStatus = msg.fusionTaskStatusMsg()
                    .getFusionTaskStatus()
                    .getValue();
            statusValid = (fusionStatus != TaskStatusType.TST_ACCEPTED);
        }

        return msgValid && statusValid;
    }

    /**
     * Update task status.
     *
     * @param plan Overall task plan
     * @param msg Fusion task
     * @return Updated plan
     */
    public static TeamTaskPlan updateTaskStatus(
            TeamTaskPlan plan,
            TaskMessages.FusionTaskStatus msg) {

        FusionTaskPlan fusionTaskPlan = plan.fusionTaskPlans().getOrDefault(
                msg.platformId(),
                FusionTaskPlan.builder()
                        .fusionTask(TaskMessages.FusionTask.builder()
                                .taskId(msg.taskId())
                                .platformId(msg.platformId())
                                .classification(msg.classification())
                                .fusionTaskMsg(FusionTaskMsg.newBuilder().build())
                                .build())
                        .build());

        Map<PlatformId, FusionTaskPlan> fusionPlans =
                new ImmutableMap.Builder<PlatformId, FusionTaskPlan>()
                        .putAll(plan.fusionTaskPlans())
                        .put(msg.platformId(),
                                fusionTaskPlan.toBuilder()
                                        .fusionTaskStatus(msg)
                        .build())
                        .build();

        return plan.toBuilder()
                .fusionTaskPlans(fusionPlans)
                .build();
    }

    /**
     * Checks if all plans have reached a given status.
     *
     * @param plans Set of all fusion task plans
     * @param status Desired status
     * @return True if all task plans have that status
     */
    public static boolean allPlansHaveStatus(
            Collection<FusionTaskPlan> plans,
            TaskStatusType status) {

        Collection<FusionTaskPlan> filtered = plans.stream()
                .filter(p -> !p.fusionTaskStatus().taskStatus().equals(status))
                .collect(Collectors.toList());
        return filtered.isEmpty();
    }

}
