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
package com.comcept.daedalus.behaviors.task.data;

import com.comcept.daedalus.api.task.TaskId;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.msglib.MessageState;
import com.comcept.daedalus.msglib.PilotTaskMsg;
import com.comcept.daedalus.msglib.TaskStatusType;
import com.comcept.daedalus.msglib.TeamTaskDetails;
import com.comcept.daedalus.msglib.TeamTaskStateType;
import com.comcept.daedalus.msglib.TeamTaskStatusDetails;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SecurityClassification;
import org.junit.jupiter.api.Test;

public class TeamTaskPlanTest {

    @Test
    public void testCheckCurrentPilotPlanStatus() {
        TeamTaskPlan teamTaskPlan = TeamTaskPlan.builder()
                .taskId(TaskId.of(1, 1))
                .messageState(MessageState.MS_NEW)
                .teamTaskDetails(TeamTaskDetails.newBuilder().build())
                .teamTaskState(TeamTaskStateType.TTST_ACQUIRING_TARGET)
                .teamTaskStatus(TaskStatusType.TST_ACCEPTED)
                .teamTaskStatusDetails(TeamTaskStatusDetails.newBuilder().build())
                .build();

        // TODO: Empty pilot plan should return false
        //assertFalse(teamTaskPlan.checkCurrentPilotPlanStatus(TaskStatusType.TST_ACCEPTED));

        TaskMessages.PilotTask pilotTask = TaskMessages.PilotTask.builder()
                .platformId(PlatformId.of(1, 1))
                .taskId(TaskId.of(1, 1))
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .pilotTaskMsg(PilotTaskMsg.newBuilder().build())
                .build();

        //assertTrue(teamTaskPlan.checkCurrentPilotPlanStatus(TaskStatusType.TST_ACCEPTED));
        //assertFalse(teamTaskPlan.checkCurrentPilotPlanStatus(TaskStatusType.TST_ASSESSING));
    }

}
