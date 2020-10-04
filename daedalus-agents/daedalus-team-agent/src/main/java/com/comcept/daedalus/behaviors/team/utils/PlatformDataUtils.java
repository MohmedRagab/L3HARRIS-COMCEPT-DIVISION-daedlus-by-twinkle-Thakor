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
import com.comcept.ncct.typed.api.common.PlatformId;
import java.time.Instant;

public class PlatformDataUtils {

    private PlatformDataUtils() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Update the platform position in a platform data object (only if newer).
     *
     * @param existing Existing platform position
     * @param updated Updated platform position
     * @return New platform data with updated position
     */
    public static boolean isNewer(
            DaedalusMessages.TeamPlatformPosition existing,
            DaedalusMessages.TeamPlatformPosition updated) {

        PlatformId existingId = existing.platformPosition().platformId();
        PlatformId updatedId = updated.platformPosition().platformId();

        if (! existingId.equals(updatedId)) {
            return false;
        }

        Instant existingTime = existing.platformPosition().creationTime();
        Instant updatedTime = updated.platformPosition().creationTime();

        return (updatedTime.getEpochSecond() > existingTime.getEpochSecond());
    }

    /**
     * Update the platform configuration in a platform data object (only if newer).
     *
     * @param existing Existing platform configuration
     * @param updated Updated platform configuration
     * @return New platform data with updated configuration
     */
    public static boolean isNewer(
            DaedalusMessages.TeamPlatformConfiguration existing,
            DaedalusMessages.TeamPlatformConfiguration updated) {

        PlatformId existingId = existing.platformConfiguration().platformId();
        PlatformId updatedId = updated.platformConfiguration().platformId();

        if (! existingId.equals(updatedId)) {
            return false;
        }

        Instant existingTime = existing.platformConfiguration().creationTime();
        Instant updatedTime = updated.platformConfiguration().creationTime();

        return (updatedTime.getEpochSecond() > existingTime.getEpochSecond());
    }

}
