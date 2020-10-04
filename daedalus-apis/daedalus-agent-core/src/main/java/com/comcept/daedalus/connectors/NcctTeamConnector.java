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
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.api.team.TeamInternal;
import com.comcept.daedalus.api.team.TeamMessages;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.NcctTyped;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.typed.api.PlatformPosition;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.zmq.actor.apis.NcctPim;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NcctTeamConnector extends AbstractBehavior<Team> {

    private final ActorRef<NcctPim.Command> connector;
    private final EventDepot eventDepot;
    private final ActorRef<NcctTyped> messageAdapter;

    private PlatformId platformId = PlatformId.UNKNOWN;
    private TeamId teamId = TeamId.UNKNOWN;

    // Downstream handlers
    private final Map<Class<? extends Team>, List<ActorRef<Team>>> subscriptionHandlers
            = new HashMap<>();

    /**
     * Create the NcctTeamConnector behavior.
     *
     * @param connector NCCT connector for sending/receiving events
     * @return NcctTeamConnector behavior
     */
    public static Behavior<Team> create(ActorRef<NcctPim.Command> connector, EventDepot eventDepot) {
        Behavior<Team> behavior =
                Behaviors.setup(ctx -> new NcctTeamConnector(ctx, connector, eventDepot));
        return Behaviors.supervise(behavior)
                .onFailure(SupervisorStrategy.restart());
    }

    private NcctTeamConnector(
            ActorContext<Team> context,
            ActorRef<NcctPim.Command> connector,
            EventDepot eventDepot) {
        super(context);

        this.connector = connector;
        this.eventDepot = eventDepot;
        this.messageAdapter = context.messageAdapter(NcctTyped.class, NcctToTeamAdapter::from);

        connector.tell(NcctPim.RegisterHandlers.builder()
                .subscriptionHandler(PlatformPosition.class, messageAdapter)
                .subscriptionHandler(PlatformConfiguration.class, messageAdapter)
                .subscriptionHandler(GenericPayload.class, messageAdapter)
                .subscriptionHandler(NcctPim.PimIdUpdated.class, messageAdapter)
                .build());
        connector.tell(NcctPim.GetPimId.of(messageAdapter));

        eventDepot.teamEventBus().subscribe(context.getSelf(), TeamEvents.RequestSelfPlatformId.class);
        eventDepot.teamEventBus().subscribe(context.getSelf(), DaedalusEvents.TeamIdUpdated.class);
    }

    @Override
    public Receive<Team> createReceive() {
        return newReceiveBuilder()
                .onMessage(TeamEvents.RegisterListener.class,
                        this::onRegisterListener)
                .onMessage(TeamEvents.RequestSelfPlatformId.class,
                        this::onRequestSelfPlatformId)
                .onMessage(DaedalusEvents.PlatformIdUpdated.class,
                        this::forwardSelfPlatformIdUpdated)
                .onMessage(DaedalusEvents.TeamIdUpdated.class,
                        this::forwardTeamIdUpdated)
                .onMessage(DaedalusMessages.TeamPlatformPosition.class,
                        this::forwardPlatformPosition)
                .onMessage(DaedalusMessages.TeamPlatformConfiguration.class,
                        this::forwardPlatformConfiguration)
                .onMessage(TeamMessages.TeamAssignment.class,
                        this::forwardTeamAssignment)
                .onMessage(TeamMessages.TeamInvitation.class,
                        this::forwardTeamInvitation)
                .onMessage(TeamMessages.TeamInvitationResponse.class,
                        this::forwardTeamInvitationResponse)
                .onMessage(TeamMessages.TeamRegistration.class,
                        this::forwardTeamRegistration)
                .onMessage(TeamInternal.SendMsg.class,
                        this::onSendMsg)
                .build();
    }

    private Behavior<Team> onRegisterListener(TeamEvents.RegisterListener event) {

        for (Map.Entry<Class<? extends Team>, ActorRef<Team>> entry :
                event.subscriptionHandlers().entrySet()) {
            List<ActorRef<Team>> updated
                    = subscriptionHandlers.getOrDefault(entry.getKey(), new ArrayList<>());
            updated.add(entry.getValue());
            subscriptionHandlers.put(entry.getKey(), updated);
        }

        return Behaviors.same();
    }

    private Behavior<Team> onRequestSelfPlatformId(TeamEvents.RequestSelfPlatformId event) {

        event.replyTo().tell(DaedalusEvents.PlatformIdUpdated.of(platformId));

        return Behaviors.same();
    }

    private Behavior<Team> forwardSelfPlatformIdUpdated(
            DaedalusEvents.PlatformIdUpdated msg) {

        platformId = msg.platformId();
        eventDepot.teamEventBus().publish(msg);

        return Behaviors.same();
    }

    private Behavior<Team> forwardTeamIdUpdated(
            DaedalusEvents.TeamIdUpdated msg) {

        teamId = msg.teamId();
        eventDepot.teamEventBus().publish(msg);

        return Behaviors.same();
    }

    private Behavior<Team> forwardPlatformPosition(
            DaedalusMessages.TeamPlatformPosition msg) {

        publishMsg(msg, DaedalusMessages.TeamPlatformPosition.class);

        return Behaviors.same();
    }

    private Behavior<Team> forwardPlatformConfiguration(
            DaedalusMessages.TeamPlatformConfiguration msg) {

        publishMsg(msg, DaedalusMessages.TeamPlatformConfiguration.class);

        return Behaviors.same();
    }

    private Behavior<Team> forwardTeamAssignment(
            TeamMessages.TeamAssignment msg) {

        publishMsg(msg, TeamMessages.TeamAssignment.class);

        return Behaviors.same();
    }

    private Behavior<Team> forwardTeamInvitation(
            TeamMessages.TeamInvitation msg) {

        publishMsg(msg, TeamMessages.TeamInvitation.class);

        return Behaviors.same();
    }

    private Behavior<Team> forwardTeamInvitationResponse(
            TeamMessages.TeamInvitationResponse msg) {

        publishMsg(msg, TeamMessages.TeamInvitationResponse.class);

        return Behaviors.same();
    }

    private Behavior<Team> forwardTeamRegistration(
            TeamMessages.TeamRegistration msg) {

        if (!teamId.equals(msg.teamId())) {
            teamId = msg.teamId();
            getContext().getSelf().tell(DaedalusEvents.TeamIdUpdated.of(teamId));
        }

        publishMsg(msg, TeamMessages.TeamRegistration.class);

        return Behaviors.same();
    }

    private Behavior<Team> onSendMsg(TeamInternal.SendMsg msg) {

        NcctTyped typed = TeamToNcctAdapter.from(msg.msg());
        if (typed != null) {
            connector.tell(NcctPim.SendMsg.of(msg.msgType(), msg.destination(), typed));
        }

        return Behaviors.same();
    }

    private <T extends Team> void publishMsg(T msg, Class<T> clazz) {

        List<ActorRef<Team>> refs = subscriptionHandlers.getOrDefault(clazz, Collections.emptyList());

        for (ActorRef<Team> ref : refs) {
            ref.tell(msg);
        }
    }

}
