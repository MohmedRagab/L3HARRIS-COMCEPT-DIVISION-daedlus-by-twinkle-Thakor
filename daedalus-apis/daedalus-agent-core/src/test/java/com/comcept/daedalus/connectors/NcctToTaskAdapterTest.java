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

import com.comcept.daedalus.api.msg.Daedalus;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.task.TaskInternal;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.msglib.FusionTaskMsg;
import com.comcept.daedalus.msglib.FusionTaskStatusMsg;
import com.comcept.daedalus.msglib.MessageState;
import com.comcept.daedalus.msglib.MessageStateValue;
import com.comcept.daedalus.msglib.PilotTaskCommandMsg;
import com.comcept.daedalus.msglib.PilotTaskMsg;
import com.comcept.daedalus.msglib.PilotTaskStatusMsg;
import com.comcept.daedalus.msglib.TaskCommandType;
import com.comcept.daedalus.msglib.TaskCommandTypeValue;
import com.comcept.daedalus.msglib.TaskStatusType;
import com.comcept.daedalus.msglib.TaskStatusTypeValue;
import com.comcept.daedalus.msglib.TeamTaskDetails;
import com.comcept.daedalus.msglib.TeamTaskMsg;
import com.comcept.daedalus.msglib.TeamTaskStateType;
import com.comcept.daedalus.msglib.TeamTaskStateTypeValue;
import com.comcept.daedalus.msglib.TeamTaskStatusMsg;
import com.comcept.daedalus.msglib.TeamTaskType;
import com.comcept.daedalus.msglib.TeamTaskTypeValue;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.common.MessageId;
import com.comcept.ncct.typed.api.common.SourceId;
import com.comcept.ncct.typed.mapper.utils.MapperUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Message;
import comcept.ncct.msglib.CatalogType;
import comcept.ncct.msglib.CatalogTypeValue;
import comcept.ncct.msglib.GenericPayloadMsg;
import comcept.ncct.msglib.MessageInfo;
import comcept.ncct.msglib.NcctMessageType;
import comcept.ncct.msglib.PingInitiateMsg;
import java.time.Instant;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NcctToTaskAdapterTest {

    @Test
    public void createTeamTaskFrom() {
        TeamTaskMsg teamTaskMsg = TeamTaskMsg.newBuilder()
                .setTaskDetails(TeamTaskDetails.newBuilder()
                        .setTaskType(TeamTaskTypeValue.newBuilder()
                                .setValue(TeamTaskType.TTT_ACQUIRE_TARGET)
                                .build())
                        .build())
                .build();

        Task event = convert(
                teamTaskMsg,
                MapperUtils.unmapMsgType(Daedalus.TEAM_TASK_TEAM(TeamId.of(1L))),
                NcctToTaskAdapter::from);

        assertTrue(TaskMessages.TeamTask.class.isAssignableFrom(event.getClass()));
        assertEquals(teamTaskMsg, TaskMessages.TeamTask.class.cast(event).teamTaskMsg());
    }

    @Test
    public void createTeamTaskStatusFrom() {
        TeamTaskStatusMsg teamTaskStatusMsg = TeamTaskStatusMsg.newBuilder()
                .setTeamTaskStatus(TaskStatusTypeValue.newBuilder()
                        .setValue(TaskStatusType.TST_ACCEPTED)
                        .build())
                .setTeamTaskState(TeamTaskStateTypeValue.newBuilder()
                        .setValue(TeamTaskStateType.TTST_ACQUIRING_TARGET)
                        .build())
                .build();

        Task event = convert(
                teamTaskStatusMsg,
                MapperUtils.unmapMsgType(Daedalus.TEAM_TASK_STATUS_TEAM(TeamId.of(1L))),
                NcctToTaskAdapter::from);

        assertTrue(TaskMessages.TeamTaskStatus.class.isAssignableFrom(event.getClass()));
        assertEquals(teamTaskStatusMsg,
                TaskMessages.TeamTaskStatus.class.cast(event).teamTaskStatusMsg());
    }

    @Test
    public void createFusionTaskFrom() {
        FusionTaskMsg fusionTaskMsg = FusionTaskMsg.newBuilder()
                .setMessageState(MessageStateValue.newBuilder()
                        .setValue(MessageState.MS_NEW)
                        .build())
                .build();

        Task event = convert(
                fusionTaskMsg,
                MapperUtils.unmapMsgType(Daedalus.FUSION_TASK_TEAM(TeamId.of(1L))),
                NcctToTaskAdapter::from);

        assertTrue(TaskMessages.FusionTask.class.isAssignableFrom(event.getClass()));
        assertEquals(fusionTaskMsg, TaskMessages.FusionTask.class.cast(event).fusionTaskMsg());
    }

    @Test
    public void createFusionTaskStatusFrom() {
        FusionTaskStatusMsg fusionTaskStatusMsg = FusionTaskStatusMsg.newBuilder()
                .setFusionTaskStatus(TaskStatusTypeValue.newBuilder()
                        .setValue(TaskStatusType.TST_ACCEPTED)
                        .build())
                .build();

        Task event = convert(
                fusionTaskStatusMsg,
                MapperUtils.unmapMsgType(Daedalus.FUSION_TASK_STATUS_TEAM(TeamId.of(1L))),
                NcctToTaskAdapter::from);

        assertTrue(TaskMessages.FusionTaskStatus.class.isAssignableFrom(event.getClass()));
        assertEquals(fusionTaskStatusMsg,
                TaskMessages.FusionTaskStatus.class.cast(event).fusionTaskStatusMsg());
    }

    @Test
    public void createPilotTaskFrom() {
        PilotTaskMsg pilotTaskMsg = PilotTaskMsg.newBuilder()
                .setMessageState(MessageStateValue.newBuilder()
                        .setValue(MessageState.MS_NEW)
                        .build())
                .build();

        Task event = convert(
                pilotTaskMsg,
                MapperUtils.unmapMsgType(Daedalus.PILOT_TASK_TEAM(TeamId.of(1L))),
                NcctToTaskAdapter::from);

        assertTrue(TaskMessages.PilotTask.class.isAssignableFrom(event.getClass()));
        assertEquals(pilotTaskMsg, TaskMessages.PilotTask.class.cast(event).pilotTaskMsg());
    }

    @Test
    public void createPilotTaskCommandFrom() {
        PilotTaskCommandMsg pilotTaskCommandMsg = PilotTaskCommandMsg.newBuilder()
                .setCommandType(TaskCommandTypeValue.newBuilder()
                        .setValue(TaskCommandType.TCT_DESIGNATE_TARGET)
                        .build())
                .build();

        Task event = convert(
                pilotTaskCommandMsg,
                MapperUtils.unmapMsgType(Daedalus.PILOT_TASK_COMMAND_TEAM(TeamId.of(1L))),
                NcctToTaskAdapter::from);

        assertTrue(TaskMessages.PilotTaskCommand.class.isAssignableFrom(event.getClass()));
        assertEquals(pilotTaskCommandMsg,
                TaskMessages.PilotTaskCommand.class.cast(event).pilotTaskCommandMsg());
    }

    @Test
    public void createPilotTaskStatusFrom() {
        PilotTaskStatusMsg pilotTaskStatusMsg = PilotTaskStatusMsg.newBuilder()
                .setPilotTaskStatus(TaskStatusTypeValue.newBuilder()
                        .setValue(TaskStatusType.TST_ACCEPTED)
                        .build())
                .build();

        Task event = convert(
                pilotTaskStatusMsg,
                MapperUtils.unmapMsgType(Daedalus.PILOT_TASK_STATUS_TEAM(TeamId.of(1L))),
                NcctToTaskAdapter::from);

        assertTrue(TaskMessages.PilotTaskStatus.class.isAssignableFrom(event.getClass()));
        assertEquals(pilotTaskStatusMsg,
                TaskMessages.PilotTaskStatus.class.cast(event).pilotTaskStatusMsg());
    }

    @Test
    public void testUnknownFrom() {
        PingInitiateMsg pingInitiateMsg = PingInitiateMsg.newBuilder()
                .build();

        Task event = convert(
                pingInitiateMsg,
                MessageInfo.newBuilder()
                        .setCatalog(CatalogTypeValue.newBuilder()
                                .setValue(CatalogType.CT_NCCT_5)
                                .build())
                        .setMessageType(Int32Value.of(NcctMessageType.NMST_PingInitiate_VALUE))
                        .setMessageSubtype(0L)
                        .build(),
                NcctToTaskAdapter::from);

        assertTrue(TaskInternal.ParseError.class.isAssignableFrom(event.getClass()));
    }

    @Test
    public void testBadMsgFrom() {
        Task event = convertBadMsg(
                MapperUtils.unmapMsgType(Daedalus.TEAM_TASK_TEAM(TeamId.of(1L))),
                NcctToTaskAdapter::from);

        assertTrue(TaskInternal.ParseError.class.isAssignableFrom(event.getClass()));
    }

    private <T extends Message, U extends Task> U convert(
            T sourceMsg,
            MessageInfo messageInfo,
            Function<GenericPayload, U> fn) {

        GenericPayloadMsg genericPayloadMsg = GenericPayloadMsg.newBuilder()
                .setPayload(BytesValue.of(sourceMsg.toByteString()))
                .build();

        byte[] payload = new byte[genericPayloadMsg.getPayload().getValue().size()];
        genericPayloadMsg.getPayload().getValue().copyTo(payload, 0);

        GenericPayload genericPayload = GenericPayload.builder()
                .classification(com.comcept.ncct.typed.api.common.SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.now())
                .msgType(MapperUtils.mapMsgType(messageInfo, true))
                .payload(payload)
                .build();

        return fn.apply(genericPayload);
    }

    private <U extends Task> U convertBadMsg(
            MessageInfo messageInfo,
            Function<GenericPayload, U> fn) {

        GenericPayloadMsg genericPayloadMsg = GenericPayloadMsg.newBuilder()
                .setPayload(BytesValue.of(ByteString.copyFrom(new byte[] { 0x10, 0x20, 0x40})))
                .build();

        byte[] payload = new byte[genericPayloadMsg.getPayload().getValue().size()];
        genericPayloadMsg.getPayload().getValue().copyTo(payload, 0);

        GenericPayload genericPayload = GenericPayload.builder()
                .classification(com.comcept.ncct.typed.api.common.SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.now())
                .msgType(MapperUtils.mapMsgType(messageInfo, true))
                .payload(payload)
                .build();

        return fn.apply(genericPayload);
    }

}
