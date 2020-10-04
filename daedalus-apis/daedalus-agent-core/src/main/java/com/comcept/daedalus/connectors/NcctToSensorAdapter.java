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

import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.daedalus.api.sensor.Sensor;
import com.comcept.daedalus.api.sensor.SensorInternals;
import com.comcept.daedalus.utils.SensorUtils;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.NcctTyped;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.typed.api.common.MsgType;
import java.util.Optional;
import lombok.experimental.UtilityClass;

/**
 * Utility class for NCCT to Team protocol conversion.
 *
 * @author pborawski, rdanna
 */
@UtilityClass
public class NcctToSensorAdapter {

    /**
     * Create a Team event from an NCCT generic payload.
     *
     * @param msg NcctTyped
     * @return Team event
     */
    public Sensor from(NcctTyped msg) {

        if (msg instanceof PlatformConfiguration) {
            PlatformConfiguration platformConfiguration = (PlatformConfiguration) msg;
            return from(platformConfiguration);
        } else if (msg instanceof GenericPayload) {
            GenericPayload payload = (GenericPayload) msg;
            return from(payload);
        } else {
            return SensorInternals.ParseError.of("Unknown message type: " + msg.getClass().toString());
        }
    }

    /**
     * Create a Team event from an NCCT generic payload.
     *
     * @param genericPayloadEvent Generic payload event
     * @return Team event
     */
    public Sensor from(GenericPayload genericPayloadEvent) {

        MsgType msgType = genericPayloadEvent.msgType();

        return SensorInternals.ParseError.of("Unknown message type: " + msgType.toString());
    }
    
    /**
     * Create a Team event from an NCCT platform configuration.
     *
     * @param platformConfigurationEvent Platform configuration event
     * @return Team event
     */
    public Sensor from(PlatformConfiguration platformConfigurationEvent) {

        Optional<DaedalusMessages.TeamPlatformConfiguration> event =
                SensorUtils.createPlatformConfigurationFrom(platformConfigurationEvent);
        if (event.isPresent()) {
            return event.get();
        } else {
            return SensorInternals.ParseError.of("Failed to parse PlatformConfiguration data");
        }
    }

}
