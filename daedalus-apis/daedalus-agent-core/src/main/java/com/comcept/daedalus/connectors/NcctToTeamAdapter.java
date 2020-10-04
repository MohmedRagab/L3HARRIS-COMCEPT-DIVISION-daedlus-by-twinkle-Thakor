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

import com.comcept.daedalus.api.DaedalusEvents;
import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.daedalus.api.msg.Daedalus;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.api.team.TeamInternal;
import com.comcept.daedalus.api.team.TeamMessages;
import com.comcept.daedalus.utils.TeamUtils;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.NcctTyped;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.typed.api.PlatformPosition;
import com.comcept.ncct.typed.api.common.MsgType;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.zmq.actor.apis.NcctPim;
import java.util.Optional;
import lombok.experimental.UtilityClass;

/**
 * Utility class for NCCT to Team protocol conversion.
 *
 * @author pborawski, rdanna
 */
@UtilityClass
public class NcctToTeamAdapter {

    /**
     * Create a Team event from an NCCT generic payload.
     *
     * @param msg NcctTyped
     * @return Team event
     */
    public Team from(NcctTyped msg) {

        if (msg instanceof PlatformPosition) {
            PlatformPosition platformPosition = (PlatformPosition) msg;
            return from(platformPosition);
        } else if (msg instanceof PlatformConfiguration) {
            PlatformConfiguration platformConfiguration = (PlatformConfiguration) msg;
            return from(platformConfiguration);
        } else if (msg instanceof GenericPayload) {
            GenericPayload payload = (GenericPayload) msg;
            return from(payload);
        } else if (msg instanceof NcctPim.PimIdUpdated) {
            NcctPim.PimIdUpdated pimIdUpdated = (NcctPim.PimIdUpdated) msg;
            return from(pimIdUpdated);
        } else {
            return TeamInternal.ParseError.of("Unknown message type: " + msg.getClass().toString());
        }
    }

    /**
     * Create a Team event from an NCCT generic payload.
     *
     * @param genericPayloadEvent Generic payload event
     * @return Team event
     */
    public Team from(GenericPayload genericPayloadEvent) {

        MsgType msgType = genericPayloadEvent.msgType();

        Team resolvedEvent = TeamInternal.ParseError.of("Unknown message type: " + msgType.toString());

        if (msgType.equals(Daedalus.TEAM_ASSIGNMENT_TEAM(TeamId.of(1L)))) {
            Optional<TeamMessages.TeamAssignment> event =
                    TeamUtils.createTeamAssignmentFrom(genericPayloadEvent);
            if (event.isPresent()) {
                resolvedEvent = event.get();
            }
        } else if (msgType.equals(Daedalus.TEAM_INVITATION_TEAM(TeamId.of(1L)))) {
            Optional<TeamMessages.TeamInvitation> event =
                    TeamUtils.createTeamInvitationFrom(genericPayloadEvent);
            if (event.isPresent()) {
                resolvedEvent = event.get();
            }
        } else if (msgType.equals(Daedalus.TEAM_INVITATION_RESPONSE_TEAM(TeamId.of(1L)))) {
            Optional<TeamMessages.TeamInvitationResponse> event =
                    TeamUtils.createTeamInvitationResponseFrom(genericPayloadEvent);
            if (event.isPresent()) {
                resolvedEvent = event.get();
            }
        } else if (msgType.equals(Daedalus.TEAM_REGISTRATION_TEAM(TeamId.of(1L)))) {
            Optional<TeamMessages.TeamRegistration> event =
                    TeamUtils.createTeamRegistrationFrom(genericPayloadEvent);
            if (event.isPresent()) {
                resolvedEvent = event.get();
            }
        }

        return resolvedEvent;
    }

    /**
     * Create a Team event from an NCCT platform position.
     *
     * @param platformPositionEvent Platform position event
     * @return Team event
     */
    public Team from(PlatformPosition platformPositionEvent) {

        Optional<DaedalusMessages.TeamPlatformPosition> event =
                TeamUtils.createPlatformPositionFrom(platformPositionEvent);
        if (event.isPresent()) {
            return event.get();
        } else {
            return TeamInternal.ParseError.of("Failed to parse PlatformPosition data");
        }
    }

    /**
     * Create a Team event from an NCCT platform configuration.
     *
     * @param platformConfigurationEvent Platform configuration event
     * @return Team event
     */
    public Team from(PlatformConfiguration platformConfigurationEvent) {

        Optional<DaedalusMessages.TeamPlatformConfiguration> event =
                TeamUtils.createPlatformConfigurationFrom(platformConfigurationEvent);
        if (event.isPresent()) {
            return event.get();
        } else {
            return TeamInternal.ParseError.of("Failed to parse PlatformConfiguration data");
        }
    }

    /**
     * Create a PlatformIdUpdated from a PimIdUpdated.
     *
     * @param pimIdUpdated PIM ID updated
     * @return PlatformIdUpdated event
     */
    public Team from(NcctPim.PimIdUpdated pimIdUpdated) {
        return DaedalusEvents.PlatformIdUpdated.of(PlatformId.of(pimIdUpdated.id()));
    }

}
