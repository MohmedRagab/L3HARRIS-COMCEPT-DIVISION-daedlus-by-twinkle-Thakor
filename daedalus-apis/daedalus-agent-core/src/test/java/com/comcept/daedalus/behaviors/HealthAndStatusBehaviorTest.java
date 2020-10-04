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
package com.comcept.daedalus.behaviors;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import com.comcept.daedalus.api.DaedalusEvents;
import com.comcept.daedalus.api.msg.Daedalus;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.api.team.TeamInternal;
import com.comcept.daedalus.api.team.TeamMessages;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.daedalus.msglib.AgentStatusDetails;
import com.comcept.daedalus.msglib.AgentType;
import com.comcept.daedalus.msglib.AgentTypeValue;
import com.comcept.daedalus.msglib.HealthAndStatusMsg;
import com.comcept.daedalus.msglib.HealthAndStatusType;
import com.comcept.daedalus.msglib.HealthAndStatusTypeValue;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SourceId;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Health and status test.
 * s
 * @author RDanna
 */
class HealthAndStatusBehaviorTest {

    static final ActorTestKit testKit = ActorTestKit.create(ConfigFactory.parseMap(
            ImmutableMap.of("app.agent.timers.agent_status_interval", 500)
    ));

    @BeforeAll
    static void setup() {
    }

    @AfterAll
    static void cleanup() {
        testKit.shutdownTestKit();
    }

    @Test
    void testHealthAndStatus() {
        TestProbe<Team> teamProbe = testKit.createTestProbe();

        ActorRef<Team> healthAndStatus = testKit.spawn(HealthAndStatusBehavior.create(
                teamProbe.getRef(),
                EventDepot.create(),
                AgentType.AT_TEAM_AGENT));

        TeamInternal.SendMsg expectedInitial = TeamInternal.SendMsg.of(
                Daedalus.HEALTH_AND_STATUS_GLOBAL,
                SourceId.UNKNOWN,
                TeamMessages.HealthAndStatus.builder()
                        .healthAndStatusMsg(HealthAndStatusMsg.newBuilder()
                                .setAgentDetails(AgentStatusDetails.newBuilder()
                                        .setAgentType(AgentTypeValue.newBuilder()
                                                .setValue(AgentType.AT_TEAM_AGENT)
                                                .build())
                                        .setStatus(HealthAndStatusTypeValue.newBuilder()
                                                .setValue(HealthAndStatusType.HST_NORMAL))
                                        .build())
                                .build())
                        .build());

        teamProbe.expectMessage(expectedInitial);

        healthAndStatus.tell(DaedalusEvents.TeamIdUpdated.of(TeamId.of(34L)));

        TeamInternal.SendMsg expectedWithTeamId = TeamInternal.SendMsg.of(
                Daedalus.HEALTH_AND_STATUS_GLOBAL,
                SourceId.UNKNOWN,
                TeamMessages.HealthAndStatus.builder()
                        .healthAndStatusMsg(HealthAndStatusMsg.newBuilder()
                                .setAgentDetails(AgentStatusDetails.newBuilder()
                                        .setAgentType(AgentTypeValue.newBuilder()
                                                .setValue(AgentType.AT_TEAM_AGENT)
                                                .build())
                                        .setStatus(HealthAndStatusTypeValue.newBuilder()
                                                .setValue(HealthAndStatusType.HST_NORMAL))
                                        .build())
                                .build())
                        .build());

        teamProbe.expectMessage(expectedWithTeamId);

        healthAndStatus.tell(DaedalusEvents.PlatformIdUpdated.of(PlatformId.of(3, 4)));

        TeamInternal.SendMsg expectedWithSourceId = TeamInternal.SendMsg.of(
                Daedalus.HEALTH_AND_STATUS_GLOBAL,
                SourceId.of(3, 4),
                TeamMessages.HealthAndStatus.builder()
                        .healthAndStatusMsg(HealthAndStatusMsg.newBuilder()
                                .setAgentDetails(AgentStatusDetails.newBuilder()
                                        .setAgentType(AgentTypeValue.newBuilder()
                                                .setValue(AgentType.AT_TEAM_AGENT)
                                                .build())
                                        .setStatus(HealthAndStatusTypeValue.newBuilder()
                                                .setValue(HealthAndStatusType.HST_NORMAL))
                                        .build())
                                .build())
                        .build());

        teamProbe.expectMessage(expectedWithSourceId);

        testKit.stop(healthAndStatus);
    }

}