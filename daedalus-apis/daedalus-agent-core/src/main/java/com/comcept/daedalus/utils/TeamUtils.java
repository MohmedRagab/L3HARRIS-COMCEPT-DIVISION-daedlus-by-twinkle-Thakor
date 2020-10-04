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
import com.comcept.daedalus.api.utils.MsgUtils;
import com.comcept.daedalus.msglib.TeamAssignmentMsg;
import com.comcept.daedalus.msglib.TeamInvitationMsg;
import com.comcept.daedalus.msglib.TeamInvitationResponseMsg;
import com.comcept.daedalus.msglib.TeamRegistrationMsg;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.typed.api.PlatformPosition;
import com.comcept.ncct.typed.api.common.MsgType;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SourceId;
import com.comcept.ncct.typed.mapper.utils.NcctIdUtils;
import com.comcept.ncct.zmq.actor.apis.NcctPim;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TeamUtils {

    /**
     * Create a PlatformPosition createPlatformConfigurationFrom an NcctPlatformPositionEvent.
     *
     * @param event NcctPlatformPositionEvent message
     * @return PlatformPosition
     */
    public Optional<DaedalusMessages.TeamPlatformPosition> createPlatformPositionFrom(
            PlatformPosition event) {

        return Optional.of(DaedalusMessages.TeamPlatformPosition.builder()
                .platformPosition(event)
                .build());
    }

    /**
     * Create a PlatformConfiguration from an NcctPlatformConfigurationEvent.
     *
     * @param event NcctPlatformConfigurationEvent message
     * @return PlatformConfiguration
     */
    public Optional<DaedalusMessages.TeamPlatformConfiguration> createPlatformConfigurationFrom(
            PlatformConfiguration event) {

        return Optional.of(DaedalusMessages.TeamPlatformConfiguration.builder()
                .platformConfiguration(event)
                .build());
    }

    /**
     * Parse a team assignment from a byte string.
     *
     * @param event Generic payload
     * @return TeamAssignment or empty if failed
     */
    public Optional<TeamMessages.TeamAssignment> createTeamAssignmentFrom(GenericPayload event) {
        Optional<TeamAssignmentMsg> teamAssignmentMsg =
                MsgUtils.parsePayload(TeamAssignmentMsg::parseFrom, event.payload());

        if (teamAssignmentMsg.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(TeamMessages.TeamAssignment.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .teamId(TeamId.of(teamAssignmentMsg.get().getDetails().getTeamId().getValue()))
                .teamAssignmentMsg(teamAssignmentMsg.get())
                .build());
    }

    /**
     * Parse a team invitation from a byte string.
     *
     * @param event Generic payload
     * @return TeamInvitation or empty if failed
     */
    public Optional<TeamMessages.TeamInvitation> createTeamInvitationFrom(GenericPayload event) {
        Optional<TeamInvitationMsg> teamInvitationMsg =
                MsgUtils.parsePayload(TeamInvitationMsg::parseFrom, event.payload());

        if (teamInvitationMsg.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(TeamMessages.TeamInvitation.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .leaderId(NcctIdUtils.toPlatformId(teamInvitationMsg.get().getLeaderId()))
                .teamInvitationMsg(teamInvitationMsg.get())
                .build());
    }

    /**
     * Parse a team invitation response from a byte string.
     *
     * @param event Generic payload
     * @return TeamInvitationResponse or empty if failed
     */
    public Optional<TeamMessages.TeamInvitationResponse> createTeamInvitationResponseFrom(GenericPayload event) {
        Optional<TeamInvitationResponseMsg> teamInvitationResponseMsg =
                MsgUtils.parsePayload(TeamInvitationResponseMsg::parseFrom, event.payload());

        if (teamInvitationResponseMsg.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(TeamMessages.TeamInvitationResponse.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .teamInvitationResponseMsg(teamInvitationResponseMsg.get())
                .build());
    }

    /**
     * Parse a team registration from a byte string.
     *
     * @param event Generic payload
     * @return TeamRegistration or empty if failed
     */
    public Optional<TeamMessages.TeamRegistration> createTeamRegistrationFrom(GenericPayload event) {
        Optional<TeamRegistrationMsg> teamRegistrationMsg =
                MsgUtils.parsePayload(TeamRegistrationMsg::parseFrom, event.payload());

        if (teamRegistrationMsg.isEmpty()) {
            return Optional.empty();
        }

        List<PlatformId> memberIds = teamRegistrationMsg.get().getMemberIdsList().stream()
                .map(NcctIdUtils::toPlatformId)
                .collect(Collectors.toList());

        return Optional.of(TeamMessages.TeamRegistration.builder()
                .teamId(TeamId.of(teamRegistrationMsg.get().getTeamId().getValue()))
                .leaderId(NcctIdUtils.toPlatformId(teamRegistrationMsg.get().getLeaderId()))
                .memberIds(memberIds)
                .classification(event.classification())
                .teamRegistrationMsg(teamRegistrationMsg.get())
                .build());
    }

    public NcctPim.Command prepToSend(Team msg, PlatformId destination) {
        return prepToSend(msg, SourceId.of(destination.id(), destination.subId()));
    }

    /**
     * Prepare a message to be sent.
     *
     * @param msg Team message
     * @param destination Destination
     * @return NcctPim.SendMsg or NcctPim.UnspecifiedError if failed
     */
    public NcctPim.Command prepToSend(Team msg, SourceId destination) {
        if (msg instanceof TeamMessages.TeamAssignment) {
            TeamMessages.TeamAssignment team = (TeamMessages.TeamAssignment) msg;
            return buildWrapper(
                    Daedalus.TEAM_ASSIGNMENT_TEAM(TeamId.of(1L)),
                    destination,
                    team.teamAssignmentMsg().toByteArray());
        } else if (msg instanceof TeamMessages.TeamInvitation) {
            TeamMessages.TeamInvitation team = (TeamMessages.TeamInvitation) msg;
            return buildWrapper(
                    Daedalus.TEAM_INVITATION_TEAM(TeamId.of(1L)),
                    destination,
                    team.teamInvitationMsg().toByteArray());
        } else if (msg instanceof TeamMessages.TeamInvitationResponse) {
            TeamMessages.TeamInvitationResponse team = (TeamMessages.TeamInvitationResponse) msg;
            return buildWrapper(
                    Daedalus.TEAM_INVITATION_RESPONSE_TEAM(TeamId.of(1L)),
                    destination,
                    team.teamInvitationResponseMsg().toByteArray());
        } else if (msg instanceof TeamMessages.TeamRegistration) {
            TeamMessages.TeamRegistration team = (TeamMessages.TeamRegistration) msg;
            return buildWrapper(
                    Daedalus.TEAM_REGISTRATION_TEAM(TeamId.of(1L)),
                    destination,
                    team.teamRegistrationMsg().toByteArray());
        } else {
            return NcctPim.UnspecifiedError.of(
                    "Attempted to prep unknown message type " + msg.getClass().toString() + " for send");
        }
    }

    private NcctPim.Command buildWrapper(MsgType msgType, SourceId destination, byte[] payload) {
        return NcctPim.SendMsg.of(
                msgType,
                destination,
                GenericPayload.builder()
                        .msgType(msgType)
                        .payload(payload)
                        .build());
    }

}
