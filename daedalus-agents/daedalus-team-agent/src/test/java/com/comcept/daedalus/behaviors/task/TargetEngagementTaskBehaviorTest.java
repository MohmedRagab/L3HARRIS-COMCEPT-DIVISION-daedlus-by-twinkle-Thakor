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

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.task.TaskEvents;
import com.comcept.daedalus.api.task.TaskId;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.daedalus.msglib.FusionTaskStatusMsg;
import com.comcept.daedalus.msglib.MessageState;
import com.comcept.daedalus.msglib.MessageStateValue;
import com.comcept.daedalus.msglib.PilotTaskStatusMsg;
import com.comcept.daedalus.msglib.TaskStatusType;
import com.comcept.daedalus.msglib.TeamTaskDetails;
import com.comcept.daedalus.msglib.TeamTaskMsg;
import com.comcept.daedalus.msglib.TeamTaskType;
import com.comcept.daedalus.msglib.TeamTaskTypeValue;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SecurityClassification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TargetEngagementTaskBehaviorTest {

    static final ActorTestKit testKit = ActorTestKit.create();

    @BeforeAll
    public static void setup() {
    }

    @AfterAll
    public static void cleanup() {
        testKit.shutdownTestKit();
    }

    @Test
    public void testTeamTaskMessage() {
        TestProbe<Task> dummyConnector = testKit.createTestProbe();
        ActorRef<Task> behavior = testKit.spawn(
                TargetEngagementTaskBehavior.create(
                        dummyConnector.getRef(),
                        EventDepot.create()));

        dummyConnector.expectMessageClass(TaskEvents.RegisterListener.class);

        TaskMessages.TeamTask teamTask = TaskMessages.TeamTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .taskId(TaskId.of(1, 1))
                .teamTaskMsg(TeamTaskMsg.newBuilder()
                        .setMessageState(MessageStateValue.newBuilder()
                                .setValue(MessageState.MS_NEW)
                                .build())
                        .setTaskDetails(TeamTaskDetails.newBuilder()
                                .setTaskType(TeamTaskTypeValue.newBuilder()
                                        .setValue(TeamTaskType.TTT_ACQUIRE_TARGET)
                                        .build())
                                .build())
                        .build())
                .build();

        behavior.tell(teamTask);
        dummyConnector.expectMessageClass(TaskMessages.TeamTaskStatus.class);
        dummyConnector.expectNoMessage();
        testKit.stop(behavior);
    }

    @Test
    public void testPilotTaskStatusMessage() {
        TestProbe<Task> dummyConnector = testKit.createTestProbe();
        ActorRef<Task> behavior = testKit.spawn(
                TargetEngagementTaskBehavior.create(
                        dummyConnector.getRef(),
                        EventDepot.create()));

        dummyConnector.expectMessageClass(TaskEvents.RegisterListener.class);

        TaskMessages.TeamTask teamTask = TaskMessages.TeamTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .taskId(TaskId.of(1, 1))
                .teamTaskMsg(TeamTaskMsg.newBuilder()
                        .setMessageState(MessageStateValue.newBuilder()
                                .setValue(MessageState.MS_NEW)
                                .build())
                        .setTaskDetails(TeamTaskDetails.newBuilder()
                                .setTaskType(TeamTaskTypeValue.newBuilder()
                                        .setValue(TeamTaskType.TTT_ACQUIRE_TARGET)
                                        .build())
                                .build())
                        .build())
                .build();

        behavior.tell(teamTask);
        dummyConnector.expectMessageClass(TaskMessages.TeamTaskStatus.class);

        TaskMessages.PilotTaskStatus pilotTaskStatus = TaskMessages.PilotTaskStatus.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .taskStatus(TaskStatusType.TST_ACCEPTED)
                .pilotTaskStatusMsg(PilotTaskStatusMsg.newBuilder()
                        .build())
                .build();

        behavior.tell(pilotTaskStatus);
        dummyConnector.expectMessageClass(TaskMessages.PilotTaskCommand.class);
        dummyConnector.expectNoMessage();
        testKit.stop(behavior);
    }

    @Test
    public void testFusionTaskStatusMessage() {
        TestProbe<Task> dummyConnector = testKit.createTestProbe();
        ActorRef<Task> behavior = testKit.spawn(
                TargetEngagementTaskBehavior.create(
                        dummyConnector.getRef(),
                        EventDepot.create()));

        dummyConnector.expectMessageClass(TaskEvents.RegisterListener.class);

        TaskMessages.TeamTask teamTask = TaskMessages.TeamTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .taskId(TaskId.of(1, 1))
                .teamTaskMsg(TeamTaskMsg.newBuilder()
                        .setMessageState(MessageStateValue.newBuilder()
                                .setValue(MessageState.MS_NEW)
                                .build())
                        .setTaskDetails(TeamTaskDetails.newBuilder()
                                .setTaskType(TeamTaskTypeValue.newBuilder()
                                        .setValue(TeamTaskType.TTT_ACQUIRE_TARGET)
                                        .build())
                                .build())
                        .build())
                .build();

        behavior.tell(teamTask);
        dummyConnector.expectMessageClass(TaskMessages.TeamTaskStatus.class);

        TaskMessages.FusionTaskStatus fusionTaskStatus = TaskMessages.FusionTaskStatus.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .taskStatus(TaskStatusType.TST_EXECUTING)
                .fusionTaskStatusMsg(FusionTaskStatusMsg.newBuilder()
                        .build())
                .build();

        behavior.tell(fusionTaskStatus);
        dummyConnector.expectMessageClass(TaskMessages.TeamTaskStatus.class);
        dummyConnector.expectNoMessage();
        testKit.stop(behavior);
    }

}
