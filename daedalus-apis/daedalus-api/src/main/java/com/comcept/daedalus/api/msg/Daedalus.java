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
package com.comcept.daedalus.api.msg;

import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.msglib.DaedalusMessageType;
import com.comcept.ncct.typed.api.common.MsgType;
import lombok.experimental.UtilityClass;

// CHECKSTYLE:OFF
@UtilityClass
public class Daedalus {

    public static final MsgType PILOT_TASK_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK, SubTypes.LOCAL);
    public static final MsgType PILOT_TASK_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK, SubTypes.GLOBAL);

    public static MsgType PILOT_TASK_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK, teamId.id());
    }

    public static final MsgType ACTIVATE_MISSION_PLAN_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.ACTIVATE_MISSION_PLAN, SubTypes.LOCAL);
    public static final MsgType ACTIVATE_MISSION_PLAN_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.ACTIVATE_MISSION_PLAN, SubTypes.GLOBAL);

    public static MsgType ACTIVATE_MISSION_PLAN_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.ACTIVATE_MISSION_PLAN, teamId.id());
    }

    public static final MsgType ACTIVATE_MISSION_PLAN_STATUS_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.ACTIVATE_MISSION_PLAN_STATUS, SubTypes.LOCAL);
    public static final MsgType ACTIVATE_MISSION_PLAN_STATUS_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.ACTIVATE_MISSION_PLAN_STATUS, SubTypes.GLOBAL);

    public static MsgType ACTIVATE_MISSION_PLAN_STATUS_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.ACTIVATE_MISSION_PLAN_STATUS, teamId.id());
    }

    public static final MsgType MISSION_PLAN_STATUS_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.MISSION_PLAN_STATUS, SubTypes.LOCAL);
    public static final MsgType MISSION_PLAN_STATUS_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.MISSION_PLAN_STATUS, SubTypes.GLOBAL);

    public static MsgType MISSION_PLAN_STATUS_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.MISSION_PLAN_STATUS, teamId.id());
    }

    public static final MsgType TEAM_ASSIGNMENT_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_ASSIGNMENT, SubTypes.LOCAL);
    public static final MsgType TEAM_ASSIGNMENT_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_ASSIGNMENT, SubTypes.GLOBAL);

    public static MsgType TEAM_ASSIGNMENT_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.TEAM_ASSIGNMENT, teamId.id());
    }

    public static final MsgType TEAM_INVITATION_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_INVITATION, SubTypes.LOCAL);
    public static final MsgType TEAM_INVITATION_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_INVITATION, SubTypes.GLOBAL);

    public static MsgType TEAM_INVITATION_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.TEAM_INVITATION, teamId.id());
    }

    public static final MsgType TEAM_INVITATION_RESPONSE_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_INVITATION_RESPONSE, SubTypes.LOCAL);
    public static final MsgType TEAM_INVITATION_RESPONSE_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_INVITATION_RESPONSE, SubTypes.GLOBAL);

    public static MsgType TEAM_INVITATION_RESPONSE_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.TEAM_INVITATION_RESPONSE, teamId.id());
    }

    public static final MsgType TEAM_REGISTRATION_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_REGISTRATION, SubTypes.LOCAL);
    public static final MsgType TEAM_REGISTRATION_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_REGISTRATION, SubTypes.GLOBAL);

    public static MsgType TEAM_REGISTRATION_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.TEAM_REGISTRATION, teamId.id());
    }

    public static final MsgType TEAM_TASK_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK, SubTypes.LOCAL);
    public static final MsgType TEAM_TASK_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK, SubTypes.GLOBAL);

    public static MsgType TEAM_TASK_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK, teamId.id());
    }

    public static final MsgType TEAM_TASK_STATUS_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_STATUS, SubTypes.LOCAL);
    public static final MsgType TEAM_TASK_STATUS_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_STATUS, SubTypes.GLOBAL);

    public static MsgType TEAM_TASK_STATUS_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_STATUS, teamId.id());
    }

    public static final MsgType TEAM_TASK_COMMAND_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_COMMAND, SubTypes.LOCAL);
    public static final MsgType TEAM_TASK_COMMAND_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_COMMAND, SubTypes.GLOBAL);

    public static MsgType TEAM_TASK_COMMAND_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_COMMAND, teamId.id());
    }

    public static final MsgType TEAM_TASK_COMMAND_STATUS_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_COMMAND_STATUS, SubTypes.LOCAL);
    public static final MsgType TEAM_TASK_COMMAND_STATUS_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_COMMAND_STATUS, SubTypes.GLOBAL);

    public static MsgType TEAM_TASK_COMMAND_STATUS_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_COMMAND_STATUS, teamId.id());
    }

    public static final MsgType TEAM_TASK_NOTIFICATION_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_NOTIFICATION, SubTypes.LOCAL);
    public static final MsgType TEAM_TASK_NOTIFICATION_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_NOTIFICATION, SubTypes.GLOBAL);

    public static MsgType TEAM_TASK_NOTIFICATION_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_NOTIFICATION, teamId.id());
    }

    public static final MsgType TEAM_TASK_QUEUE_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_QUEUE, SubTypes.LOCAL);
    public static final MsgType TEAM_TASK_QUEUE_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_QUEUE, SubTypes.GLOBAL);

    public static MsgType TEAM_TASK_QUEUE_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.TEAM_TASK_QUEUE, teamId.id());
    }

    public static final MsgType FUSION_TASK_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.FUSION_TASK, SubTypes.LOCAL);
    public static final MsgType FUSION_TASK_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.FUSION_TASK, SubTypes.GLOBAL);

    public static MsgType FUSION_TASK_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.FUSION_TASK, teamId.id());
    }

    public static final MsgType FUSION_TASK_STATUS_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.FUSION_TASK_STATUS, SubTypes.LOCAL);
    public static final MsgType FUSION_TASK_STATUS_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.FUSION_TASK_STATUS, SubTypes.GLOBAL);

    public static MsgType FUSION_TASK_STATUS_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.FUSION_TASK_STATUS, teamId.id());
    }

    public static final MsgType PILOT_TASK_STATUS_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK_STATUS, SubTypes.LOCAL);
    public static final MsgType PILOT_TASK_STATUS_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK_STATUS, SubTypes.GLOBAL);

    public static MsgType PILOT_TASK_STATUS_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK_STATUS, teamId.id());
    }

    public static final MsgType PILOT_TASK_COMMAND_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK_COMMAND, SubTypes.LOCAL);
    public static final MsgType PILOT_TASK_COMMAND_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK_COMMAND, SubTypes.GLOBAL);

    public static MsgType PILOT_TASK_COMMAND_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK_COMMAND, teamId.id());
    }

    public static final MsgType PILOT_TASK_COMMAND_STATUS_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK_COMMAND_STATUS, SubTypes.LOCAL);
    public static final MsgType PILOT_TASK_COMMAND_STATUS_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK_COMMAND_STATUS, SubTypes.GLOBAL);

    public static MsgType PILOT_TASK_COMMAND_STATUS_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.PILOT_TASK_COMMAND_STATUS, teamId.id());
    }

    public static final MsgType VEHICLE_TASK_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.VEHICLE_TASK, SubTypes.LOCAL);
    public static final MsgType VEHICLE_TASK_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.VEHICLE_TASK, SubTypes.GLOBAL);

    public static MsgType VEHICLE_TASK_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.VEHICLE_TASK, teamId.id());
    }

    public static final MsgType VEHICLE_TASK_STATUS_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.VEHICLE_TASK_STATUS, SubTypes.LOCAL);
    public static final MsgType VEHICLE_TASK_STATUS_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.VEHICLE_TASK_STATUS, SubTypes.GLOBAL);

    public static MsgType VEHICLE_TASK_STATUS_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.VEHICLE_TASK_STATUS, teamId.id());
    }

    public static final MsgType SENSOR_TASK_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.SENSOR_TASK, SubTypes.LOCAL);
    public static final MsgType SENSOR_TASK_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.SENSOR_TASK, SubTypes.GLOBAL);

    public static MsgType SENSOR_TASK_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.SENSOR_TASK, teamId.id());
    }

    public static final MsgType SENSOR_TASK_STATUS_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.SENSOR_TASK_STATUS, SubTypes.LOCAL);
    public static final MsgType SENSOR_TASK_STATUS_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.SENSOR_TASK_STATUS, SubTypes.GLOBAL);

    public static MsgType SENSOR_TASK_STATUS_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.SENSOR_TASK_STATUS, teamId.id());
    }

    public static final MsgType TRACKER_COMMAND_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TRACKER_COMMAND, SubTypes.LOCAL);
    public static final MsgType TRACKER_COMMAND_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.TRACKER_COMMAND, SubTypes.GLOBAL);

    public static MsgType TRACKER_COMMAND_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.TRACKER_COMMAND, teamId.id());
    }

    public static final MsgType HEALTH_AND_STATUS_LOCAL =
            MsgType.of(Catalogs.DAEDALUS, Types.HEALTH_AND_STATUS, SubTypes.LOCAL);
    public static final MsgType HEALTH_AND_STATUS_GLOBAL =
            MsgType.of(Catalogs.DAEDALUS, Types.HEALTH_AND_STATUS, SubTypes.GLOBAL);

    public static MsgType HEALTH_AND_STATUS_TEAM(TeamId teamId) {
        return MsgType.of(Catalogs.DAEDALUS, Types.HEALTH_AND_STATUS, teamId.id());
    }


    @UtilityClass
    public static class Catalogs {

        public static final int DAEDALUS = 100;

    }

    @UtilityClass
    public static class Types {

        public static final int UNKNOWN = DaedalusMessageType.Unknown_VALUE;
        public static final int ACTIVATE_MISSION_PLAN = DaedalusMessageType.ActivateMissionPlan_VALUE;
        public static final int ACTIVATE_MISSION_PLAN_STATUS = DaedalusMessageType.ActivateMissionPlanStatus_VALUE;
        public static final int MISSION_PLAN_STATUS = DaedalusMessageType.MissionPlanStatus_VALUE;
        public static final int TEAM_ASSIGNMENT = DaedalusMessageType.TeamAssignment_VALUE;
        public static final int TEAM_INVITATION = DaedalusMessageType.TeamInvitation_VALUE;
        public static final int TEAM_INVITATION_RESPONSE = DaedalusMessageType.TeamInvitationResponse_VALUE;
        public static final int TEAM_REGISTRATION = DaedalusMessageType.TeamRegistration_VALUE;
        public static final int TEAM_TASK = DaedalusMessageType.TeamTask_VALUE;
        public static final int TEAM_TASK_STATUS = DaedalusMessageType.TeamTaskStatus_VALUE;
        public static final int TEAM_TASK_COMMAND = DaedalusMessageType.TeamTaskCommand_VALUE;
        public static final int TEAM_TASK_COMMAND_STATUS = DaedalusMessageType.TeamTaskCommandStatus_VALUE;
        public static final int TEAM_TASK_NOTIFICATION = DaedalusMessageType.TeamTaskNotification_VALUE;
        public static final int TEAM_TASK_QUEUE = DaedalusMessageType.TeamTaskQueue_VALUE;
        public static final int FUSION_TASK = DaedalusMessageType.FusionTask_VALUE;
        public static final int FUSION_TASK_STATUS = DaedalusMessageType.FusionTaskStatus_VALUE;
        public static final int PILOT_TASK = DaedalusMessageType.PilotTask_VALUE;
        public static final int PILOT_TASK_STATUS = DaedalusMessageType.PilotTaskStatus_VALUE;
        public static final int PILOT_TASK_COMMAND = DaedalusMessageType.PilotTaskCommand_VALUE;
        public static final int PILOT_TASK_COMMAND_STATUS = DaedalusMessageType.PilotTaskCommandStatus_VALUE;
        public static final int VEHICLE_TASK = DaedalusMessageType.VehicleTask_VALUE;
        public static final int VEHICLE_TASK_STATUS = DaedalusMessageType.VehicleTaskStatus_VALUE;
        public static final int SENSOR_TASK = DaedalusMessageType.SensorTask_VALUE;
        public static final int SENSOR_TASK_STATUS = DaedalusMessageType.SensorTaskStatus_VALUE;
        public static final int TRACKER_COMMAND = DaedalusMessageType.TrackerCommand_VALUE;
        public static final int HEALTH_AND_STATUS = DaedalusMessageType.HealthAndStatus_VALUE;

    }

    @UtilityClass
    public static class SubTypes {

        public static final Long GLOBAL = 0L;
        public static final Long LOCAL = Long.MAX_VALUE;

    }

}
