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
package com.comcept.daedalus.api;

import com.comcept.daedalus.api.sensor.Sensor;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.team.Team;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.typed.api.PlatformPosition;
import com.comcept.ncct.typed.api.common.GeoPoint;
import com.comcept.ncct.typed.api.common.Identification;
import com.comcept.ncct.typed.api.common.MessageId;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SourceId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Daedalus Messages Test.
 *
 * @author rrana
 */
class DaedalusMessagesTest {

    @Test
    void checkMessageType() {

        PlatformPosition platformPosition = PlatformPosition.builder()
                .classification(com.comcept.ncct.typed.api.common.SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.ofEpochSecond(1234L))
                .platformId(PlatformId.of(1, 1))
                .location(GeoPoint.builder()
                        .latitude(12.0)
                        .longitude(98.1)
                        .build())
                .identification(Identification.builder()
                        .build())
                .validUntil(Instant.EPOCH)
                .build();

        PlatformConfiguration platformConfiguration = PlatformConfiguration.builder()
                .classification(com.comcept.ncct.typed.api.common.SecurityClassification.builder()
                        .capco("UNCLASSIFIED")
                        .ownerTrigraph("USA")
                        .build())
                .messageId(MessageId.UNKNOWN)
                .sourceId(SourceId.UNKNOWN)
                .creationTime(Instant.ofEpochSecond(1234L))
                .platformId(PlatformId.of(1, 1))
                .subIdMin(100)
                .subIdMax(100)
                .build();

        Team teamPlatformPosition = DaedalusMessages.TeamPlatformPosition
                .builder().platformPosition(platformPosition).build();

        assertTrue(teamPlatformPosition instanceof Team);
        assertTrue(teamPlatformPosition instanceof Task);
        assertTrue(teamPlatformPosition instanceof Sensor);

        teamPlatformPosition = null;
        assertFalse(teamPlatformPosition instanceof DaedalusMessages.TeamPlatformPosition);


        Task teamPlatformConfiguration = DaedalusMessages.TeamPlatformConfiguration.builder()
                .platformConfiguration(platformConfiguration).build();

        assertTrue(teamPlatformConfiguration instanceof Team);
        assertTrue(teamPlatformConfiguration instanceof Task);
        assertTrue(teamPlatformConfiguration instanceof Sensor);

        teamPlatformConfiguration = null;
        assertFalse(teamPlatformConfiguration instanceof DaedalusMessages.TeamPlatformConfiguration);

    }
}