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
package com.comcept.daedalus.api.task;

import com.comcept.daedalus.msglib.FusionTaskMsg;
import com.comcept.daedalus.msglib.FusionTaskStatusMsg;
import com.comcept.daedalus.msglib.PilotTaskCommandMsg;
import com.comcept.daedalus.msglib.PilotTaskMsg;
import com.comcept.daedalus.msglib.PilotTaskStatusMsg;
import com.comcept.daedalus.msglib.TaskStatusType;
import com.comcept.daedalus.msglib.TeamTaskMsg;
import com.comcept.daedalus.msglib.TeamTaskStatusMsg;
import com.comcept.ncct.typed.api.common.MessageId;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SecurityClassification;
import com.comcept.ncct.typed.api.common.SourceId;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * @author RDanna
 */
public interface TaskMessages {

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class TeamTask implements Task {

        SecurityClassification classification;
        MessageId messageId;
        SourceId sourceId;
        Instant creationTime;
        TaskId taskId;
        TeamTaskMsg teamTaskMsg;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class TeamTaskStatus implements Task {

        SecurityClassification classification;
        MessageId messageId;
        SourceId sourceId;
        Instant creationTime;
        TaskId taskId;
        TaskStatusType taskStatus;
        TeamTaskStatusMsg teamTaskStatusMsg;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class FusionTask implements Task {

        SecurityClassification classification;
        MessageId messageId;
        SourceId sourceId;
        Instant creationTime;
        TaskId taskId;
        PlatformId platformId;
        FusionTaskMsg fusionTaskMsg;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class FusionTaskStatus implements Task {

        SecurityClassification classification;
        MessageId messageId;
        SourceId sourceId;
        Instant creationTime;
        TaskId taskId;
        PlatformId platformId;
        TaskStatusType taskStatus;
        FusionTaskStatusMsg fusionTaskStatusMsg;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class PilotTask implements Task {

        SecurityClassification classification;
        MessageId messageId;
        SourceId sourceId;
        Instant creationTime;
        TaskId taskId;
        PlatformId platformId;
        PilotTaskMsg pilotTaskMsg;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class PilotTaskCommand implements Task {

        SecurityClassification classification;
        MessageId messageId;
        SourceId sourceId;
        Instant creationTime;
        TaskId taskId;
        PilotTaskCommandMsg pilotTaskCommandMsg;

    }

    @SuppressWarnings("FallThrough")
    @Builder
    @Value
    @Accessors(fluent = true)
    class PilotTaskStatus implements Task {

        SecurityClassification classification;
        MessageId messageId;
        SourceId sourceId;
        Instant creationTime;
        TaskId taskId;
        PlatformId platformId;
        TaskStatusType taskStatus;
        PilotTaskStatusMsg pilotTaskStatusMsg;

    }

}
