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

import com.comcept.daedalus.msglib.HealthAndStatusMsg;
import com.comcept.daedalus.msglib.TeamAssignmentMsg;
import com.comcept.daedalus.msglib.TeamInvitationMsg;
import com.comcept.daedalus.msglib.TeamInvitationResponseMsg;
import com.comcept.daedalus.msglib.TeamRegistrationMsg;
import com.comcept.ncct.typed.api.common.MessageId;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SecurityClassification;
import com.comcept.ncct.typed.api.common.SourceId;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * @author RDanna
 */
public interface TeamMessages {

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class HealthAndStatus implements Team {

        HealthAndStatusMsg healthAndStatusMsg;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class TeamAssignment implements Team {

        SecurityClassification classification;
        MessageId messageId;
        SourceId sourceId;
        Instant creationTime;
        TeamId teamId;
        TeamAssignmentMsg teamAssignmentMsg;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class TeamInvitation implements Team {

        SecurityClassification classification;
        MessageId messageId;
        SourceId sourceId;
        Instant creationTime;
        PlatformId leaderId;
        TeamInvitationMsg teamInvitationMsg;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class TeamInvitationResponse implements Team {

        SecurityClassification classification;
        MessageId messageId;
        SourceId sourceId;
        Instant creationTime;
        TeamInvitationResponseMsg teamInvitationResponseMsg;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class TeamRegistration implements Team {

        SecurityClassification classification;
        MessageId messageId;
        SourceId sourceId;
        Instant creationTime;
        TeamId teamId;
        PlatformId leaderId;
        @Singular
        List<PlatformId> memberIds;
        TeamRegistrationMsg teamRegistrationMsg;

    }

}
