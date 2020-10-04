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
import com.comcept.daedalus.api.team.TeamMessages;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.NcctTyped;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.typed.api.PlatformPosition;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TeamToNcctAdapter {

    /** Convert a Team event to an NCCT event.
     *
     * @param event Team event
     * @return NCCT event
     */
    public NcctTyped from(Team event) {
        if (event instanceof DaedalusMessages.TeamPlatformPosition) {
            DaedalusMessages.TeamPlatformPosition msg = (DaedalusMessages.TeamPlatformPosition) event;
            return from(msg);
        } else if (event instanceof DaedalusMessages.TeamPlatformConfiguration) {
            DaedalusMessages.TeamPlatformConfiguration msg = (DaedalusMessages.TeamPlatformConfiguration) event;
            return from(msg);
        } else if (event instanceof TeamMessages.TeamRegistration) {
            TeamMessages.TeamRegistration msg = (TeamMessages.TeamRegistration) event;
            return from(msg);
        } else if (event instanceof TeamMessages.TeamInvitation) {
            TeamMessages.TeamInvitation msg = (TeamMessages.TeamInvitation) event;
            return from(msg);
        } else if (event instanceof TeamMessages.TeamInvitationResponse) {
            TeamMessages.TeamInvitationResponse msg = (TeamMessages.TeamInvitationResponse) event;
            return from(msg);
        } else if (event instanceof TeamMessages.TeamAssignment) {
            TeamMessages.TeamAssignment msg = (TeamMessages.TeamAssignment) event;
            return from(msg);
        } else {
            return null;
        }
    }

    /** Convert a PlatformPosition to an NCCT event.
     *
     * @param event Team event
     * @return NCCT event
     */
    public PlatformPosition from(DaedalusMessages.TeamPlatformPosition event) {
        return event.platformPosition();
    }

    /** Convert a PlatformConfiguration to an NCCT event.
     *
     * @param event Team event
     * @return NCCT event
     */
    public PlatformConfiguration from(DaedalusMessages.TeamPlatformConfiguration event) {
        return event.platformConfiguration();
    }

    /** Convert a TeamAssignment to an NCCT event.
     *
     * @param event Team event
     * @return NCCT event
     */
    public GenericPayload from(TeamMessages.TeamAssignment event) {
        return GenericPayload.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .msgType(Daedalus.TEAM_ASSIGNMENT_TEAM(TeamId.of(1L)))
                .payload(event.teamAssignmentMsg().toByteArray())
                .build();
    }

    /** Convert a TeamInvitation to an NCCT event.
     *
     * @param event Team event
     * @return NCCT event
     */
    public GenericPayload from(TeamMessages.TeamInvitation event) {
        return GenericPayload.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .msgType(Daedalus.TEAM_INVITATION_TEAM(TeamId.of(1L)))
                .payload(event.teamInvitationMsg().toByteArray())
                .build();
    }

    /** Convert a TeamInvitationResponse to an NCCT event.
     *
     * @param event Team event
     * @return NCCT event
     */
    public GenericPayload from(TeamMessages.TeamInvitationResponse event) {
        return GenericPayload.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .msgType(Daedalus.TEAM_INVITATION_RESPONSE_TEAM(TeamId.of(1L)))
                .payload(event.teamInvitationResponseMsg().toByteArray())
                .build();
    }

    /** Convert a TeamRegistration to an NCCT event.
     *
     * @param event Team event
     * @return NCCT event
     */
    public GenericPayload from(TeamMessages.TeamRegistration event) {
        return GenericPayload.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .msgType(Daedalus.TEAM_REGISTRATION_TEAM(TeamId.of(1L)))
                .payload(event.teamRegistrationMsg().toByteArray())
                .build();
    }

}
