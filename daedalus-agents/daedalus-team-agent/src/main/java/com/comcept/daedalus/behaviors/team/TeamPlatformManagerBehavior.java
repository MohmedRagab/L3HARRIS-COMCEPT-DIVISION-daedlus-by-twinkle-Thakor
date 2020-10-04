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

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.comcept.daedalus.api.DaedalusEvents;
import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamEvents;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.ncct.typed.api.common.PlatformId;
import java.util.HashMap;
import java.util.Map;

public class TeamPlatformManagerBehavior extends AbstractBehavior<Team> {

    private final Map<PlatformId, DaedalusMessages.TeamPlatformPosition> positions = new HashMap<>();
    private final Map<PlatformId, DaedalusMessages.TeamPlatformConfiguration> configs = new HashMap<>();

    /**
     * Create a TeamPlatformManagerBehavior.
     *
     * @param teamConnector Connector to send/receive team events
     * @return TeamPlatformManagerBehavior
     */
    public static Behavior<Team> create(ActorRef<Team> teamConnector, EventDepot eventDepot) {
        Behavior<Team> behavior =
                Behaviors.setup(context -> new TeamPlatformManagerBehavior(context, teamConnector, eventDepot));

        return Behaviors.supervise(behavior)
                .onFailure(SupervisorStrategy.restart());
    }

    private TeamPlatformManagerBehavior(
            ActorContext<Team> context,
            ActorRef<Team> teamConnector,
            EventDepot eventDepot) {
        super(context);

        teamConnector.tell(TeamEvents.RegisterListener.builder()
                .subscriptionHandler(DaedalusMessages.TeamPlatformPosition.class, context.getSelf())
                .subscriptionHandler(DaedalusMessages.TeamPlatformConfiguration.class, context.getSelf())
                .build());

        // Subscribe for lookup requests
        eventDepot.teamEventBus().subscribe(context.getSelf(), DaedalusEvents.PlatformPositionRequest.class);
        eventDepot.teamEventBus().subscribe(context.getSelf(), DaedalusEvents.PlatformConfigurationRequest.class);
    }

    @Override
    public Receive<Team> createReceive() {
        return newReceiveBuilder()
                .onMessage(DaedalusMessages.TeamPlatformPosition.class,
                        this::onPlatformPosition)
                .onMessage(DaedalusMessages.TeamPlatformConfiguration.class,
                        this::onPlatformConfiguration)
                .onMessage(DaedalusEvents.PlatformPositionRequest.class,
                        this::onPlatformPositionRequest)
                .onMessage(DaedalusEvents.PlatformConfigurationRequest.class,
                        this::onPlatformConfigurationRequest)
                .build();
    }

    private Behavior<Team> onPlatformPosition(
            DaedalusMessages.TeamPlatformPosition msg) {

        positions.put(msg.platformPosition().platformId(), msg);

        return Behaviors.same();
    }

    private Behavior<Team> onPlatformConfiguration(
            DaedalusMessages.TeamPlatformConfiguration msg) {

        configs.put(msg.platformConfiguration().platformId(), msg);

        return Behaviors.same();
    }

    private Behavior<Team> onPlatformPositionRequest(
            DaedalusEvents.PlatformPositionRequest msg) {

        if (! positions.containsKey(msg.platformId())) {
            getContext().getLog().info("Failed to find platform position data for {}", msg.platformId());
            return Behaviors.same();
        }

        msg.replyTo().tell(positions.get(msg.platformId()));

        return Behaviors.same();
    }

    private Behavior<Team> onPlatformConfigurationRequest(
            DaedalusEvents.PlatformConfigurationRequest msg) {

        if (! configs.containsKey(msg.platformId())) {
            getContext().getLog().info("Failed to find platform configuration data for {}", msg.platformId());
            return Behaviors.same();
        }

        msg.replyTo().tell(configs.get(msg.platformId()));

        return Behaviors.same();
    }

}
