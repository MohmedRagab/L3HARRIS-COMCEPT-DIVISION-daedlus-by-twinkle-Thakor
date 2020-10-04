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

import akka.actor.typed.ActorRef;
import com.comcept.daedalus.api.sensor.Sensor;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.ncct.typed.api.common.PlatformId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Daedalus Events.
 *
 * @author rrana
 */
public interface DaedalusEvents {

    @SuppressWarnings("FallThrough")
    @Value(staticConstructor = "of")
    @Accessors(fluent = true)
    class PlatformIdUpdated implements Team, Task, Sensor {

        @NonNull PlatformId platformId;

    }

    @SuppressWarnings("FallThrough")
    @Value(staticConstructor = "of")
    @Accessors(fluent = true)
    class TeamIdUpdated implements Team, Task, Sensor {

        @NonNull TeamId teamId;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class PlatformPositionRequest implements Team, Task, Sensor {

        PlatformId platformId;
        ActorRef<DaedalusMessages.TeamPlatformPosition> replyTo;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class PlatformConfigurationRequest implements Team, Task, Sensor {

        PlatformId platformId;
        ActorRef<DaedalusMessages.TeamPlatformConfiguration> replyTo;

    }

}
