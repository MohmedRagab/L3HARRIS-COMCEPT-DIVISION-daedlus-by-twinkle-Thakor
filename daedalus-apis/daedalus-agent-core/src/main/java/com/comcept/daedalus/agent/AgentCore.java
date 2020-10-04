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
package com.comcept.daedalus.agent;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import com.comcept.daedalus.api.sensor.Sensor;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.behaviors.HealthAndStatusBehavior;
import com.comcept.daedalus.behaviors.SubscriptionManagerBehavior;
import com.comcept.daedalus.connectors.NcctSensorConnector;
import com.comcept.daedalus.connectors.NcctTaskConnector;
import com.comcept.daedalus.connectors.NcctTeamConnector;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.daedalus.msglib.AgentType;
import com.comcept.ncct.zmq.actor.NcctConnectionManager;
import com.comcept.ncct.zmq.actor.ZeromqDealer;
import com.comcept.ncct.zmq.actor.apis.NcctPim;

/**
 * Base agent core.
 *
 * @author RDanna
 */
public abstract class AgentCore extends AbstractBehavior<Void> {

    protected final EventDepot eventDepot;

    protected final ActorRef<NcctPim.Command> ncctPim;
    protected final ActorRef<Team> teamConnector;
    protected final ActorRef<Task> taskConnector;
    protected final ActorRef<Sensor> sensorConnector;

    AgentCore(ActorContext<Void> context, AgentType agentType) {
        super(context);

        this.eventDepot = EventDepot.create();

        // Create an actor to publish team domain events
        this.ncctPim = context.spawn(NcctConnectionManager.create(new ZeromqDealer()), "NcctConnection");
        this.teamConnector = context.spawn(NcctTeamConnector.create(ncctPim, eventDepot), "TeamConnector");
        this.taskConnector = context.spawn(NcctTaskConnector.create(ncctPim, eventDepot), "TaskConnector");
        this.sensorConnector = context.spawn(NcctSensorConnector.create(ncctPim, eventDepot), "SensorConnector");

        // Start standard behaviors
        context.spawn(SubscriptionManagerBehavior.create(ncctPim, eventDepot), "SubscriptionManager");
        context.spawn(HealthAndStatusBehavior.create(teamConnector, eventDepot, agentType), "HealthAndStatus");
    }

    public <T> void addBehavior(Behavior<T> behavior, String name) {
        getContext().spawn(behavior, name);
    }

    @Override
    public final Receive<Void> createReceive() {
        return newReceiveBuilder().build();
    }

}
