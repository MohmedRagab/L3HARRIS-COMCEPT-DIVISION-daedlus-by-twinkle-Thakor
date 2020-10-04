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
package com.comcept.daedalus.eventbus;

import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import com.comcept.daedalus.api.DaedalusEvents;
import com.comcept.daedalus.api.sensor.Sensor;
import com.comcept.daedalus.api.sensor.SensorMessages;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.task.TaskInternal;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * DaedalusEventBus test.
 *
 * @author RDanna
 */
class EventDepotTest {

    static final ActorTestKit testKit = ActorTestKit.create();

    @BeforeAll
    static void setup() {
    }

    @AfterAll
    static void cleanup() {
        testKit.shutdownTestKit();
    }

    @Test
    void testTeamEventBus() {
        TestProbe<Team> teamSink = testKit.createTestProbe();
        TestProbe<Sensor> sensorSink = testKit.createTestProbe();

        TestProbe<Team> teamProbe = testKit.createTestProbe();
        TestProbe<Sensor> sensorProbe = testKit.createTestProbe();

        EventDepot depot = EventDepot.create();
        
        depot.teamEventBus().subscribe(teamSink.getRef(), Team.class);
        depot.teamEventBus().subscribe(teamProbe.getRef(), DaedalusEvents.TeamIdUpdated.class);

        DaedalusEvents.TeamIdUpdated event1 = DaedalusEvents.TeamIdUpdated.of(TeamId.of(3L));
        depot.teamEventBus().publish(event1);
        teamProbe.expectMessage(event1);
        teamSink.expectMessage(event1);

        Team event2 = new Team() {};
        depot.teamEventBus().publish(event2);
        teamProbe.expectNoMessage();
        teamSink.expectMessage(event2);
    }

    @Test
    void testTaskEventBus() {
        TestProbe<Task> taskSink = testKit.createTestProbe();
        TestProbe<Task> taskProbe = testKit.createTestProbe();

        EventDepot depot = EventDepot.create();

        depot.taskEventBus().subscribe(taskSink.getRef(), Task.class);
        depot.taskEventBus().subscribe(taskProbe.getRef(), TaskInternal.ParseError.class);

        TaskInternal.ParseError event1 = TaskInternal.ParseError.of("Foo");
        depot.taskEventBus().publish(event1);
        taskProbe.expectMessage(event1);
        taskSink.expectMessage(event1);
        
        Task event2 = new Task() {};
        depot.taskEventBus().publish(event2);
        taskProbe.expectNoMessage();
        taskSink.expectMessage(event2);
    }

    @Test
    void testSensorEventBus() {
        TestProbe<Sensor> sensorSink = testKit.createTestProbe();

        TestProbe<Sensor> sensorProbe = testKit.createTestProbe();

        EventDepot depot = EventDepot.create();

        depot.sensorEventBus().subscribe(sensorSink.getRef(), Sensor.class);
        depot.sensorEventBus().subscribe(sensorProbe.getRef(), SensorMessages.SensorStatus.class);

        SensorMessages.SensorStatus event1 = SensorMessages.SensorStatus.builder().build();
        depot.sensorEventBus().publish(event1);
        sensorProbe.expectMessage(event1);
        sensorSink.expectMessage(event1);

        Sensor event2 = new Sensor() {};
        depot.sensorEventBus().publish(event2);
        sensorProbe.expectNoMessage();
        sensorSink.expectMessage(event2);
    }

}