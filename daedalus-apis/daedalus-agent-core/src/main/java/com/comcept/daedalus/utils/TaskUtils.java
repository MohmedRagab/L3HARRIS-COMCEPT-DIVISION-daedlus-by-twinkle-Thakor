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
package com.comcept.daedalus.utils;

import com.comcept.daedalus.api.msg.Daedalus;
import com.comcept.daedalus.api.task.Task;
import com.comcept.daedalus.api.task.TaskId;
import com.comcept.daedalus.api.task.TaskMessages;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.api.utils.MsgUtils;
import com.comcept.daedalus.msglib.FusionTaskMsg;
import com.comcept.daedalus.msglib.FusionTaskStatusMsg;
import com.comcept.daedalus.msglib.PilotTaskCommandMsg;
import com.comcept.daedalus.msglib.PilotTaskMsg;
import com.comcept.daedalus.msglib.PilotTaskStatusMsg;
import com.comcept.daedalus.msglib.TeamTaskMsg;
import com.comcept.daedalus.msglib.TeamTaskStatusMsg;
import com.comcept.ncct.typed.api.GenericPayload;
import com.comcept.ncct.typed.api.common.MsgType;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SourceId;
import com.comcept.ncct.typed.mapper.utils.NcctIdUtils;
import com.comcept.ncct.zmq.actor.apis.NcctPim;
import com.google.protobuf.Int32Value;
import comcept.ncct.msglib.NcctId;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskUtils {

    /**
     * Get a task id for an NCCT id.
     *
     * @param ncctId NCCT id
     * @return TaskId if successful, empty otherwise
     */
    public TaskId toTaskId(NcctId ncctId) {
        return TaskId.of(ncctId.getNncId().getValue(), ncctId.getSubId().getValue());
    }

    /**
     * Convert a task id to an NCCT id.
     *
     * @param taskId Task id
     * @return NCCT id
     */
    public NcctId toNcctId(TaskId taskId) {
        return NcctId.newBuilder()
                .setNncId(Int32Value.of(taskId.id()))
                .setSubId(Int32Value.of(taskId.subId()))
                .build();
    }

    /**
     * Parse a team task from an NCCT generic payload.
     *
     * @param event NCCT generic payload
     * @return TeamTask or empty if failed
     */
    public Optional<TaskMessages.TeamTask> createTeamTaskFrom(GenericPayload event) {
        Optional<TeamTaskMsg> teamTaskMsg =
                MsgUtils.parsePayload(TeamTaskMsg::parseFrom, event.payload());

        if (teamTaskMsg.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(TaskMessages.TeamTask.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .taskId(toTaskId(NcctIdUtils.toNcctId(event.messageId())))
                .teamTaskMsg(teamTaskMsg.get())
                .build());
    }

    /**
     * Parse a team task status from an NCCT generic payload.
     *
     * @param event NCCT generic payload
     * @return TeamTaskStatus or empty if failed
     */
    public Optional<TaskMessages.TeamTaskStatus> createTeamTaskStatusFrom(GenericPayload event) {
        Optional<TeamTaskStatusMsg> teamTaskStatusMsg =
                MsgUtils.parsePayload(TeamTaskStatusMsg::parseFrom, event.payload());

        if (teamTaskStatusMsg.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(TaskMessages.TeamTaskStatus.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .taskId(toTaskId(NcctIdUtils.toNcctId(event.messageId())))
                .taskStatus(teamTaskStatusMsg.get()
                        .getTeamTaskStatus()
                        .getValue())
                .teamTaskStatusMsg(teamTaskStatusMsg.get())
                .build());
    }

    /**
     * Parse a fusion task from an NCCT generic payload.
     *
     * @param event NCCT generic payload
     * @return FusionTask or empty if failed
     */
    public Optional<TaskMessages.FusionTask> createFusionTaskFrom(GenericPayload event) {
        Optional<FusionTaskMsg> fusionTaskMsg =
                MsgUtils.parsePayload(FusionTaskMsg::parseFrom, event.payload());

        if (fusionTaskMsg.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(TaskMessages.FusionTask.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .platformId(PlatformId.of(event.sourceId()))
                .taskId(toTaskId(NcctIdUtils.toNcctId(event.messageId())))
                .fusionTaskMsg(fusionTaskMsg.get())
                .build());
    }

    /**
     * Parse a fusion task status from an NCCT generic payload.
     *
     * @param event NCCT generic payload
     * @return FusionTaskStatus or empty if failed
     */
    public Optional<TaskMessages.FusionTaskStatus> createFusionTaskStatusFrom(GenericPayload event) {
        Optional<FusionTaskStatusMsg> fusionTaskStatusMsg =
                MsgUtils.parsePayload(FusionTaskStatusMsg::parseFrom, event.payload());

        if (fusionTaskStatusMsg.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(TaskMessages.FusionTaskStatus.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .platformId(PlatformId.of(event.sourceId()))
                .taskId(toTaskId(NcctIdUtils.toNcctId(event.messageId())))
                .taskStatus(fusionTaskStatusMsg.get()
                        .getFusionTaskStatus()
                        .getValue())
                .fusionTaskStatusMsg(fusionTaskStatusMsg.get())
                .build());
    }

    /**
     * Parse a pilot task from an NCCT generic payload.
     *
     * @param event NCCT generic payload
     * @return PilotTask or empty if failed
     */
    public Optional<TaskMessages.PilotTask> createPilotTaskFrom(GenericPayload event) {
        Optional<PilotTaskMsg> pilotTaskMsg =
                MsgUtils.parsePayload(PilotTaskMsg::parseFrom, event.payload());

        if (pilotTaskMsg.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(TaskMessages.PilotTask.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .platformId(PlatformId.of(event.sourceId()))
                .taskId(toTaskId(NcctIdUtils.toNcctId(event.messageId())))
                .pilotTaskMsg(pilotTaskMsg.get())
                .build());
    }

    /**
     * Parse a pilot task command from an NCCT generic payload.
     *
     * @param event NCCT generic payload
     * @return PilotTaskCommand or empty if failed
     */
    public Optional<TaskMessages.PilotTaskCommand> createPilotTaskCommandFrom(GenericPayload event) {
        Optional<PilotTaskCommandMsg> pilotTaskCommandMsg =
                MsgUtils.parsePayload(PilotTaskCommandMsg::parseFrom, event.payload());

        if (pilotTaskCommandMsg.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(TaskMessages.PilotTaskCommand.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .taskId(toTaskId(NcctIdUtils.toNcctId(event.messageId())))
                .pilotTaskCommandMsg(pilotTaskCommandMsg.get())
                .build());
    }

    /**
     * Parse a pilot task status from an NCCT generic payload.
     *
     * @param event NCCT generic payload
     * @return PilotTaskStatus or empty if failed
     */
    public Optional<TaskMessages.PilotTaskStatus> createPilotTaskStatusFrom(GenericPayload event) {
        Optional<PilotTaskStatusMsg> pilotTaskStatusMsg =
                MsgUtils.parsePayload(PilotTaskStatusMsg::parseFrom, event.payload());

        if (pilotTaskStatusMsg.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(TaskMessages.PilotTaskStatus.builder()
                .classification(event.classification())
                .sourceId(event.sourceId())
                .messageId(event.messageId())
                .creationTime(event.creationTime())
                .platformId(PlatformId.of(event.sourceId()))
                .taskId(toTaskId(NcctIdUtils.toNcctId(event.messageId())))
                .taskStatus(pilotTaskStatusMsg.get()
                        .getPilotTaskStatus()
                        .getValue())
                .pilotTaskStatusMsg(pilotTaskStatusMsg.get())
                .build());
    }

    public NcctPim.Command prepToSend(Task msg, PlatformId destination) {
        return prepToSend(msg, SourceId.of(destination.id(), destination.subId()));
    }

    /**
     * Prepare a message to be sent.
     *
     * @param msg Task message
     * @param destination Destination
     * @return NcctPim.SendMsg or NcctPim.UnspecifiedError if failed
     */
    public NcctPim.Command prepToSend(Task msg, SourceId destination) {
        if (msg instanceof TaskMessages.TeamTask) {
            TaskMessages.TeamTask task = (TaskMessages.TeamTask) msg;
            return buildWrapper(Daedalus.TEAM_TASK_TEAM(TeamId.of(1L)),
                    destination,
                    task.teamTaskMsg().toByteArray());
        } else if (msg instanceof TaskMessages.TeamTaskStatus) {
            TaskMessages.TeamTaskStatus task = (TaskMessages.TeamTaskStatus) msg;
            return buildWrapper(Daedalus.TEAM_TASK_STATUS_TEAM(TeamId.of(1L)),
                    destination,
                    task.teamTaskStatusMsg().toByteArray());
        } else if (msg instanceof TaskMessages.FusionTask) {
            TaskMessages.FusionTask task = (TaskMessages.FusionTask) msg;
            return buildWrapper(Daedalus.FUSION_TASK_TEAM(TeamId.of(1L)),
                    destination,
                    task.fusionTaskMsg().toByteArray());
        } else if (msg instanceof TaskMessages.FusionTaskStatus) {
            TaskMessages.FusionTaskStatus task = (TaskMessages.FusionTaskStatus) msg;
            return buildWrapper(Daedalus.FUSION_TASK_STATUS_TEAM(TeamId.of(1L)),
                    destination,
                    task.fusionTaskStatusMsg().toByteArray());
        } else if (msg instanceof TaskMessages.PilotTask) {
            TaskMessages.PilotTask task = (TaskMessages.PilotTask) msg;
            return buildWrapper(Daedalus.PILOT_TASK_TEAM(TeamId.of(1L)),
                    destination,
                    task.pilotTaskMsg().toByteArray());
        } else if (msg instanceof TaskMessages.PilotTaskCommand) {
            TaskMessages.PilotTaskCommand task = (TaskMessages.PilotTaskCommand) msg;
            return buildWrapper(Daedalus.PILOT_TASK_COMMAND_TEAM(TeamId.of(1L)),
                    destination,
                    task.pilotTaskCommandMsg().toByteArray());
        } else if (msg instanceof TaskMessages.PilotTaskStatus) {
            TaskMessages.PilotTaskStatus task = (TaskMessages.PilotTaskStatus) msg;
            return buildWrapper(Daedalus.PILOT_TASK_STATUS_TEAM(TeamId.of(1L)),
                    destination,
                    task.pilotTaskStatusMsg().toByteArray());
        } else {
            return NcctPim.UnspecifiedError.of(
                    "Attempted to prep unknown message type " + msg.getClass().toString() + " for send");
        }
    }

    private NcctPim.Command buildWrapper(MsgType msgType, SourceId destination, byte[] payload) {
        return NcctPim.SendMsg.of(
                msgType,
                destination,
                GenericPayload.builder()
                        .msgType(msgType)
                        .payload(payload)
                        .build());
    }

}
