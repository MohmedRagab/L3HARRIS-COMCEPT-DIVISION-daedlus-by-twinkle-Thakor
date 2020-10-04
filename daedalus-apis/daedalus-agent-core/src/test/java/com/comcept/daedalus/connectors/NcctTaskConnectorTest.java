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

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.task.TaskEvents;
import com.comcept.daedalus.api.task.TaskId;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.daedalus.msglib.FusionTaskMsg;
import com.comcept.daedalus.msglib.FusionTaskStatusMsg;
import com.comcept.daedalus.msglib.PilotTaskCommandMsg;
import com.comcept.daedalus.msglib.PilotTaskMsg;
import com.comcept.daedalus.msglib.PilotTaskStatusMsg;
import com.comcept.daedalus.msglib.TaskStatusType;
import com.comcept.daedalus.msglib.TeamTaskMsg;
import com.comcept.daedalus.msglib.TeamTaskStatusMsg;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SecurityClassification;
import com.comcept.ncct.zmq.actor.apis.NcctPim;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NcctTaskConnectorTest {

    static final ActorTestKit testKit = ActorTestKit.create();

    @BeforeAll
    static void setup() {
    }

    @AfterAll
    static void cleanup() {
        testKit.shutdownTestKit();
    }

    @Test
    void testRegisterListeners() {
        TestProbe<Task> teamTaskListener
                = testKit.createTestProbe();
        TestProbe<Task> teamTaskStatusListener
                = testKit.createTestProbe();
        TestProbe<Task> fusionTaskListener
                = testKit.createTestProbe();
        TestProbe<Task> fusionTaskStatusListener
                = testKit.createTestProbe();
        TestProbe<Task> pilotTaskListener
                = testKit.createTestProbe();
        TestProbe<Task> pilotTaskCommandListener
                = testKit.createTestProbe();
        TestProbe<Task> pilotTaskStatusListener
                = testKit.createTestProbe();

        TaskEvents.RegisterListener registerListener = TaskEvents.RegisterListener.builder()
                .subscriptionHandler(TaskMessages.TeamTask.class, teamTaskListener.getRef())
                .subscriptionHandler(TaskMessages.TeamTaskStatus.class, teamTaskStatusListener.getRef())
                .subscriptionHandler(TaskMessages.FusionTask.class, fusionTaskListener.getRef())
                .subscriptionHandler(TaskMessages.FusionTaskStatus.class, fusionTaskStatusListener.getRef())
                .subscriptionHandler(TaskMessages.PilotTask.class, pilotTaskListener.getRef())
                .subscriptionHandler(TaskMessages.PilotTaskCommand.class, pilotTaskCommandListener.getRef())
                .subscriptionHandler(TaskMessages.PilotTaskStatus.class, pilotTaskStatusListener.getRef())
                .build();

        TestProbe<NcctPim.Command> connector = testKit.createTestProbe();

        EventDepot eventDepot = EventDepot.create();

        ActorRef<Task> ref = testKit.spawn(
                NcctTaskConnector.create(connector.getRef(), eventDepot));

        ref.tell(registerListener);

        TaskMessages.TeamTask teamTask = TaskMessages.TeamTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .teamTaskMsg(TeamTaskMsg.newBuilder().build())
                .taskId(TaskId.of(1, 1))
                .build();

        ref.tell(teamTask);

        teamTaskListener.expectMessage(teamTask);
        teamTaskStatusListener.expectNoMessage();
        fusionTaskListener.expectNoMessage();
        fusionTaskStatusListener.expectNoMessage();
        pilotTaskListener.expectNoMessage();
        pilotTaskCommandListener.expectNoMessage();
        pilotTaskStatusListener.expectNoMessage();

        TaskMessages.TeamTaskStatus teamTaskStatus = TaskMessages.TeamTaskStatus.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .teamTaskStatusMsg(TeamTaskStatusMsg.newBuilder().build())
                .taskStatus(TaskStatusType.TST_ACCEPTED)
                .taskId(TaskId.of(1, 1))
                .build();

        ref.tell(teamTaskStatus);

        teamTaskListener.expectNoMessage();
        teamTaskStatusListener.expectMessage(teamTaskStatus);
        fusionTaskListener.expectNoMessage();
        fusionTaskStatusListener.expectNoMessage();
        pilotTaskListener.expectNoMessage();
        pilotTaskCommandListener.expectNoMessage();
        pilotTaskStatusListener.expectNoMessage();

        TaskMessages.FusionTask fusionTask = TaskMessages.FusionTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .fusionTaskMsg(FusionTaskMsg.newBuilder().build())
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .build();

        ref.tell(fusionTask);

        teamTaskListener.expectNoMessage();
        teamTaskStatusListener.expectNoMessage();
        fusionTaskListener.expectMessage(fusionTask);
        fusionTaskStatusListener.expectNoMessage();
        pilotTaskListener.expectNoMessage();
        pilotTaskCommandListener.expectNoMessage();
        pilotTaskStatusListener.expectNoMessage();

        TaskMessages.FusionTaskStatus fusionTaskStatus = TaskMessages.FusionTaskStatus.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .fusionTaskStatusMsg(FusionTaskStatusMsg.newBuilder().build())
                .taskStatus(TaskStatusType.TST_ACCEPTED)
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .build();

        ref.tell(fusionTaskStatus);

        teamTaskListener.expectNoMessage();
        teamTaskStatusListener.expectNoMessage();
        fusionTaskListener.expectNoMessage();
        fusionTaskStatusListener.expectMessage(fusionTaskStatus);
        pilotTaskListener.expectNoMessage();
        pilotTaskCommandListener.expectNoMessage();
        pilotTaskStatusListener.expectNoMessage();

        TaskMessages.PilotTask pilotTask = TaskMessages.PilotTask.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .pilotTaskMsg(PilotTaskMsg.newBuilder().build())
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .build();

        ref.tell(pilotTask);

        teamTaskListener.expectNoMessage();
        teamTaskStatusListener.expectNoMessage();
        fusionTaskListener.expectNoMessage();
        fusionTaskStatusListener.expectNoMessage();
        pilotTaskListener.expectMessage(pilotTask);
        pilotTaskCommandListener.expectNoMessage();
        pilotTaskStatusListener.expectNoMessage();

        TaskMessages.PilotTaskCommand pilotTaskCommand = TaskMessages.PilotTaskCommand.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .pilotTaskCommandMsg(PilotTaskCommandMsg.newBuilder().build())
                .taskId(TaskId.of(1, 1))
                .build();

        ref.tell(pilotTaskCommand);

        teamTaskListener.expectNoMessage();
        teamTaskStatusListener.expectNoMessage();
        fusionTaskListener.expectNoMessage();
        fusionTaskStatusListener.expectNoMessage();
        pilotTaskListener.expectNoMessage();
        pilotTaskCommandListener.expectMessage(pilotTaskCommand);
        pilotTaskStatusListener.expectNoMessage();

        TaskMessages.PilotTaskStatus pilotTaskStatus = TaskMessages.PilotTaskStatus.builder()
                .classification(SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .pilotTaskStatusMsg(PilotTaskStatusMsg.newBuilder().build())
                .taskStatus(TaskStatusType.TST_ACCEPTED)
                .taskId(TaskId.of(1, 1))
                .platformId(PlatformId.of(1, 1))
                .build();

        ref.tell(pilotTaskStatus);

        teamTaskListener.expectNoMessage();
        teamTaskStatusListener.expectNoMessage();
        fusionTaskListener.expectNoMessage();
        fusionTaskStatusListener.expectNoMessage();
        pilotTaskListener.expectNoMessage();
        pilotTaskCommandListener.expectNoMessage();
        pilotTaskStatusListener.expectMessage(pilotTaskStatus);

        // Check unknown handling
        ref.tell(new Task() {});

        teamTaskListener.expectNoMessage();
        teamTaskStatusListener.expectNoMessage();
        fusionTaskListener.expectNoMessage();
        fusionTaskStatusListener.expectNoMessage();
        pilotTaskListener.expectNoMessage();
        pilotTaskCommandListener.expectNoMessage();
        pilotTaskStatusListener.expectNoMessage();

        testKit.stop(ref);
    }

}
