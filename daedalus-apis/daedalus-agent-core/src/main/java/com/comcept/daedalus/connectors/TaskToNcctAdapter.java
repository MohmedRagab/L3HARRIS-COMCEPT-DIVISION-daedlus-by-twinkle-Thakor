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

import com.comcept.daedalus.api.msg.Daedalus;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.NcctTyped;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskToNcctAdapter {

    /** Convert a TeamTask to an NCCT event.
     *
     * @param event Task event
     * @return NCCT event
     */
    public NcctTyped from(Task event) {
        if (event instanceof TaskMessages.TeamTask) {
            TaskMessages.TeamTask msg = (TaskMessages.TeamTask) event;
            return from(msg);
        } else if (event instanceof TaskMessages.TeamTaskStatus) {
            TaskMessages.TeamTaskStatus msg = (TaskMessages.TeamTaskStatus) event;
            return from(msg);
        } else if (event instanceof TaskMessages.FusionTask) {
            TaskMessages.FusionTask msg = (TaskMessages.FusionTask) event;
            return from(msg);
        } else if (event instanceof TaskMessages.FusionTaskStatus) {
            TaskMessages.FusionTaskStatus msg = (TaskMessages.FusionTaskStatus) event;
            return from(msg);
        } else if (event instanceof TaskMessages.PilotTask) {
            TaskMessages.PilotTask msg = (TaskMessages.PilotTask) event;
            return from(msg);
        } else if (event instanceof TaskMessages.PilotTaskCommand) {
            TaskMessages.PilotTaskCommand msg = (TaskMessages.PilotTaskCommand) event;
            return from(msg);
        } else if (event instanceof TaskMessages.PilotTaskStatus) {
            TaskMessages.PilotTaskStatus msg = (TaskMessages.PilotTaskStatus) event;
            return from(msg);
        } else {
            return null;
        }
    }

    /** Convert a TeamTask to an NCCT event.
     *
     * @param event Task event
     * @return NCCT event
     */
    public GenericPayload from(TaskMessages.TeamTask event) {
        return GenericPayload.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .msgType(Daedalus.TEAM_TASK_TEAM(TeamId.of(1L)))
                .payload(event.teamTaskMsg().toByteArray())
                .build();
    }

    /**
     * Convert a TeamTaskStatus to an NCCT event.
     *
     * @param event Task event
     * @return NCCT event
     */
    public GenericPayload from(TaskMessages.TeamTaskStatus event) {
        return GenericPayload.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .msgType(Daedalus.TEAM_TASK_STATUS_TEAM(TeamId.of(1L)))
                .payload(event.teamTaskStatusMsg().toByteArray())
                .build();
    }

    /**
     * Convert a FusionTask to an NCCT event.
     *
     * @param event Task event
     * @return NCCT event
     */
    public GenericPayload from(TaskMessages.FusionTask event) {
        return GenericPayload.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .msgType(Daedalus.FUSION_TASK_TEAM(TeamId.of(1L)))
                .payload(event.fusionTaskMsg().toByteArray())
                .build();
    }

    /**
     * Convert a FusionTaskStatus to an NCCT event.
     *
     * @param event Task event
     * @return NCCT event
     */
    public GenericPayload from(TaskMessages.FusionTaskStatus event) {
        return GenericPayload.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .msgType(Daedalus.FUSION_TASK_STATUS_TEAM(TeamId.of(1L)))
                .payload(event.fusionTaskStatusMsg().toByteArray())
                .build();
    }

    /**
     * Convert a PilotTask to an NCCT event.
     *
     * @param event Task event
     * @return NCCT event
     */
    public GenericPayload from(TaskMessages.PilotTask event) {
        return GenericPayload.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .msgType(Daedalus.PILOT_TASK_TEAM(TeamId.of(1L)))
                .payload(event.pilotTaskMsg().toByteArray())
                .build();
    }

    /**
     * Convert a PilotTaskCommand to an NCCT event.
     *
     * @param event Task event
     * @return NCCT event
     */
    public GenericPayload from(TaskMessages.PilotTaskCommand event) {
        return GenericPayload.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .msgType(Daedalus.PILOT_TASK_COMMAND_TEAM(TeamId.of(1L)))
                .payload(event.pilotTaskCommandMsg().toByteArray())
                .build();
    }

    /** Convert a PilotTaskStatus to an NCCT event.
     *
     * @param event Task event
     * @return NCCT event
     */
    public GenericPayload from(TaskMessages.PilotTaskStatus event) {
        return GenericPayload.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .msgType(Daedalus.PILOT_TASK_STATUS_TEAM(TeamId.of(1L)))
                .payload(event.pilotTaskStatusMsg().toByteArray())
                .build();
    }

}
