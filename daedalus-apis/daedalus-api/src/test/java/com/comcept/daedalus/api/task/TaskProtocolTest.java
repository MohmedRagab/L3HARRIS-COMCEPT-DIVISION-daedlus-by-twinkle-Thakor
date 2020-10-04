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
package com.comcept.daedalus.api.task;

import com.comcept.daedalus.api.DaedalusApi;
import com.comcept.daedalus.msglib.FusionTaskMsg;
import com.comcept.daedalus.msglib.FusionTaskStatusMsg;
import com.comcept.daedalus.msglib.MessageState;
import com.comcept.daedalus.msglib.MessageStateValue;
import com.comcept.daedalus.msglib.PilotTaskCommandMsg;
import com.comcept.daedalus.msglib.PilotTaskMsg;
import com.comcept.daedalus.msglib.PilotTaskStatusMsg;
import com.comcept.daedalus.msglib.TaskStatusType;
import com.comcept.daedalus.msglib.TaskStatusTypeValue;
import com.comcept.daedalus.msglib.TeamTaskDetails;
import com.comcept.daedalus.msglib.TeamTaskMsg;
import com.comcept.daedalus.msglib.TeamTaskStateType;
import com.comcept.daedalus.msglib.TeamTaskStateTypeValue;
import com.comcept.daedalus.msglib.TeamTaskStatusMsg;
import com.comcept.daedalus.msglib.TeamTaskType;
import com.comcept.daedalus.msglib.TeamTaskTypeValue;
import com.comcept.ncct.typed.api.common.MessageId;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SecurityClassification;
import com.comcept.ncct.typed.api.common.SourceId;
import com.google.protobuf.Int32Value;
import comcept.ncct.msglib.NcctId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task Protocol verification.
 *
 * <p>The tests here are to exercise the API. Message construction and assignment
 * is enough to validate behavior. If there is a failure, the expectation is either
 * a compilation error or runtime exception.
 */
public class TaskProtocolTest {

    @Test
    public void testTaskInterface() {
        Task task = new Task() {};

        assertTrue(task instanceof DaedalusApi);
    }

    @Test
    public void testRegisterListener() {
        Task event = TaskEvents.RegisterListener.builder()
                .build();

        assertNotNull(event);
    }

    @Test
    public void testTeamTask() {
        Task event = TaskMessages.TeamTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("TOP SECRET")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 0))
                .teamTaskMsg(TeamTaskMsg.newBuilder()
                        .setMessageState(MessageStateValue.newBuilder()
                                .setValue(MessageState.MS_NEW)
                                .build())
                        .setTaskDetails(TeamTaskDetails.newBuilder()
                                .setTaskType(TeamTaskTypeValue.newBuilder()
                                        .setValue(TeamTaskType.TTT_ACQUIRE_TARGET)
                                        .build())
                                .build())
                        .build())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testTeamTaskStatus() {
        Task event = TaskMessages.TeamTaskStatus.builder()
                .classification(SecurityClassification.builder()
                        .capco("TOP SECRET")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 0))
                .taskStatus(TaskStatusType.TST_ACCEPTED)
                .teamTaskStatusMsg(TeamTaskStatusMsg.newBuilder()
                        .setTeamTaskId(NcctId.newBuilder()
                                .setNncId(Int32Value.of(1))
                                .setSubId(Int32Value.of(100))
                                .build())
                        .setTeamTaskState(TeamTaskStateTypeValue.newBuilder()
                                .setValue(TeamTaskStateType.TTST_ACQUIRING_TARGET)
                                .build())
                        .setTeamTaskStatus(TaskStatusTypeValue.newBuilder()
                                .setValue(TaskStatusType.TST_ACCEPTED)
                                .build())
                        .build())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testFusionTask() {
        Task event = TaskMessages.FusionTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("SECRET")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .fusionTaskMsg(FusionTaskMsg.newBuilder()
                        .build())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testFusionTaskStatus() {
        Task event = TaskMessages.FusionTaskStatus.builder()
                .classification(SecurityClassification.builder()
                        .capco("SECRET")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .taskStatus(TaskStatusType.TST_ACCEPTED)
                .fusionTaskStatusMsg(FusionTaskStatusMsg.newBuilder()
                        .build())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testPilotTask() {
        Task event = TaskMessages.PilotTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("SECRET")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .pilotTaskMsg(PilotTaskMsg.newBuilder()
                        .build())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testPilotTaskCommand() {
        Task event = TaskMessages.PilotTaskCommand.builder()
                .classification(SecurityClassification.builder()
                        .capco("SECRET")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 1))
                .pilotTaskCommandMsg(PilotTaskCommandMsg.newBuilder()
                        .build())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testPilotTaskStatus() {
        Task event = TaskMessages.PilotTaskStatus.builder()
                .classification(SecurityClassification.builder()
                        .capco("SECRET")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .taskStatus(TaskStatusType.TST_ACCEPTED)
                .pilotTaskStatusMsg(PilotTaskStatusMsg.newBuilder()
                        .build())
                .build();

        assertNotNull(event);
    }

}
