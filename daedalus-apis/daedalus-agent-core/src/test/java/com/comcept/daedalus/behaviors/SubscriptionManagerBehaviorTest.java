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
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.daedalus.msglib.DaedalusMessageType;
import com.comcept.ncct.typed.api.common.MsgType;
import com.comcept.ncct.zmq.actor.apis.NcctPim;
import com.typesafe.config.ConfigFactory;
import comcept.ncct.msglib.CatalogType;
import comcept.ncct.msglib.NcctMessageType;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Subscription manager test.
 *
 * @author RDanna
 */
class SubscriptionManagerBehaviorTest {

    static final ActorTestKit testKit = ActorTestKit.create(ConfigFactory.load());

    @BeforeAll
    static void setup() {
    }

    @AfterAll
    static void cleanup() {
        testKit.shutdownTestKit();
    }

    @Test
    void testSubscriptionManager() {
        TestProbe<NcctPim.Command> pimProbe = testKit.createTestProbe();

        ActorRef<Team> subscriptionManager =
                testKit.spawn(SubscriptionManagerBehavior.create(pimProbe.getRef(), EventDepot.create()));

        Set<MsgType> expectedDefault = new HashSet<>();
        expectedDefault.add(MsgType.of(
                CatalogType.CT_NCCT_5_VALUE, 
                NcctMessageType.NMST_PlatformPosition_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedDefault.add(MsgType.of(CatalogType.CT_NCCT_5_VALUE,
                NcctMessageType.NMST_PlatformConfiguration_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedDefault.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamAssignment_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedDefault.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamInvitation_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedDefault.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamInvitationResponse_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedDefault.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamRegistration_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedDefault.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamTask_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedDefault.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamTaskStatus_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedDefault.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.PilotTask_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedDefault.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.PilotTaskStatus_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedDefault.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.FusionTask_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedDefault.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.FusionTaskStatus_VALUE,
                Daedalus.SubTypes.GLOBAL));

        NcctPim.SubscribeTo msgDefault = pimProbe.expectMessageClass(NcctPim.SubscribeTo.class);
        assertEquals(expectedDefault, new HashSet<>(msgDefault.msgTypes()));

        TeamId teamId = TeamId.of(123L);
        subscriptionManager.tell(DaedalusEvents.TeamIdUpdated.of(teamId));

        Set<MsgType> expectedWithTeamId = new HashSet<>();
        expectedWithTeamId.add(MsgType.of(
                CatalogType.CT_NCCT_5_VALUE,
                NcctMessageType.NMST_PlatformPosition_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedWithTeamId.add(MsgType.of(CatalogType.CT_NCCT_5_VALUE,
                NcctMessageType.NMST_PlatformConfiguration_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedWithTeamId.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamAssignment_VALUE,
                Daedalus.SubTypes.GLOBAL));
        expectedWithTeamId.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamInvitation_VALUE,
                teamId.id()));
        expectedWithTeamId.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamInvitationResponse_VALUE,
                teamId.id()));
        expectedWithTeamId.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamRegistration_VALUE,
                teamId.id()));
        expectedWithTeamId.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamTask_VALUE,
                teamId.id()));
        expectedWithTeamId.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.TeamTaskStatus_VALUE,
                teamId.id()));
        expectedWithTeamId.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.PilotTask_VALUE,
                teamId.id()));
        expectedWithTeamId.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.PilotTaskStatus_VALUE,
                teamId.id()));
        expectedWithTeamId.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.FusionTask_VALUE,
                teamId.id()));
        expectedWithTeamId.add(MsgType.of(Daedalus.Catalogs.DAEDALUS,
                DaedalusMessageType.FusionTaskStatus_VALUE,
                teamId.id()));

        NcctPim.SubscribeTo msgWithTeamId = pimProbe.expectMessageClass(NcctPim.SubscribeTo.class);
        assertEquals(expectedWithTeamId, new HashSet<>(msgWithTeamId.msgTypes()));

        testKit.stop(subscriptionManager);
    }

}