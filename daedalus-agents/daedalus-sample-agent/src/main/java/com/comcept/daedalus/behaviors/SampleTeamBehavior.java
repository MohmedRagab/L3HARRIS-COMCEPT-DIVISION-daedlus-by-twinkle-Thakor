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

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.comcept.daedalus.api.DaedalusEvents;
import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.daedalus.api.msg.Daedalus;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamEvents;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.api.team.TeamInternal;
import com.comcept.daedalus.api.team.TeamMessages;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.ncct.typed.api.common.SourceId;

/**
 * Sample team behavior.
 *
 * @author RDanna
 */
public class SampleTeamBehavior extends AbstractBehavior<Team> {

    private final ActorRef<Team> connector;
    private final EventDepot eventDepot;

    public static Behavior<Team> create(ActorRef<Team> connector, EventDepot eventDepot) {
        return Behaviors.setup(ctx -> new SampleTeamBehavior(ctx, connector, eventDepot));
    }

    private SampleTeamBehavior(ActorContext<Team> context,
                              ActorRef<Team> connector,
                              EventDepot eventDepot) {
        super(context);

        this.connector = connector;
        this.eventDepot = eventDepot;

        // Register for PIM messages (only what you need)
        connector.tell(TeamEvents.RegisterListener.builder()
                .subscriptionHandler(DaedalusEvents.PlatformIdUpdated.class, context.getSelf())
                .subscriptionHandler(DaedalusMessages.TeamPlatformPosition.class, context.getSelf())
                .subscriptionHandler(DaedalusMessages.TeamPlatformConfiguration.class, context.getSelf())
                .subscriptionHandler(TeamMessages.TeamAssignment.class, context.getSelf())
                .subscriptionHandler(TeamMessages.TeamInvitation.class, context.getSelf())
                .subscriptionHandler(TeamMessages.TeamInvitationResponse.class, context.getSelf())
                .subscriptionHandler(TeamMessages.TeamRegistration.class, context.getSelf())
                .build());

        // Subscribe for local events
        eventDepot.teamEventBus().subscribe(context.getSelf(), TeamEvents.TeamData.class);
    }

    @Override
    public Receive<Team> createReceive() {
        return newReceiveBuilder()
                // Register handlers for all the messages subscribed for from PIM or event bus
                .onMessage(DaedalusEvents.PlatformIdUpdated.class, this::onPlatformIdUpdated)
                .build();
    }

    private Behavior<Team> onPlatformIdUpdated(DaedalusEvents.PlatformIdUpdated event) {
        // Do stuff

        // Non-functional example of send via PIM
        connector.tell(TeamInternal.SendMsg.of(
                Daedalus.TEAM_REGISTRATION_TEAM(TeamId.of(1L)),
                SourceId.of(1, 1),
                event));

        // Non-functional example of local send
        eventDepot.teamEventBus().publish(event);

        return Behaviors.same();
    }

}
