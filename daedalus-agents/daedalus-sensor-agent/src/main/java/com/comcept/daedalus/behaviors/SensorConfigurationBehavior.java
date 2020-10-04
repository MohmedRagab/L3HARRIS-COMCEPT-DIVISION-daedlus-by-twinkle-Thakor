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
import com.comcept.daedalus.api.sensor.Sensor;
import com.comcept.daedalus.api.sensor.SensorEvents;
import com.comcept.daedalus.api.sensor.SensorMessages;
import com.comcept.daedalus.eventbus.EventDepot;

/**
 * Sensor configuration behavior.
 * 
 * @author RDanna
 */
@SuppressWarnings("unused")
public class SensorConfigurationBehavior extends AbstractBehavior<Sensor> {

    private final ActorRef<Sensor> publisher;
    private final EventDepot eventDepot;

    public static Behavior<Sensor> create(
            ActorRef<Sensor> publisher,
            EventDepot eventDepot) {

        return Behaviors.setup(ctx -> new SensorConfigurationBehavior(ctx, publisher, eventDepot));
    }

    private SensorConfigurationBehavior(
            ActorContext<Sensor> context,
            ActorRef<Sensor> publisher,
            EventDepot eventDepot) {
        super(context);
        
        this.publisher = publisher;
        this.eventDepot = eventDepot;
        publisher.tell(SensorEvents.RegisterListener.builder()
                .subscriptionHandler(SensorMessages.SensorStatus.class, context.getSelf())
                .build());
    }

    @Override
    public Receive<Sensor> createReceive() {
        return newReceiveBuilder()
                .onMessage(SensorMessages.SensorStatus.class, this::onSensorStatus)
                .build();
    }

    private Behavior<Sensor> onSensorStatus(SensorMessages.SensorStatus status) {
        
        // Update platform configuration and send
        return Behaviors.same();
    }

}
