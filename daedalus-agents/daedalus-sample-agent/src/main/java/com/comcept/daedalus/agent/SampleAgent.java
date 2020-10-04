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

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import com.comcept.daedalus.behaviors.SampleTaskBehavior;
import com.comcept.daedalus.behaviors.SampleTeamBehavior;
import com.comcept.daedalus.msglib.AgentType;

/**
 * Sample agent guardian behavior.
 *
 * @author RDanna
 */
public final class SampleAgent extends AgentCore {

    public static Behavior<Void> create() {
        return Behaviors.setup(SampleAgent::new);
    }

    SampleAgent(ActorContext<Void> context) {
        super(context, AgentType.AT_UNKNOWN);

        // Start behaviors here
        // Pass one of teamConnector or taskConnector and eventDepot as arguments to the behavior
        addBehavior(SampleTeamBehavior.create(teamConnector, eventDepot), "SampleTeamBehavior");
        addBehavior(SampleTaskBehavior.create(taskConnector, eventDepot), "SampleTaskBehavior");
    }

}
