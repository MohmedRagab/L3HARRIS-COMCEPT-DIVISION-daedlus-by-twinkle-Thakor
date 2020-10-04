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
import com.comcept.daedalus.api.task.TaskInternal;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.utils.TaskUtils;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.NcctTyped;
import com.comcept.ncct.typed.api.common.MsgType;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class for NCCT to Task protocol conversion.
 *
 * @author pborawski, rdanna
 */
public class NcctToTaskAdapter {

    private NcctToTaskAdapter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Create a Task event from an NCCT event.
     *
     * @param event NCCT event
     * @return Task event
     */
    public static Task from(NcctTyped event) {
        if (event instanceof GenericPayload) {
            GenericPayload msg = (GenericPayload) event;
            return from(msg);
        } else {
            return TaskInternal.ParseError.of("Unknown message type: " + event.getClass().toString());
        }
    }

    /**
     * Create a Task event from an NCCT generic payload.
     *
     * @param genericPayloadEvent Generic payload event
     * @return Task event
     */
    public static Task from(GenericPayload genericPayloadEvent) {
        
        MsgType msgType = genericPayloadEvent.msgType();

        if (msgType.equals(Daedalus.TEAM_TASK_TEAM(TeamId.of(1L)))) {
            return buildMsg(TaskUtils::createTeamTaskFrom, genericPayloadEvent);
        } else if (msgType.equals(Daedalus.TEAM_TASK_STATUS_TEAM(TeamId.of(1L)))) {
            return buildMsg(TaskUtils::createTeamTaskStatusFrom, genericPayloadEvent);
        } else if (msgType.equals(Daedalus.FUSION_TASK_TEAM(TeamId.of(1L)))) {
            return buildMsg(TaskUtils::createFusionTaskFrom, genericPayloadEvent);
        } else if (msgType.equals(Daedalus.FUSION_TASK_STATUS_TEAM(TeamId.of(1L)))) {
            return buildMsg(TaskUtils::createFusionTaskStatusFrom, genericPayloadEvent);
        } else if (msgType.equals(Daedalus.PILOT_TASK_TEAM(TeamId.of(1L)))) {
            return buildMsg(TaskUtils::createPilotTaskFrom, genericPayloadEvent);
        } else if (msgType.equals(Daedalus.PILOT_TASK_COMMAND_TEAM(TeamId.of(1L)))) {
            return buildMsg(TaskUtils::createPilotTaskCommandFrom, genericPayloadEvent);
        } else if (msgType.equals(Daedalus.PILOT_TASK_STATUS_TEAM(TeamId.of(1L)))) {
            return buildMsg(TaskUtils::createPilotTaskStatusFrom, genericPayloadEvent);
        } else {
            return TaskInternal.ParseError.of("Unknown message type: " + msgType.toString());
        }
    }

    private static <T extends Task> Task buildMsg(
            Function<GenericPayload, Optional<T>> fn,
            GenericPayload event) {

        Optional<T> maybeTask = fn.apply(event);

        if (maybeTask.isPresent()) {
            return maybeTask.get();
        } else {
            return TaskInternal.ParseError.of("Failed to deserialize generic payload");
        }
    }

}
