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

import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.daedalus.api.msg.Daedalus;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.api.team.TeamInternal;
import com.comcept.daedalus.api.team.TeamMessages;
import com.comcept.daedalus.msglib.ResponseType;
import com.comcept.daedalus.msglib.ResponseTypeValue;
import com.comcept.daedalus.msglib.TeamAssignmentDetails;
import com.comcept.daedalus.msglib.TeamAssignmentMsg;
import com.comcept.daedalus.msglib.TeamInvitationMsg;
import com.comcept.daedalus.msglib.TeamInvitationResponseMsg;
import com.comcept.daedalus.msglib.TeamRegistrationMsg;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.typed.api.PlatformPosition;
import com.comcept.ncct.typed.api.common.GeoPoint;
import com.comcept.ncct.typed.api.common.Identification;
import com.comcept.ncct.typed.api.common.MessageId;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SourceId;
import com.comcept.ncct.typed.mapper.utils.MapperUtils;
import com.comcept.ncct.typed.mapper.utils.NcctIdUtils;
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

public class NcctToTeamAdapterTest {

    @Test
    public void testCreatePlatformPositionFrom() {
        PlatformPosition platformPosition = PlatformPosition.builder()
                .classification(com.comcept.ncct.typed.api.common.SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.ofEpochSecond(1234L))
                .platformId(PlatformId.of(1, 1))
                .location(GeoPoint.builder()
                        .latitude(12.0)
                        .longitude(98.1)
                        .build())
                .identification(Identification.builder()
                        .build())
                .validUntil(Instant.EPOCH)
                .build();

        DaedalusMessages.TeamPlatformPosition teamPlatformPosition = DaedalusMessages.TeamPlatformPosition.builder()
                .platformPosition(platformPosition)
                .build();

        Team event = NcctToTeamAdapter.from(platformPosition);

        assertTrue(event instanceof DaedalusMessages.TeamPlatformPosition);
        assertEquals(teamPlatformPosition, (DaedalusMessages.TeamPlatformPosition) event);
    }

    @Test
    public void testCreatePlatformConfigurationFrom() {
        PlatformConfiguration platformConfiguration = PlatformConfiguration.builder()
                .classification(com.comcept.ncct.typed.api.common.SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.ofEpochSecond(1234L))
                .platformId(PlatformId.of(1, 1))
                .subIdMin(100)
                .subIdMax(100)
                .build();

        DaedalusMessages.TeamPlatformConfiguration teamPlatformConfiguration
                = DaedalusMessages.TeamPlatformConfiguration.builder()
                .platformConfiguration(platformConfiguration)
                .build();

        Team event = NcctToTeamAdapter.from(platformConfiguration);

        assertTrue(event instanceof DaedalusMessages.TeamPlatformConfiguration);
        assertEquals(teamPlatformConfiguration, (DaedalusMessages.TeamPlatformConfiguration) event);
    }

    @Test
    public void testCreateTeamAssignmentFrom() {
        TeamAssignmentMsg teamAssignmentMsg = TeamAssignmentMsg.newBuilder()
                .setDetails(TeamAssignmentDetails.newBuilder().build())
                .build();

        Team event = convert(
                teamAssignmentMsg,
                MapperUtils.unmapMsgType(Daedalus.TEAM_ASSIGNMENT_TEAM(TeamId.of(1L))),
                NcctToTeamAdapter::from);

        assertTrue(event instanceof TeamMessages.TeamAssignment);;
        assertEquals(teamAssignmentMsg, ((TeamMessages.TeamAssignment) event).teamAssignmentMsg());
    }

    @Test
    public void createTeamInvitationFrom() {
        TeamInvitationMsg teamInvitationMsg = TeamInvitationMsg.newBuilder()
                .setLeaderId(NcctIdUtils.toNcctId(PlatformId.UNKNOWN))
                .build();

        Team event = convert(
                teamInvitationMsg,
                MapperUtils.unmapMsgType(Daedalus.TEAM_INVITATION_TEAM(TeamId.of(1L))),
                NcctToTeamAdapter::from);

        assertTrue(event instanceof TeamMessages.TeamInvitation);;
        assertEquals(teamInvitationMsg, ((TeamMessages.TeamInvitation) event).teamInvitationMsg());
    }

    @Test
    public void createTeamInvitiationResponseFrom() {
        TeamInvitationResponseMsg teamInvitationResponseMsg
                = TeamInvitationResponseMsg.newBuilder()
                .setResponse(ResponseTypeValue.newBuilder()
                        .setValue(ResponseType.RT_ACCEPT)
                        .build())
                .build();

        Team event = convert(
                teamInvitationResponseMsg,
                MapperUtils.unmapMsgType(Daedalus.TEAM_INVITATION_RESPONSE_TEAM(TeamId.of(1L))),
                NcctToTeamAdapter::from);

        assertTrue(event instanceof TeamMessages.TeamInvitationResponse);;
        assertEquals(teamInvitationResponseMsg,
                ((TeamMessages.TeamInvitationResponse) event).teamInvitationResponseMsg());
    }

    @Test
    public void createTeamRegistrationFrom() {
        TeamRegistrationMsg teamRegistrationMsg = TeamRegistrationMsg.newBuilder()
                .setLeaderId(NcctIdUtils.toNcctId(PlatformId.UNKNOWN))
                .addMemberIds(NcctIdUtils.toNcctId(PlatformId.UNKNOWN))
                .build();

        Team event = convert(
                teamRegistrationMsg,
                MapperUtils.unmapMsgType(Daedalus.TEAM_REGISTRATION_TEAM(TeamId.of(1L))),
                NcctToTeamAdapter::from);

        assertTrue(event instanceof TeamMessages.TeamRegistration);
        assertEquals(teamRegistrationMsg, ((TeamMessages.TeamRegistration) event).teamRegistrationMsg());
    }

    @Test
    public void testUnknownFrom() {
        PingInitiateMsg pingInitiateMsg = PingInitiateMsg.newBuilder()
                .build();

        Team event = convert(
                pingInitiateMsg,
                MessageInfo.newBuilder()
                        .setCatalog(CatalogTypeValue.newBuilder()
                                .setValue(CatalogType.CT_NCCT_5)
                                .build())
                        .setMessageType(Int32Value.of(NcctMessageType.NMST_PingInitiate_VALUE))
                        .setMessageSubtype(0L)
                        .build(),
                NcctToTeamAdapter::from);

        assertTrue(event instanceof TeamInternal.ParseError);;
    }

    @Test
    public void testBadMsgFrom() {
        Team event = convertBadMsg(
                MapperUtils.unmapMsgType(Daedalus.TEAM_ASSIGNMENT_TEAM(TeamId.of(1L))),
                NcctToTeamAdapter::from);

        assertTrue(event instanceof TeamInternal.ParseError);;
    }

    private <T extends Message, U extends Team> U convert(
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

    private <U extends Team> U convertBadMsg(
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
