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
package com.comcept.daedalus.api.team;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import com.comcept.daedalus.api.DaedalusApi;
import com.comcept.daedalus.api.DaedalusEvents;
import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.daedalus.msglib.TeamAssignmentMsg;
import com.comcept.daedalus.msglib.TeamInvitationMsg;
import com.comcept.daedalus.msglib.TeamInvitationResponseMsg;
import com.comcept.daedalus.msglib.TeamRegistrationMsg;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.typed.api.PlatformPosition;
import com.comcept.ncct.typed.api.common.GeoPoint;
import com.comcept.ncct.typed.api.common.Identification;
import com.comcept.ncct.typed.api.common.MessageId;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SourceId;
import java.time.Instant;
import java.util.Collections;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Team Protocol verification.
 *
 * <p>The tests here are to exercise the API. Message construction and assignment
 * is enough to validate behavior. If there is a failure, the expectation is either
 * a compilation error or runtime exception.
 */
public class TeamProtocolTest {

    static final ActorTestKit testKit = ActorTestKit.create();

    @BeforeAll
    public static void setup() {

    }

    @AfterAll
    public static void cleanup() {
        testKit.shutdownTestKit();
    }

    @Test
    public void testTeamInterface() {
        Team team = new Team() {};

        assertTrue(team instanceof DaedalusApi);
    }

    @Test
    public void testRegisterListener() {
        Team event = TeamEvents.RegisterListener.builder()
                .build();

        assertNotNull(event);
    }

    @Test
    public void testPlatformPosition() {
        Team event = DaedalusMessages.TeamPlatformPosition.builder()
                .platformPosition(PlatformPosition.builder()
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
                        .build())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testPlatformConfiguration() {
        Team event = DaedalusMessages.TeamPlatformConfiguration.builder()
                .platformConfiguration(PlatformConfiguration.builder()
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
                        .build())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testTeamAssignment() {
        Team event = TeamMessages.TeamAssignment.builder()
                .classification(com.comcept.ncct.typed.api.common.SecurityClassification.builder()
                        .capco("TOP SECRET")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .teamId(TeamId.of(1L))
                .teamAssignmentMsg(TeamAssignmentMsg.newBuilder()
                        .build())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testTeamInvitation() {
        Team event = TeamMessages.TeamInvitation.builder()
                .classification(com.comcept.ncct.typed.api.common.SecurityClassification.builder()
                        .capco("TOP SECRET")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .leaderId(PlatformId.of(1, 1))
                .teamInvitationMsg(TeamInvitationMsg.newBuilder()
                        .build())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testTeamInvitationResponse() {
        Team event = TeamMessages.TeamInvitationResponse.builder()
                .classification(com.comcept.ncct.typed.api.common.SecurityClassification.builder()
                        .capco("TOP SECRET")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .teamInvitationResponseMsg(TeamInvitationResponseMsg.newBuilder()
                        .build())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testTeamRegistration() {
        Team event = TeamMessages.TeamRegistration.builder()
                .classification(com.comcept.ncct.typed.api.common.SecurityClassification.builder()
                        .capco("TOP SECRET")
                        .ownerTrigraph("USA")
                        .build())
                .sourceId(SourceId.UNKNOWN)
                .messageId(MessageId.UNKNOWN)
                .creationTime(Instant.EPOCH)
                .teamId(TeamId.of(1L))
                .leaderId(PlatformId.of(1, 1))
                .memberIds(Collections.singletonList(PlatformId.of(1, 1)))
                .teamRegistrationMsg(TeamRegistrationMsg.newBuilder()
                        .build())
                .build();

        assertNotNull(event);
    }
    
    @Test
    public void testPlatformPositionRequest() {
        TestProbe<DaedalusMessages.TeamPlatformPosition> dummyRequestor = testKit.createTestProbe();

        Team event = DaedalusEvents.PlatformPositionRequest.builder()
                .platformId(PlatformId.of(1, 1))
                .replyTo(dummyRequestor.getRef())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testPlatformConfigurationRequest() {
        TestProbe<DaedalusMessages.TeamPlatformConfiguration> dummyRequestor = testKit.createTestProbe();

        Team event = DaedalusEvents.PlatformConfigurationRequest.builder()
                .platformId(PlatformId.of(1, 1))
                .replyTo(dummyRequestor.getRef())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testTeamData() {
        Team event = TeamEvents.TeamData.builder()
                .teamId(TeamId.of(1L))
                .platformPositions(Collections.emptyMap())
                .platformConfigurations(Collections.emptyMap())
                .build();

        assertNotNull(event);
    }

    @Test
    public void testTeamDataRequest() {
        TestProbe<TeamEvents.TeamData> dummyRequestor = testKit.createTestProbe();

        Team event = TeamEvents.TeamDataRequest.builder()
                .teamId(TeamId.of(1L))
                .replyTo(dummyRequestor.getRef())
                .build();

        assertNotNull(event);
    }

}
