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
package com.comcept.daedalus.behaviors.team.utils;

import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.ncct.typed.api.PlatformConfiguration;
import com.comcept.ncct.typed.api.PlatformPosition;
import com.comcept.ncct.typed.api.common.GeoPoint;
import com.comcept.ncct.typed.api.common.Identification;
import com.comcept.ncct.typed.api.common.MessageId;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SecurityClassification;
import com.comcept.ncct.typed.api.common.SourceId;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlatformDataUtilsTest {

    private final DaedalusMessages.TeamPlatformPosition origTeamPlatformPosition =
            DaedalusMessages.TeamPlatformPosition.builder()
                    .platformPosition(PlatformPosition.builder()
                            .classification(SecurityClassification.builder()
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
                            .build())
                    .build();

    private final DaedalusMessages.TeamPlatformConfiguration origTeamPlatformConfiguration =
            DaedalusMessages.TeamPlatformConfiguration.builder()
                    .platformConfiguration(PlatformConfiguration.builder()
                            .classification(SecurityClassification.builder()
                                    .capco("UNCLASSIFIED")
                                    .ownerTrigraph("USA")
                                    .build())
                            .messageId(MessageId.UNKNOWN)
                            .sourceId(SourceId.UNKNOWN)
                            .creationTime(Instant.ofEpochSecond(1234L))
                            .platformId(PlatformId.of(1, 1))
                            .subIdMin(100)
                            .subIdMax(100)
                            .build())
                    .build();

    private final DaedalusMessages.TeamPlatformPosition newerPlatform1Position =
            DaedalusMessages.TeamPlatformPosition.builder()
                    .platformPosition(PlatformPosition.builder()
                            .classification(SecurityClassification.builder()
                                    .capco("UNCLASSIFIED")
                                    .ownerTrigraph("USA")
                                    .build())
                            .messageId(MessageId.UNKNOWN)
                            .sourceId(SourceId.UNKNOWN)
                            .creationTime(Instant.now())
                            .platformId(PlatformId.of(1, 1))
                            .location(GeoPoint.builder()
                                    .latitude(12.0)
                                    .longitude(98.1)
                                    .build())
                            .identification(Identification.builder()
                                    .build())
                            .validUntil(Instant.EPOCH)
                            .build())
                    .build();

    private final DaedalusMessages.TeamPlatformPosition newerPlatform2Position =
            DaedalusMessages.TeamPlatformPosition.builder()
                    .platformPosition(PlatformPosition.builder()
                            .classification(SecurityClassification.builder()
                                    .capco("UNCLASSIFIED")
                                    .ownerTrigraph("USA")
                                    .build())
                            .messageId(MessageId.UNKNOWN)
                            .sourceId(SourceId.UNKNOWN)
                            .creationTime(Instant.now())
                            .platformId(PlatformId.of(2, 1))
                            .location(GeoPoint.builder()
                                    .latitude(12.0)
                                    .longitude(98.1)
                                    .build())
                            .identification(Identification.builder()
                                    .build())
                            .validUntil(Instant.EPOCH)
                            .build())
                    .build();

    private final DaedalusMessages.TeamPlatformPosition olderPlatform1Position =
            DaedalusMessages.TeamPlatformPosition.builder()
                    .platformPosition(PlatformPosition.builder()
                            .classification(SecurityClassification.builder()
                                    .capco("UNCLASSIFIED")
                                    .ownerTrigraph("USA")
                                    .build())
                            .messageId(MessageId.UNKNOWN)
                            .sourceId(SourceId.UNKNOWN)
                            .creationTime(Instant.ofEpochSecond(123L))
                            .platformId(PlatformId.of(1, 1))
                            .location(GeoPoint.builder()
                                    .latitude(12.0)
                                    .longitude(98.1)
                                    .build())
                            .identification(Identification.builder()
                                    .build())
                            .validUntil(Instant.EPOCH)
                            .build())
                    .build();

    private final DaedalusMessages.TeamPlatformPosition olderPlatform2Position =
            DaedalusMessages.TeamPlatformPosition.builder()
                    .platformPosition(PlatformPosition.builder()
                            .classification(SecurityClassification.builder()
                                    .capco("UNCLASSIFIED")
                                    .ownerTrigraph("USA")
                                    .build())
                            .messageId(MessageId.UNKNOWN)
                            .sourceId(SourceId.UNKNOWN)
                            .creationTime(Instant.ofEpochSecond(123L))
                            .platformId(PlatformId.of(2, 1))
                            .location(GeoPoint.builder()
                                    .latitude(12.0)
                                    .longitude(98.1)
                                    .build())
                            .identification(Identification.builder()
                                    .build())
                            .validUntil(Instant.EPOCH)
                            .build())
                    .build();

    private final DaedalusMessages.TeamPlatformConfiguration newerPlatform1Configuration =
            DaedalusMessages.TeamPlatformConfiguration.builder()
                    .platformConfiguration(PlatformConfiguration.builder()
                            .classification(SecurityClassification.builder()
                                    .capco("UNCLASSIFIED")
                                    .ownerTrigraph("USA")
                                    .build())
                            .messageId(MessageId.UNKNOWN)
                            .sourceId(SourceId.UNKNOWN)
                            .creationTime(Instant.now())
                            .platformId(PlatformId.of(1, 1))
                            .subIdMin(100)
                            .subIdMax(100)
                            .build())
                    .build();

    private final DaedalusMessages.TeamPlatformConfiguration newerPlatform2Configuration =
            DaedalusMessages.TeamPlatformConfiguration.builder()
                    .platformConfiguration(PlatformConfiguration.builder()
                            .classification(SecurityClassification.builder()
                                    .capco("UNCLASSIFIED")
                                    .ownerTrigraph("USA")
                                    .build())
                            .messageId(MessageId.UNKNOWN)
                            .sourceId(SourceId.UNKNOWN)
                            .creationTime(Instant.now())
                            .platformId(PlatformId.of(2, 1))
                            .subIdMin(100)
                            .subIdMax(100)
                            .build())
                    .build();

    private final DaedalusMessages.TeamPlatformConfiguration olderPlatform1Configuration =
            DaedalusMessages.TeamPlatformConfiguration.builder()
                    .platformConfiguration(PlatformConfiguration.builder()
                            .classification(SecurityClassification.builder()
                                    .capco("UNCLASSIFIED")
                                    .ownerTrigraph("USA")
                                    .build())
                            .messageId(MessageId.UNKNOWN)
                            .sourceId(SourceId.UNKNOWN)
                            .creationTime(Instant.ofEpochSecond(123L))
                            .platformId(PlatformId.of(1, 1))
                            .subIdMin(100)
                            .subIdMax(100)
                            .build())
                    .build();

    private final DaedalusMessages.TeamPlatformConfiguration olderPlatform2Configuration =
            DaedalusMessages.TeamPlatformConfiguration.builder()
                    .platformConfiguration(PlatformConfiguration.builder()
                            .classification(SecurityClassification.builder()
                                    .capco("UNCLASSIFIED")
                                    .ownerTrigraph("USA")
                                    .build())
                            .messageId(MessageId.UNKNOWN)
                            .sourceId(SourceId.UNKNOWN)
                            .creationTime(Instant.ofEpochSecond(123L))
                            .platformId(PlatformId.of(2, 1))
                            .subIdMin(100)
                            .subIdMax(100)
                            .build())
                    .build();


    @Test
    public void testIsNewer_PlatformPosition() {
        assertTrue(PlatformDataUtils.isNewer(
                origTeamPlatformPosition, newerPlatform1Position));

        assertFalse(PlatformDataUtils.isNewer(
                origTeamPlatformPosition, newerPlatform2Position));

        assertFalse(PlatformDataUtils.isNewer(
                origTeamPlatformPosition, olderPlatform1Position));

        assertFalse(PlatformDataUtils.isNewer(
                origTeamPlatformPosition, olderPlatform2Position));
    }

    @Test
    public void testIsNewer_PlatformConfiguration() {
        assertTrue(PlatformDataUtils.isNewer(
                origTeamPlatformConfiguration, newerPlatform1Configuration));

        assertFalse(PlatformDataUtils.isNewer(
                origTeamPlatformConfiguration, newerPlatform2Configuration));

        assertFalse(PlatformDataUtils.isNewer(
                origTeamPlatformConfiguration, olderPlatform1Configuration));

        assertFalse(PlatformDataUtils.isNewer(
                origTeamPlatformConfiguration, olderPlatform2Configuration));
    }

}
