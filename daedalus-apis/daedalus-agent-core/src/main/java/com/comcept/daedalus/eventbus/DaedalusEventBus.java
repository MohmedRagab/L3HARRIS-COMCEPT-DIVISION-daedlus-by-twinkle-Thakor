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

import akka.actor.typed.ActorRef;
import akka.event.japi.SubchannelEventBus;
import akka.util.Subclassification;

/**
 * Daedalus typed event bus.
 *
 * @author RDanna
 */
public class DaedalusEventBus<T> extends SubchannelEventBus<T, ActorRef<T>, Class<? extends T>> {

    private static class DaedalusSubclassification<T> implements Subclassification<Class<? extends T>> {

        @Override
        public boolean isEqual(Class<? extends T> x, Class<? extends T> y) {
            return x.equals(y);
        }

        @Override
        public boolean isSubclass(Class<? extends T> x, Class<? extends T> y) {
            return y.isAssignableFrom(x);
        }

    }

    @Override
    public Subclassification<Class<? extends T>> subclassification() {
        return new DaedalusSubclassification<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends T> classify(T event) {
        return (Class<? extends T>) event.getClass();
    }

    @Override
    public void publish(T event, ActorRef<T> subscriber) {
        subscriber.tell(event);
    }

}
