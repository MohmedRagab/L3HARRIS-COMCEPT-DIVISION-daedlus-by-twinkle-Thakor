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
package com.comcept.daedalus.behaviors.team;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import com.comcept.daedalus.api.DaedalusEvents;
import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamEvents;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.typed.api.PlatformPosition;
import com.comcept.ncct.typed.api.common.GeoPoint;
import com.comcept.ncct.typed.api.common.Identification;
import com.comcept.ncct.typed.api.common.MessageId;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SourceId;
import java.time.Instant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TeamPlatformManagerBehaviorTest {

    static final ActorTestKit testKit = ActorTestKit.create();

    @BeforeAll
    public static void setup() {
    }

    @AfterAll
    public static void cleanup() {
        testKit.shutdownTestKit();
    }

    @Test
    public void testPlatformPosition() {
        DaedalusMessages.TeamPlatformPosition teamPlatformPosition = DaedalusMessages.TeamPlatformPosition.builder()
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

        TestProbe<Team> connector = testKit.createTestProbe();

        ActorRef<Team> ref =
                testKit.spawn(TeamPlatformManagerBehavior.create(connector.getRef(), EventDepot.create()));

        connector.expectMessageClass(TeamEvents.RegisterListener.class);

        ref.tell(teamPlatformPosition);

        DaedalusEvents.PlatformPositionRequest request1 = DaedalusEvents.PlatformPositionRequest.builder()
                .platformId(PlatformId.of(1, 1))
                .replyTo(connector.getRef().narrow())
                .build();

        ref.tell(request1);

        connector.expectMessage(teamPlatformPosition);

        DaedalusEvents.PlatformPositionRequest request2 = DaedalusEvents.PlatformPositionRequest.builder()
                .platformId(PlatformId.of(2, 1))
                .replyTo(connector.getRef().narrow())
                .build();

        ref.tell(request2);

        connector.expectNoMessage();

        testKit.stop(ref);
    }

    @Test
    public void testPlatformConfiguration() {
        DaedalusMessages.TeamPlatformConfiguration teamPlatformConfiguration
                = DaedalusMessages.TeamPlatformConfiguration.builder()
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

        TestProbe<Team> connector = testKit.createTestProbe();

        ActorRef<Team> ref =
                testKit.spawn(TeamPlatformManagerBehavior.create(connector.getRef(), EventDepot.create()));

        connector.expectMessageClass(TeamEvents.RegisterListener.class);

        ref.tell(teamPlatformConfiguration);


        DaedalusEvents.PlatformConfigurationRequest request1
                = DaedalusEvents.PlatformConfigurationRequest.builder()
                .platformId(PlatformId.of(1, 1))
                .replyTo(connector.getRef().narrow())
                .build();

        ref.tell(request1);

        connector.expectMessage(teamPlatformConfiguration);

        DaedalusEvents.PlatformConfigurationRequest request2
                = DaedalusEvents.PlatformConfigurationRequest.builder()
                .platformId(PlatformId.of(2, 1))
                .replyTo(connector.getRef().narrow())
                .build();

        ref.tell(request2);

        connector.expectNoMessage();

        testKit.stop(ref);
    }

}
