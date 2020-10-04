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
package com.comcept.daedalus.api.team;

import akka.actor.typed.ActorRef;
import com.comcept.daedalus.api.DaedalusEvents;
import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.ncct.typed.api.common.PlatformId;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * @author RDanna
 */
public interface TeamEvents {

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class RegisterListener implements Team {

        @Singular
        Map<Class<? extends Team>, ActorRef<Team>> subscriptionHandlers;

    }

    @SuppressWarnings("FallThrough")
    @Value(staticConstructor = "of")
    @Accessors(fluent = true)
    class RequestSelfPlatformId implements Team {

        @NonNull ActorRef<DaedalusEvents.PlatformIdUpdated> replyTo;

    }

    @SuppressWarnings("FallThrough")
    @Value(staticConstructor = "of")
    @Accessors(fluent = true)
    class TeamMembershipChanged implements Team {

        @NonNull Set<PlatformId> teamMembers;

    }

    @SuppressWarnings("FallThrough")
    @Value(staticConstructor = "of")
    @Accessors(fluent = true)
    class LeaderElected implements Team {

        @NonNull PlatformId leaderId;

    }

    @SuppressWarnings({"FallThrough", "cast"})
    @Builder
    @Value
    @Accessors(fluent = true)
    class TeamData implements Team {

        TeamId teamId;
        @Singular Map<PlatformId, DaedalusMessages.TeamPlatformPosition> platformPositions;
        @Singular Map<PlatformId, DaedalusMessages.TeamPlatformConfiguration> platformConfigurations;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class TeamDataRequest implements Team {

        TeamId teamId;
        ActorRef<TeamData> replyTo;

    }

}
