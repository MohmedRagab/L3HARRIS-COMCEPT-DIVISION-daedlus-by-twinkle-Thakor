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
import com.comcept.daedalus.behaviors.task.data.PilotTaskPlan;
import com.comcept.daedalus.behaviors.task.data.TeamTaskPlan;
import com.comcept.daedalus.msglib.PilotTaskMsg;
import com.comcept.daedalus.msglib.TaskStatusType;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class PilotTaskUtils {

    private PilotTaskUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks if the pilot task is valid.
     *
     * @param msg Pilot task msg
     * @param teamTaskPlans Set of all task plans
     * @return True if the pilot task is in the set of tasks and has a valid status
     */
    public static boolean isValid(
            TaskMessages.PilotTaskStatus msg,
            Map<TaskId, TeamTaskPlan> teamTaskPlans) {
        return (teamTaskPlans.containsKey(msg.taskId()));
    }

    /**
     * Update task status.
     *
     * @param plan Overall task plan
     * @param msg Pilot task
     * @return Updated plan
     */
    public static TeamTaskPlan updateTaskStatus(
            TeamTaskPlan plan,
            TaskMessages.PilotTaskStatus msg) {

        PilotTaskPlan pilotTaskPlan = plan.pilotTaskPlans().getOrDefault(
                msg.platformId(),
                PilotTaskPlan.builder()
                        .pilotTask(TaskMessages.PilotTask.builder()
                                .taskId(msg.taskId())
                                .platformId(msg.platformId())
                                .classification(msg.classification())
                                .pilotTaskMsg(PilotTaskMsg.newBuilder().build())
                                .build())
                        .build());

        Map<PlatformId, PilotTaskPlan> pilotPlans =
                new ImmutableMap.Builder<PlatformId, PilotTaskPlan>()
                        .putAll(plan.pilotTaskPlans())
                        .put(msg.platformId(),
                                pilotTaskPlan.toBuilder()
                                        .pilotTaskStatus(msg)
                        .build())
                        .build();

        return plan.toBuilder()
                .pilotTaskPlans(pilotPlans)
                .build();
    }

    /**
     * Checks if all plans have reached a given status.
     *
     * @param plans Set of all pilot task plans
     * @param status Desired status
     * @return True if all task plans have that status
     */
    public static boolean allPlansHaveStatus(
            Collection<PilotTaskPlan> plans,
            TaskStatusType status) {

        Collection<PilotTaskPlan> filtered = plans.stream()
                .filter(p -> !p.pilotTaskStatus().taskStatus().equals(status))
                .collect(Collectors.toList());
        return filtered.isEmpty();
    }

}
