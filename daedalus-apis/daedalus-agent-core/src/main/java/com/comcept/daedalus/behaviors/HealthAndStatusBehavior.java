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
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.TimerScheduler;
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
import com.typesafe.config.Config;
import java.time.Duration;

/**
 * Health and status reporting behavior.
 *
 * @author RDanna
 */
public class HealthAndStatusBehavior extends AbstractBehavior<Team> {

    public static class Tick implements Team {
    }

    private final AgentType agentType;
    private final ActorRef<Team> publisher;

    private TeamId teamId = TeamId.UNKNOWN;
    private PlatformId id = PlatformId.UNKNOWN;

    /**
     * Create an instance of the Health and Status Behavior.
     *
     * @param publisher Team connector
     * @param agentType Agent type
     * @param eventDepot Event depot
     * @return New instance of the behavior
     */
    public static Behavior<Team> create(
            ActorRef<Team> publisher,
            EventDepot eventDepot,
            AgentType agentType) {

        Behavior<Team> behavior = Behaviors.setup(ctx ->
                Behaviors.withTimers(timers ->
                        new HealthAndStatusBehavior(ctx, timers, publisher, eventDepot, agentType)));

        return Behaviors.supervise(behavior).onFailure(SupervisorStrategy.restart());
    }

    private HealthAndStatusBehavior(
            ActorContext<Team> context,
            TimerScheduler<Team> timers,
            ActorRef<Team> publisher,
            EventDepot eventDepot,
            AgentType agentType) {
        super(context);

        this.agentType = agentType;
        this.publisher = publisher;

        eventDepot.teamEventBus().subscribe(context.getSelf(), DaedalusEvents.PlatformIdUpdated.class);
        eventDepot.teamEventBus().subscribe(context.getSelf(), DaedalusEvents.TeamIdUpdated.class);

        Config config = context.getSystem().settings().config();
        int statusRate = config.getInt("app.agent.timers.agent_status_interval");

        timers.startTimerAtFixedRate(new Tick(), Duration.ofMillis(statusRate));
    }

    @Override
    public Receive<Team> createReceive() {
        return newReceiveBuilder()
                .onMessage(DaedalusEvents.PlatformIdUpdated.class, this::onIdUpdated)
                .onMessage(Tick.class, this::onTick)
                .build();
    }

    private Behavior<Team> onIdUpdated(DaedalusEvents.PlatformIdUpdated event) {

        this.id = event.platformId();

        return Behaviors.same();
    }

    private Behavior<Team> onTick(Tick tick) {

        TeamMessages.HealthAndStatus healthAndStatus = TeamMessages.HealthAndStatus.builder()
                .healthAndStatusMsg(HealthAndStatusMsg.newBuilder()
                        .setAgentDetails(AgentStatusDetails.newBuilder()
                                .setAgentType(AgentTypeValue.newBuilder()
                                        .setValue(agentType))
                                .setStatus(HealthAndStatusTypeValue.newBuilder()
                                        .setValue(HealthAndStatusType.HST_NORMAL)))
                        .build())
                .build();

        publisher.tell(TeamInternal.SendMsg.of(
                Daedalus.HEALTH_AND_STATUS_TEAM(teamId),
                id.toSourceId(),
                healthAndStatus));

        return Behaviors.same();
    }

}
