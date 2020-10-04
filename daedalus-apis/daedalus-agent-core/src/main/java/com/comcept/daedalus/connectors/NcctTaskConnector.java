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
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.task.TaskEvents;
import com.comcept.daedalus.api.task.TaskInternal;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.NcctTyped;
import com.comcept.ncct.zmq.actor.apis.NcctPim;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NcctTaskConnector extends AbstractBehavior<Task> {

    private final ActorRef<NcctPim.Command> connector;

    // Downstream handlers
    private final Map<Class<? extends Task>, List<ActorRef<Task>>> subscriptionHandlers
            = new HashMap<>();

    /**
     * Create the NcctTaskConnector behavior.
     *
     * @param connector NCCT connector for sending/receiving events
     * @return NcctTaskConnector behavior
     */
    public static Behavior<Task> create(
            ActorRef<NcctPim.Command> connector,
            EventDepot eventDepot) {

        Behavior<Task> behavior =
                Behaviors.setup(ctx -> new NcctTaskConnector(ctx, connector, eventDepot));

        return Behaviors.supervise(behavior)
                .onFailure(SupervisorStrategy.restart());
    }

    private NcctTaskConnector(
            ActorContext<Task> context,
            ActorRef<NcctPim.Command> connector,
            EventDepot eventDepot) {
        super(context);

        this.connector = connector;

        ActorRef<NcctTyped> messageAdapter = context.messageAdapter(NcctTyped.class, NcctToTaskAdapter::from);

        connector.tell(NcctPim.RegisterHandlers.builder()
                .subscriptionHandler(GenericPayload.class, messageAdapter)
                .build());

        eventDepot.taskEventBus();
    }

    @Override
    public Receive<Task> createReceive() {
        return newReceiveBuilder()
                .onMessage(TaskEvents.RegisterListener.class, this::onRegisterListener)
                .onMessage(TaskMessages.TeamTask.class, this::forwardTeamTask)
                .onMessage(TaskMessages.TeamTaskStatus.class, this::forwardTeamTaskStatus)
                .onMessage(TaskMessages.FusionTask.class, this::forwardFusionTask)
                .onMessage(TaskMessages.FusionTaskStatus.class, this::forwardFusionTaskStatus)
                .onMessage(TaskMessages.PilotTask.class, this::forwardPilotTask)
                .onMessage(TaskMessages.PilotTaskCommand.class, this::forwardPilotTaskCommand)
                .onMessage(TaskMessages.PilotTaskStatus.class, this::forwardPilotTaskStatus)
                .onMessage(TaskInternal.SendMsg.class, this::onSendMsg)
                .build();
    }

    private Behavior<Task> onRegisterListener(TaskEvents.RegisterListener event) {

        for (Map.Entry<Class<? extends Task>, ActorRef<Task>> entry :
                event.subscriptionHandlers().entrySet()) {
            List<ActorRef<Task>> updated
                    = subscriptionHandlers.getOrDefault(entry.getKey(), new ArrayList<>());
            updated.add(entry.getValue());
            subscriptionHandlers.put(entry.getKey(), updated);
        }

        return Behaviors.same();
    }

    private Behavior<Task> forwardTeamTask(TaskMessages.TeamTask msg) {

        publishMsg(msg, TaskMessages.TeamTask.class);

        return Behaviors.same();
    }

    private Behavior<Task> forwardTeamTaskStatus(TaskMessages.TeamTaskStatus msg) {

        publishMsg(msg, TaskMessages.TeamTaskStatus.class);

        return Behaviors.same();
    }

    private Behavior<Task> forwardFusionTask(TaskMessages.FusionTask msg) {

        publishMsg(msg, TaskMessages.FusionTask.class);

        return Behaviors.same();
    }

    private Behavior<Task> forwardFusionTaskStatus(TaskMessages.FusionTaskStatus msg) {

        publishMsg(msg, TaskMessages.FusionTaskStatus.class);

        return Behaviors.same();
    }

    private Behavior<Task> forwardPilotTask(TaskMessages.PilotTask msg) {

        publishMsg(msg, TaskMessages.PilotTask.class);

        return Behaviors.same();
    }

    private Behavior<Task> forwardPilotTaskCommand(TaskMessages.PilotTaskCommand msg) {

        publishMsg(msg, TaskMessages.PilotTaskCommand.class);

        return Behaviors.same();
    }

    private Behavior<Task> forwardPilotTaskStatus(TaskMessages.PilotTaskStatus msg) {

        publishMsg(msg, TaskMessages.PilotTaskStatus.class);

        return Behaviors.same();
    }

    private Behavior<Task> onSendMsg(TaskInternal.SendMsg msg) {

        NcctTyped typed = TaskToNcctAdapter.from(msg.msg());
        if (typed != null) {
            connector.tell(NcctPim.SendMsg.of(msg.msgType(), msg.destination(), typed));
        }

        return Behaviors.same();
    }

    private <T extends Task> void publishMsg(T msg, Class<T> clazz) {
        List<ActorRef<Task>> refs = subscriptionHandlers.getOrDefault(clazz, Collections.emptyList());

        for (ActorRef<Task> ref : refs) {
            ref.tell(msg);
        }
    }

}
