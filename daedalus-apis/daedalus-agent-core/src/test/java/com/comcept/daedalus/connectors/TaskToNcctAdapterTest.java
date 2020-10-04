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
package com.comcept.daedalus.connectors;

import com.comcept.daedalus.api.task.TaskId;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.msglib.FusionTaskMsg;
import com.comcept.daedalus.msglib.FusionTaskStatusMsg;
import com.comcept.daedalus.msglib.PilotTaskCommandMsg;
import com.comcept.daedalus.msglib.PilotTaskMsg;
import com.comcept.daedalus.msglib.PilotTaskStatusMsg;
import com.comcept.daedalus.msglib.TaskStatusType;
import com.comcept.daedalus.msglib.TeamTaskMsg;
import com.comcept.daedalus.msglib.TeamTaskStatusMsg;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.common.MessageId;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SecurityClassification;
import com.comcept.ncct.typed.api.common.SourceId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TaskToNcctAdapterTest {

    @Test
    public void testCreateFromTeamTask() {
        TaskMessages.TeamTask teamTask = TaskMessages.TeamTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 1))
                .teamTaskMsg(TeamTaskMsg.newBuilder().build())
                .build();

        GenericPayload event = TaskToNcctAdapter.from(teamTask);

        assertArrayEquals(teamTask.teamTaskMsg().toByteArray(), event.payload());
    }

    @Test
    public void testCreateFromTeamTaskStatus() {
        TaskMessages.TeamTaskStatus teamTaskStatus = TaskMessages.TeamTaskStatus.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .teamTaskStatusMsg(TeamTaskStatusMsg.newBuilder().build())
                .taskStatus(TaskStatusType.TST_ACCEPTED)
                .taskId(TaskId.of(1, 1))
                .build();

        GenericPayload event = TaskToNcctAdapter.from(teamTaskStatus);

        assertArrayEquals(teamTaskStatus.teamTaskStatusMsg().toByteArray(), event.payload());
    }

    @Test
    public void testCreateFromFusionTask() {
        TaskMessages.FusionTask fusionTask = TaskMessages.FusionTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .fusionTaskMsg(FusionTaskMsg.newBuilder().build())
                .build();

        GenericPayload event = TaskToNcctAdapter.from(fusionTask);

        assertArrayEquals(fusionTask.fusionTaskMsg().toByteArray(), event.payload());
    }

    @Test
    public void testCreateFromFusionTaskStatus() {
        TaskMessages.FusionTaskStatus fusionTaskStatus = TaskMessages.FusionTaskStatus.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .taskStatus(TaskStatusType.TST_ACCEPTED)
                .fusionTaskStatusMsg(FusionTaskStatusMsg.newBuilder().build())
                .build();

        GenericPayload event = TaskToNcctAdapter.from(fusionTaskStatus);

        assertArrayEquals(fusionTaskStatus.fusionTaskStatusMsg().toByteArray(), event.payload());
    }

    @Test
    public void testCreateFromPilotTask() {
        TaskMessages.PilotTask pilotTask = TaskMessages.PilotTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .pilotTaskMsg(PilotTaskMsg.newBuilder().build())
                .build();

        GenericPayload event = TaskToNcctAdapter.from(pilotTask);

        assertArrayEquals(pilotTask.pilotTaskMsg().toByteArray(), event.payload());
    }

    @Test
    public void testCreateFromPilotTaskCommand() {
        TaskMessages.PilotTaskCommand pilotTaskCommand = TaskMessages.PilotTaskCommand.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 1))
                .pilotTaskCommandMsg(PilotTaskCommandMsg.newBuilder().build())
                .build();

        GenericPayload event = TaskToNcctAdapter.from(pilotTaskCommand);

        assertArrayEquals(pilotTaskCommand.pilotTaskCommandMsg().toByteArray(), event.payload());
    }

    @Test
    public void testCreateFromPilotTaskStatus() {
        TaskMessages.PilotTaskStatus pilotTaskStatus = TaskMessages.PilotTaskStatus.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .taskStatus(TaskStatusType.TST_ACCEPTED)
                .pilotTaskStatusMsg(PilotTaskStatusMsg.newBuilder().build())
                .build();

        GenericPayload event = TaskToNcctAdapter.from(pilotTaskStatus);

        assertArrayEquals(pilotTaskStatus.pilotTaskStatusMsg().toByteArray(), event.payload());
    }

}
