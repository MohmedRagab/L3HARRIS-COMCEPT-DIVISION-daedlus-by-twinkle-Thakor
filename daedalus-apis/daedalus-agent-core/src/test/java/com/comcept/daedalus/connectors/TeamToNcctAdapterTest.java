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
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.api.team.TeamMessages;
import com.comcept.daedalus.msglib.TeamAssignmentMsg;
import com.comcept.daedalus.msglib.TeamInvitationMsg;
import com.comcept.daedalus.msglib.TeamInvitationResponseMsg;
import com.comcept.daedalus.msglib.TeamRegistrationMsg;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.NcctTyped;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.typed.api.PlatformPosition;
import com.comcept.ncct.typed.api.common.GeoPoint;
import com.comcept.ncct.typed.api.common.Identification;
import com.comcept.ncct.typed.api.common.MessageId;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SecurityClassification;
import com.comcept.ncct.typed.api.common.SourceId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TeamToNcctAdapterTest {
    
    @Test
    public void testPlatformPosition() {
        DaedalusMessages.TeamPlatformPosition teamPlatformPosition = DaedalusMessages.TeamPlatformPosition.builder()
                .platformPosition(PlatformPosition.builder()
                        .classification(SecurityClassification.builder()
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
                        .build())
                .build();

        NcctTyped event = TeamToNcctAdapter.from(teamPlatformPosition);

        assertEquals(teamPlatformPosition.platformPosition(), event);
    }

    @Test
    public void testPlatformConfiguration() {
        DaedalusMessages.TeamPlatformConfiguration teamPlatformConfiguration
                = DaedalusMessages.TeamPlatformConfiguration.builder()
                .platformConfiguration(PlatformConfiguration.builder()
                        .classification(SecurityClassification.builder()
                                .capco("UNCLASSIFIED")
                                .ownerTrigraph("USA")
                                .build())
                        .messageId(MessageId.UNKNOWN)
                        .sourceId(SourceId.UNKNOWN)
                        .creationTime(Instant.ofEpochSecond(1234L))
                        .platformId(PlatformId.of(1, 1))
                        .subIdMin(100)
                        .subIdMax(100)
                        .build())
                .build();

        PlatformConfiguration event = TeamToNcctAdapter.from(teamPlatformConfiguration);

        assertEquals(teamPlatformConfiguration.platformConfiguration(), event);
    }

    @Test
    public void testTeamAssignment() {
        TeamMessages.TeamAssignment teamAssignment = TeamMessages.TeamAssignment.builder()
                .teamId(TeamId.of(1L))
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .teamAssignmentMsg(TeamAssignmentMsg.newBuilder().build())
                .build();

        GenericPayload event = TeamToNcctAdapter.from(teamAssignment);

        assertArrayEquals(teamAssignment.teamAssignmentMsg().toByteArray(), event.payload());
    }

    @Test
    public void testTeamInvitation() {
        TeamMessages.TeamInvitation teamInvitation = TeamMessages.TeamInvitation.builder()
                .leaderId(PlatformId.of(1, 1))
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .teamInvitationMsg(TeamInvitationMsg.newBuilder().build())
                .build();

        GenericPayload event = TeamToNcctAdapter.from(teamInvitation);

        assertArrayEquals(teamInvitation.teamInvitationMsg().toByteArray(), event.payload());
    }

    @Test
    public void testTeamInvitationResponse() {
        TeamMessages.TeamInvitationResponse teamInvitationResponse
                = TeamMessages.TeamInvitationResponse.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .teamInvitationResponseMsg(TeamInvitationResponseMsg.newBuilder().build())
                .build();

        GenericPayload event = TeamToNcctAdapter.from(teamInvitationResponse);

        assertArrayEquals(teamInvitationResponse.teamInvitationResponseMsg().toByteArray(),
                event.payload());
    }

    @Test
    public void testTeamRegistration() {
        TeamMessages.TeamRegistration teamRegistration = TeamMessages.TeamRegistration.builder()
                .teamId(TeamId.of(1L))
                .leaderId(PlatformId.of(1, 1))
                .memberId(PlatformId.of(1, 1))
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .teamRegistrationMsg(TeamRegistrationMsg.newBuilder().build())
                .build();

        GenericPayload event = TeamToNcctAdapter.from(teamRegistration);

        assertArrayEquals(teamRegistration.teamRegistrationMsg().toByteArray(), event.payload());
    }

}
