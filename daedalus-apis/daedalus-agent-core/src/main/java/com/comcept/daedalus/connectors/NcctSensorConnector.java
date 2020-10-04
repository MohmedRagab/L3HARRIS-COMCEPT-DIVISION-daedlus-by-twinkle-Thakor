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
import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.daedalus.api.sensor.Sensor;
import com.comcept.daedalus.api.sensor.SensorEvents;
import com.comcept.daedalus.api.sensor.SensorMessages;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.NcctTyped;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.zmq.actor.apis.NcctPim;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NcctSensorConnector extends AbstractBehavior<Sensor> {

    private final ActorRef<NcctTyped> messageAdapter;

    // Downstream handlers
    private final Map<Class<? extends Sensor>,
            List<ActorRef<Sensor>>> subscriptionHandlers = new HashMap<>();

    /**
     * Create the NcctTeamConnector behavior.
     *
     * @param connector NCCT connector for sending/receiving events
     * @return NcctTeamConnector behavior
     */
    public static Behavior<Sensor> create(
            ActorRef<NcctPim.Command> connector,
            EventDepot eventDepot) {

        Behavior<Sensor> behavior =
                Behaviors.setup(ctx -> new NcctSensorConnector(ctx, connector, eventDepot));

        return Behaviors.supervise(behavior)
                .onFailure(SupervisorStrategy.restart());
    }

    private NcctSensorConnector(
            ActorContext<Sensor> context,
            ActorRef<NcctPim.Command> connector,
            EventDepot eventDepot) {
        super(context);

        this.messageAdapter = context.messageAdapter(NcctTyped.class, NcctToSensorAdapter::from);

        connector.tell(NcctPim.RegisterHandlers.builder()
                .subscriptionHandler(PlatformConfiguration.class, messageAdapter)
                .subscriptionHandler(GenericPayload.class, messageAdapter)
                .subscriptionHandler(NcctPim.PimIdUpdated.class, messageAdapter)
                .build());

        eventDepot.sensorEventBus();
    }

    @Override
    public Receive<Sensor> createReceive() {
        return newReceiveBuilder()
                .onMessage(SensorEvents.RegisterListener.class,
                        this::onRegisterListener)
                .onMessage(DaedalusMessages.TeamPlatformConfiguration.class,
                        this::forwardPlatformConfiguration)
                .onMessage(SensorMessages.SensorStatus.class,
                        this::forwardSensorStatus)
                .build();
    }

    private Behavior<Sensor> onRegisterListener(SensorEvents.RegisterListener event) {

        for (Map.Entry<Class<? extends Sensor>, ActorRef<Sensor>> entry :
                event.subscriptionHandlers().entrySet()) {
            List<ActorRef<Sensor>> updated
                    = subscriptionHandlers.getOrDefault(entry.getKey(), new ArrayList<>());
            updated.add(entry.getValue());
            subscriptionHandlers.put(entry.getKey(), updated);
        }

        return Behaviors.same();
    }

    private Behavior<Sensor> forwardPlatformConfiguration(
            DaedalusMessages.TeamPlatformConfiguration msg) {

        publishMsg(msg, DaedalusMessages.TeamPlatformConfiguration.class);

        return Behaviors.same();
    }

    private Behavior<Sensor> forwardSensorStatus(
            SensorMessages.SensorStatus msg) {

        publishMsg(msg, SensorMessages.SensorStatus.class);

        return Behaviors.same();
    }

    private <T extends Sensor> void publishMsg(T msg, Class<T> clazz) {
        List<ActorRef<Sensor>> refs = subscriptionHandlers.getOrDefault(clazz, Collections.emptyList());

        for (ActorRef<Sensor> ref : refs) {
            ref.tell(msg);
        }
    }

}
