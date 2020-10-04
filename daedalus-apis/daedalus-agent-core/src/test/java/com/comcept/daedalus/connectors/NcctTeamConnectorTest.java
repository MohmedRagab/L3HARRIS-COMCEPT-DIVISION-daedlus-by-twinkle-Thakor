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

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamEvents;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.api.team.TeamMessages;
import com.comcept.daedalus.eventbus.EventDepot;
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
import com.comcept.ncct.typed.api.common.SecurityClassification;
import com.comcept.ncct.typed.api.common.SourceId;
import com.comcept.ncct.zmq.actor.apis.NcctPim;
import java.time.Instant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NcctTeamConnectorTest {

    static final ActorTestKit testKit = ActorTestKit.create();

    @BeforeAll
    static void setup() {
    }

    @AfterAll
    static void cleanup() {
        testKit.shutdownTestKit();
    }

    @Test
    void testRegisterListeners() {
        TestProbe<Team> platformPositionListener
                = testKit.createTestProbe();
        TestProbe<Team> platformConfigurationListener
                = testKit.createTestProbe();
        TestProbe<Team> teamAssignmentListener
                = testKit.createTestProbe();
        TestProbe<Team> teamInvitationListener
                = testKit.createTestProbe();
        TestProbe<Team> teamInvitationResponseListener
                = testKit.createTestProbe();
        TestProbe<Team> teamRegistrationListener
                = testKit.createTestProbe();

        TeamEvents.RegisterListener registerListener = TeamEvents.RegisterListener.builder()
                .subscriptionHandler(
                        DaedalusMessages.TeamPlatformPosition.class, platformPositionListener.getRef())
                .subscriptionHandler(
                        DaedalusMessages.TeamPlatformConfiguration.class, platformConfigurationListener.getRef())
                .subscriptionHandler(
                        TeamMessages.TeamAssignment.class, teamAssignmentListener.getRef())
                .subscriptionHandler(
                        TeamMessages.TeamInvitation.class, teamInvitationListener.getRef())
                .subscriptionHandler(
                        TeamMessages.TeamInvitationResponse.class, teamInvitationResponseListener.getRef())
                .subscriptionHandler(
                        TeamMessages.TeamRegistration.class, teamRegistrationListener.getRef())
                .build();

        TestProbe<NcctPim.Command> connector = testKit.createTestProbe();

        EventDepot eventDepot = EventDepot.create();

        ActorRef<Team> ref = testKit.spawn(
                NcctTeamConnector.create(connector.getRef(), eventDepot));

        ref.tell(registerListener);

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

        ref.tell(teamPlatformPosition);

        platformPositionListener.expectMessage(teamPlatformPosition);
        platformConfigurationListener.expectNoMessage();
        teamAssignmentListener.expectNoMessage();
        teamInvitationListener.expectNoMessage();
        teamInvitationResponseListener.expectNoMessage();
        teamRegistrationListener.expectNoMessage();

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

        ref.tell(teamPlatformConfiguration);

        platformPositionListener.expectNoMessage();
        platformConfigurationListener.expectMessage(teamPlatformConfiguration);
        teamAssignmentListener.expectNoMessage();
        teamInvitationListener.expectNoMessage();
        teamInvitationResponseListener.expectNoMessage();
        teamRegistrationListener.expectNoMessage();

        TeamMessages.TeamAssignment teamAssignment = TeamMessages.TeamAssignment.builder()
                .teamId(TeamId.of(1L))
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .teamAssignmentMsg(TeamAssignmentMsg.newBuilder().build())
                .build();

        ref.tell(teamAssignment);

        platformPositionListener.expectNoMessage();
        platformConfigurationListener.expectNoMessage();
        teamAssignmentListener.expectMessage(teamAssignment);
        teamInvitationListener.expectNoMessage();
        teamInvitationResponseListener.expectNoMessage();
        teamRegistrationListener.expectNoMessage();

        TeamMessages.TeamInvitation teamInvitation = TeamMessages.TeamInvitation.builder()
                .leaderId(PlatformId.of(1, 1))
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .teamInvitationMsg(TeamInvitationMsg.newBuilder().build())
                .build();

        ref.tell(teamInvitation);

        platformPositionListener.expectNoMessage();
        platformConfigurationListener.expectNoMessage();
        teamAssignmentListener.expectNoMessage();
        teamInvitationListener.expectMessage(teamInvitation);
        teamInvitationResponseListener.expectNoMessage();
        teamRegistrationListener.expectNoMessage();

        TeamMessages.TeamInvitationResponse teamInvitationResponse
                = TeamMessages.TeamInvitationResponse.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .teamInvitationResponseMsg(TeamInvitationResponseMsg.newBuilder().build())
                .build();

        ref.tell(teamInvitationResponse);

        platformPositionListener.expectNoMessage();
        platformConfigurationListener.expectNoMessage();
        teamAssignmentListener.expectNoMessage();
        teamInvitationListener.expectNoMessage();
        teamInvitationResponseListener.expectMessage(teamInvitationResponse);
        teamRegistrationListener.expectNoMessage();

        TeamMessages.TeamRegistration teamRegistration
                = TeamMessages.TeamRegistration.builder()
                .teamId(TeamId.of(1L))
                .leaderId(PlatformId.of(1, 1))
                .memberId(PlatformId.of(1, 2))
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .teamRegistrationMsg(TeamRegistrationMsg.newBuilder().build())
                .build();

        ref.tell(teamRegistration);

        platformPositionListener.expectNoMessage();
        platformConfigurationListener.expectNoMessage();
        teamAssignmentListener.expectNoMessage();
        teamInvitationListener.expectNoMessage();
        teamInvitationResponseListener.expectNoMessage();
        teamRegistrationListener.expectMessage(teamRegistration);

        // Check unknown handling
        ref.tell(new Team() {});

        platformPositionListener.expectNoMessage();
        platformConfigurationListener.expectNoMessage();
        teamAssignmentListener.expectNoMessage();
        teamInvitationListener.expectNoMessage();
        teamInvitationResponseListener.expectNoMessage();
        teamRegistrationListener.expectNoMessage();

        testKit.stop(ref);
    }

}
