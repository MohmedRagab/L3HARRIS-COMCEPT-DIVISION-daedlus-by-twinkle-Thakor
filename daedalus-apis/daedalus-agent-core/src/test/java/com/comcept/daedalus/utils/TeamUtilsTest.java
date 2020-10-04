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
package com.comcept.daedalus.utils;

import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.daedalus.api.msg.Daedalus;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamId;
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
import com.comcept.ncct.typed.api.common.SecurityClassification;
import com.comcept.ncct.typed.api.common.SourceId;
import com.comcept.ncct.typed.mapper.utils.MapperUtils;
import com.comcept.ncct.typed.mapper.utils.NcctIdUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.Message;
import comcept.ncct.msglib.GenericPayloadMsg;
import comcept.ncct.msglib.MessageInfo;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TeamUtilsTest {
    
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
        
        Optional<DaedalusMessages.TeamPlatformPosition> event =
                TeamUtils.createPlatformPositionFrom(platformPosition);
        
        assertTrue(event.isPresent());
        assertEquals(platformPosition, event.get().platformPosition());
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

        DaedalusMessages.TeamPlatformConfiguration teamPlatformConfiguration =
                DaedalusMessages.TeamPlatformConfiguration.builder()
                .platformConfiguration(platformConfiguration)
                .build();

        Optional<DaedalusMessages.TeamPlatformConfiguration> event =
                TeamUtils.createPlatformConfigurationFrom(platformConfiguration);

        assertTrue(event.isPresent());
        assertEquals(platformConfiguration, event.get().platformConfiguration());
    }

    @Test
    public void testCreateTeamAssignmentFrom() {
        TeamAssignmentMsg teamAssignmentMsg = TeamAssignmentMsg.newBuilder()
                .setDetails(TeamAssignmentDetails.newBuilder().build())
                .build();

        Optional<TeamMessages.TeamAssignment> event = convert(
                teamAssignmentMsg,
                MapperUtils.unmapMsgType(Daedalus.TEAM_ASSIGNMENT_TEAM(TeamId.of(1L))),
                TeamUtils::createTeamAssignmentFrom);
        
        assertTrue(event.isPresent());
        assertEquals(teamAssignmentMsg, event.get().teamAssignmentMsg());

        event = convertBadMsg(
                MapperUtils.unmapMsgType(Daedalus.TEAM_ASSIGNMENT_TEAM(TeamId.of(1L))),
                TeamUtils::createTeamAssignmentFrom);

        assertFalse(event.isPresent());
    }

    @Test
    public void testCreateTeamInvitationFrom() {
        TeamInvitationMsg teamInvitationMsg = TeamInvitationMsg.newBuilder()
                .setLeaderId(NcctIdUtils.toNcctId(PlatformId.UNKNOWN))
                .build();

        Optional<TeamMessages.TeamInvitation> event = convert(
                teamInvitationMsg,
                MapperUtils.unmapMsgType(Daedalus.TEAM_INVITATION_TEAM(TeamId.of(1L))),
                TeamUtils::createTeamInvitationFrom);

        assertTrue(event.isPresent());
        assertEquals(teamInvitationMsg, event.get().teamInvitationMsg());

        event = convertBadMsg(
                MapperUtils.unmapMsgType(Daedalus.TEAM_INVITATION_TEAM(TeamId.of(1L))),
                TeamUtils::createTeamInvitationFrom);

        assertFalse(event.isPresent());
    }

    @Test
    public void testCreateTeamInvitiationResponseFrom() {
        TeamInvitationResponseMsg teamInvitationResponseMsg = TeamInvitationResponseMsg.newBuilder()
                .setResponse(ResponseTypeValue.newBuilder()
                        .setValue(ResponseType.RT_ACCEPT)
                        .build())
                .build();

        Optional<TeamMessages.TeamInvitationResponse> event = convert(
                teamInvitationResponseMsg,
                MapperUtils.unmapMsgType(Daedalus.TEAM_INVITATION_RESPONSE_TEAM(TeamId.of(1L))),
                TeamUtils::createTeamInvitationResponseFrom);

        assertTrue(event.isPresent());
        assertEquals(teamInvitationResponseMsg, event.get().teamInvitationResponseMsg());

        event = convertBadMsg(
                MapperUtils.unmapMsgType(Daedalus.TEAM_INVITATION_RESPONSE_TEAM(TeamId.of(1L))),
                TeamUtils::createTeamInvitationResponseFrom);

        assertFalse(event.isPresent());
    }

    @Test
    public void testCreateTeamRegistrationFrom() {
        TeamRegistrationMsg teamRegistrationMsg = TeamRegistrationMsg.newBuilder()
                .setLeaderId(NcctIdUtils.toNcctId(PlatformId.UNKNOWN))
                .addMemberIds(NcctIdUtils.toNcctId(PlatformId.UNKNOWN))
                .build();

        Optional<TeamMessages.TeamRegistration> event = convert(
                teamRegistrationMsg,
                MapperUtils.unmapMsgType(Daedalus.TEAM_REGISTRATION_TEAM(TeamId.of(1L))),
                TeamUtils::createTeamRegistrationFrom);

        assertTrue(event.isPresent());
        assertEquals(teamRegistrationMsg, event.get().teamRegistrationMsg());

        event = convertBadMsg(
                MapperUtils.unmapMsgType(Daedalus.TEAM_REGISTRATION_TEAM(TeamId.of(1L))),
                TeamUtils::createTeamRegistrationFrom);

        assertFalse(event.isPresent());
    }

    private <T extends Message, U extends Team> Optional<U> convert(
            T sourceMsg,
            MessageInfo messageInfo,
            Function<GenericPayload, Optional<U>> fn) {

        GenericPayloadMsg genericPayloadMsg = GenericPayloadMsg.newBuilder()
                .setPayload(BytesValue.of(sourceMsg.toByteString()))
                .build();

        byte[] payload = new byte[genericPayloadMsg.getPayload().getValue().size()];
        genericPayloadMsg.getPayload().getValue().copyTo(payload, 0);

        GenericPayload genericPayload = GenericPayload.builder()
                .classification(SecurityClassification.builder()
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

    private <U extends Team> Optional<U> convertBadMsg(
            MessageInfo messageInfo,
            Function<GenericPayload, Optional<U>> fn) {

        GenericPayloadMsg genericPayloadMsg = GenericPayloadMsg.newBuilder()
                .setPayload(BytesValue.of(ByteString.copyFrom(new byte[] { 0x10, 0x20, 0x40})))
                .build();

        byte[] payload = new byte[genericPayloadMsg.getPayload().getValue().size()];
        genericPayloadMsg.getPayload().getValue().copyTo(payload, 0);

        GenericPayload genericPayload = GenericPayload.builder()
                .classification(SecurityClassification.builder()
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
