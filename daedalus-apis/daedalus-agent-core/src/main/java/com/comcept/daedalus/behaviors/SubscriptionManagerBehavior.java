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
import com.comcept.daedalus.api.msg.Daedalus;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.daedalus.msglib.DaedalusMessageType;
import com.comcept.ncct.typed.api.common.MsgType;
import com.comcept.ncct.zmq.actor.apis.NcctPim;
import com.typesafe.config.Config;
import comcept.ncct.msglib.CatalogType;
import comcept.ncct.msglib.NcctMessageType;
import java.util.List;

/**
 * Subscription manager.
 *
 * @author RDanna
 */
public class SubscriptionManagerBehavior extends AbstractBehavior<Team> {

    private final ActorRef<NcctPim.Command> pim;

    private TeamId teamId = TeamId.UNKNOWN;

    public static Behavior<Team> create(ActorRef<NcctPim.Command> pim, EventDepot eventDepot) {
        return Behaviors.setup(ctx -> new SubscriptionManagerBehavior(ctx, pim, eventDepot));
    }

    private SubscriptionManagerBehavior(ActorContext<Team> context,
                                        ActorRef<NcctPim.Command> pim,
                                        EventDepot eventDepot) {
        super(context);

        this.pim = pim;

        eventDepot.teamEventBus().subscribe(context.getSelf(), DaedalusEvents.TeamIdUpdated.class);

        subscribeToMessages();
    }

    @Override
    public Receive<Team> createReceive() {
        return newReceiveBuilder()
                .onMessage(DaedalusEvents.TeamIdUpdated.class, this::onTeamIdUpdated)
                .build();
    }

    private Behavior<Team> onTeamIdUpdated(DaedalusEvents.TeamIdUpdated event) {

        teamId = event.teamId();
        subscribeToMessages();

        return Behaviors.same();
    }

    private void subscribeToMessages() {
        Config config = getContext().getSystem().settings().config().getConfig("app.behaviors.message_subscription");
        List<? extends Config> subscriptions = config.getConfigList("subscribe_to");

        NcctPim.SubscribeTo.SubscribeToBuilder builder = NcctPim.SubscribeTo.builder();
        for (Config s : subscriptions) {
            String catalogName = s.getString("catalog");
            String typeName = s.getString("type");
            String subTypeName = s.hasPath("subtype") ? s.getString("subtype") : "";
            int catalog;
            int type;
            long subType;
            if (catalogName.equals("CT_NCCT_5")) {
                catalog = CatalogType.CT_NCCT_5_VALUE;
                type = NcctMessageType.valueOf(typeName).getNumber();
            } else if (catalogName.equals("Daedalus")) {
                catalog = Daedalus.Catalogs.DAEDALUS;
                type = DaedalusMessageType.valueOf(typeName).getNumber();
            } else {
                catalog = -1;
                type = -1;
            }
            if (subTypeName.equals("TEAMID")) {
                subType = teamId.id();
            } else if (subTypeName.equals("LOCAL")) {
                subType = Daedalus.SubTypes.LOCAL;
            } else {
                subType = Daedalus.SubTypes.GLOBAL;
            }
            builder.msgType(MsgType.of(catalog, type, subType));
        }

        pim.tell(builder.build());
    }

}
