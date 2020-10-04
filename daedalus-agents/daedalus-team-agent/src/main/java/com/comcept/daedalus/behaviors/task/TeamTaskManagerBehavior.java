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
package com.comcept.daedalus.behaviors.task;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.daedalus.msglib.TeamTaskMsg;

public class TeamTaskManagerBehavior extends AbstractBehavior<Task> {

    private final ActorRef<Task> geolocation;
    private final ActorRef<Task> acquisition;
    private final ActorRef<Task> engagement;

    /**
     * Create a TeamTaskManagerBehavior.
     *
     * @param taskConnector Connector to send/receive tasks
     * @param eventDepot Event buses
     * @return TeamTaskManagerBehavior
     */
    public static Behavior<Task> create(
            ActorRef<Task> taskConnector,
            EventDepot eventDepot) {
        Behavior<Task> behavior =
                Behaviors.setup(context ->
                        new TeamTaskManagerBehavior(context, taskConnector, eventDepot));

        return Behaviors.supervise(behavior)
                .onFailure(SupervisorStrategy.restart());
    }

    private TeamTaskManagerBehavior(
            ActorContext<Task> context,
            ActorRef<Task> taskConnector,
            EventDepot eventDepot) {
        super(context);

        this.geolocation =
                context.spawn(
                        TargetGeolocationTaskBehavior.create(taskConnector, eventDepot),
                        "GeolocationTasker");
        this.acquisition =
                context.spawn(
                        TargetAcquisitionTaskBehavior.create(taskConnector, eventDepot),
                        "AcquisitionTasker");
        this.engagement =
                context.spawn(
                        TargetEngagementTaskBehavior.create(taskConnector, eventDepot),
                        "EngagementTasker");
    }

    @Override
    public Receive<Task> createReceive() {
        return newReceiveBuilder()
                .onMessage(TaskMessages.TeamTask.class, this::onTeamTask)
                .build();
    }

    private Behavior<Task> onTeamTask(TaskMessages.TeamTask msg) {
        TeamTaskMsg teamTaskMsg = msg.teamTaskMsg();
        switch (teamTaskMsg.getTaskDetails()
                .getTaskType()
                .getValue()) {
            case TTT_GEOLOCATE_TARGET:
                // Send to Geolocation behavior
                geolocation.tell(msg);
                break;
            case TTT_ACQUIRE_TARGET:
                // Send to Acquisition behavior
                acquisition.tell(msg);
                break;
            case TTT_ENGAGE_TARGET:
                // Sned to Engagement behavior
                engagement.tell(msg);
                break;
            default:
                // Handle unknown type
                getContext().getLog()
                        .warn("Received TeamTaskMsg with invalid tast type {}",
                                teamTaskMsg.getTaskDetails()
                                        .getTaskType()
                                        .getValue());
        }

        return Behaviors.same();
    }

}
