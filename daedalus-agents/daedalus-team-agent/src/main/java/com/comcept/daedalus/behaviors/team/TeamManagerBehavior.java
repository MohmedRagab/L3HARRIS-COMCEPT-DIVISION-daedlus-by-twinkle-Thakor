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
package com.comcept.daedalus.behaviors.team;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.ReceiveBuilder;
import akka.actor.typed.javadsl.TimerScheduler;
import com.comcept.daedalus.api.DaedalusEvents;
import com.comcept.daedalus.api.DaedalusMessages;
import com.comcept.daedalus.api.msg.Daedalus;
import com.comcept.daedalus.api.team.Team;
import com.comcept.daedalus.api.team.TeamEvents;
import com.comcept.daedalus.api.team.TeamId;
import com.comcept.daedalus.api.team.TeamInternal;
import com.comcept.daedalus.api.team.TeamMessages;
import com.comcept.daedalus.eventbus.EventDepot;
import com.comcept.daedalus.msglib.ResponseType;
import com.comcept.daedalus.msglib.ResponseTypeValue;
import com.comcept.daedalus.msglib.TeamAssignmentDetails;
import com.comcept.daedalus.msglib.TeamInvitationMsg;
import com.comcept.daedalus.msglib.TeamInvitationResponseMsg;
import com.comcept.daedalus.msglib.TeamRegistrationMsg;
import com.comcept.ncct.typed.api.PlatformPosition;
import com.comcept.ncct.typed.api.common.PlatformId;
import com.comcept.ncct.typed.api.common.SecurityClassification;
import com.comcept.ncct.typed.mapper.utils.NcctIdUtils;
import com.google.common.collect.Sets;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.typesafe.config.Config;
import comcept.ncct.msglib.Uuid;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Manages team membership, formation, and leader election.
 *
 * <p>Expected Config:
 * <pre>
 *    app.behaviors.team_monitor.timers.team_formation_timeout : Integer
 *    app.behaviors.team_monitor.timers.process_candidates     : Integer
 *    app.behaviors.team_monitor.timers.send_invitations       : Integer
 *    app.behaviors.team_monitor.timers.ack_wait_timeout       : Integer
 *    app.behaviors.team_monitor.timers.platform_expiration    : Integer
 * </pre>
 *
 * @author RDanna
 */
public class TeamManagerBehavior extends AbstractBehavior<Team> {

    private static class TeamFormationTimedOut implements Team {
    }

    @Value(staticConstructor = "of")
    @Accessors(fluent = true)
    private static class PlatformTimedOut implements Team {
        @NonNull PlatformId id;
    }

    private static class CandidateGatheringTimedOut implements Team {
    }

    private static class SendInvitationTimedOut implements Team {
    }

    private static class AckWaitTimedOut implements Team {
    }

    private enum State {
        IDLE, IDLE_LEADER, IDLE_MEMBER, GATHER_CANDIDATES, INVITATION_WAIT, ACK_WAIT;

        public boolean isLeaderState() {
            return equals(IDLE_LEADER) || equals(GATHER_CANDIDATES) || equals(INVITATION_WAIT) || equals(ACK_WAIT);
        }

        public boolean isMemberState() {
            return equals(IDLE_MEMBER);
        }
    }

    private static final String CONFIG_PREFIX = "app.behaviors.team_monitor";
    private static final String TIMERS_PREFIX = "timers.";
    private static final String TEAM_FORMATION_TIMEOUT_TIMER_NAME = "team_formation_timeout";
    private static final String PROCESS_CANDIDATES_TIMER_NAME = "process_candidates";
    private static final String SEND_INVITATIONS_TIMER_NAME = "send_invitations";
    private static final String ACK_WAIT_TIMEOUT_TIMER_NAME = "ack_wait_timeout";
    private static final String PLATFORM_EXPIRATON_TIMER_PREFIX = "platform_expiration";

    private final TimerScheduler<Team> timers;

    private final ActorRef<Team> publisher;
    private final EventDepot eventDepot;

    private final SecurityClassification security;
    
    private final long teamFormationExpiration;
    private final long platformExpiration;
    private final long candidateExpiration;
    private final long sendInvitationExpiration;
    private final long ackWaitExpiration;

    private TeamId teamId = TeamId.UNKNOWN;
    private PlatformId leaderId = PlatformId.UNKNOWN;
    private PlatformId selfId = PlatformId.UNKNOWN;
    private String inviteId = "";
    private int inviteCounter = 0;
    private State currentState = State.IDLE;

    private final Map<PlatformId, Integer> priorityMap = new HashMap<>();
    private final Set<PlatformId> teamMembers = new HashSet<>();
    private final Set<PlatformId> groupMembers = new HashSet<>();
    private final Set<PlatformId> downList = new HashSet<>();
    private final Set<PlatformId> candidateList = new HashSet<>();
    private final Set<PlatformId> pendingResponses = new HashSet<>();

    /**
     * Create the behavior.
     *
     * @param publisher NCCT PIM
     * @return New behavior
     */
    public static Behavior<Team> create(ActorRef<Team> publisher, EventDepot eventDepot) {
        Behavior<Team> behavior = Behaviors.setup(context ->
                Behaviors.withTimers(timers -> new TeamManagerBehavior(context, timers, publisher, eventDepot)));

        return Behaviors.supervise(behavior).onFailure(SupervisorStrategy.restart());
    }

    private TeamManagerBehavior(
            ActorContext<Team> context,
            TimerScheduler<Team> timers,
            ActorRef<Team> publisher,
            EventDepot eventDepot) {
        super(context);

        Config config = context.getSystem().settings().config();
        
        this.security = SecurityClassification.builder()
                .capco(config.getString("security.capco"))
                .ownerTrigraph(config.getString("security.owner"))
                .build();
        
        this.timers = timers;
        this.publisher = publisher;
        this.eventDepot = eventDepot;

        Config teamManagerConfig = config.getConfig(CONFIG_PREFIX);

        this.teamFormationExpiration = teamManagerConfig.getLong(TIMERS_PREFIX + TEAM_FORMATION_TIMEOUT_TIMER_NAME);
        this.platformExpiration = teamManagerConfig.getLong(TIMERS_PREFIX + PLATFORM_EXPIRATON_TIMER_PREFIX);
        this.candidateExpiration = teamManagerConfig.getLong(TIMERS_PREFIX + PROCESS_CANDIDATES_TIMER_NAME);
        this.sendInvitationExpiration = teamManagerConfig.getLong(TIMERS_PREFIX + SEND_INVITATIONS_TIMER_NAME);
        this.ackWaitExpiration = teamManagerConfig.getLong(TIMERS_PREFIX + ACK_WAIT_TIMEOUT_TIMER_NAME);

        publisher.tell(TeamEvents.RegisterListener.builder()
                .subscriptionHandler(TeamMessages.TeamAssignment.class, context.getSelf())
                .subscriptionHandler(TeamMessages.TeamInvitation.class, context.getSelf())
                .subscriptionHandler(TeamMessages.TeamInvitationResponse.class, context.getSelf())
                .subscriptionHandler(TeamMessages.TeamRegistration.class, context.getSelf())
                .build());

        eventDepot.teamEventBus().subscribe(context.getSelf(), DaedalusEvents.PlatformIdUpdated.class);
    }

    @Override
    public Receive<Team> createReceive() {
        return becomeIdle();
    }

    private Behavior<Team> onTeamAssignment(TeamMessages.TeamAssignment msg) {

        TeamAssignmentDetails details = msg.teamAssignmentMsg().getDetails();
        if (! details.getMemberPriorityOrderList().contains(NcctIdUtils.toNcctId(msg.sourceId()))) {
            getContext().getLog().warn("Platform not included in team member list, dropping message");
            return Behaviors.same();
        }

        startTeamFormationTimeout();
        final Set<PlatformId> prevTeamMembers = new HashSet<>(teamMembers);

        List<PlatformId> priorityList = details.getMemberPriorityOrderList().stream()
                .map(NcctIdUtils::toPlatformId)
                .collect(Collectors.toList());

        priorityMap.clear();
        for (int i = 0; i < priorityList.size(); i++) {
            priorityMap.put(priorityList.get(i), i);
        }

        teamMembers.clear();
        teamMembers.addAll(priorityList);

        eventDepot.teamEventBus().publish(TeamEvents.TeamMembershipChanged.of(teamMembers));

        if (teamMembers.size() == 1) {
            getContext().getLog().info("Team has only one member - jumping to registration");
            groupMembers.clear();
            timers.cancel(TEAM_FORMATION_TIMEOUT_TIMER_NAME);
            sendTeamRegistration();
            return becomeLeader();
        }

        if (teamId.equals(TeamId.UNKNOWN)) {
            getContext().getLog().warn("No prior team assignment");
            updateTeamId(msg.teamId());
            return becomeLeader();
        }

        if (! teamId.equals(msg.teamId())) {
            getContext().getLog().info("Assigned to new team");
            updateTeamId(msg.teamId());
            startTeamFormationTimeout();
            return recover();
        }

        if (teamMembers.equals(prevTeamMembers)) {
            getContext().getLog().info("Team membership unchanged");
            return Behaviors.same();
        }

        if (currentState.isLeaderState()) {
            getContext().getLog().info("Leader with team membership changed");
            Set<PlatformId> lostMembers = new HashSet<>(prevTeamMembers);
            lostMembers.removeAll(teamMembers);

            for (PlatformId member: lostMembers) {
                candidateList.remove(member);
            }

            return Behaviors.same();
        }

        if (!teamMembers.contains(leaderId)) {
            getContext().getLog().info("Lost leader");
            startTeamFormationTimeout();
            return recover();
        }

        return Behaviors.same();
    }

    private Behavior<Team> onTeamInvitation(TeamMessages.TeamInvitation msg) {

        switch (currentState) {
            case IDLE:
                getContext().getLog().warn("Received team invitation in IDLE state - ignoring");
                return Behaviors.same();
            case ACK_WAIT:
                getContext().getLog().debug("Waiting on responses to our own invite - declining");
                sendTeamInvitationResponse(msg, ResponseType.RT_DECLINE);
                return Behaviors.same();
            case IDLE_MEMBER:
                getContext().getLog().info("Accepting invite from leader");
                leaderId = msg.leaderId();
                sendTeamInvitationResponse(msg, ResponseType.RT_ACCEPT);
                return Behaviors.same();
            case IDLE_LEADER:
            case INVITATION_WAIT:
            case GATHER_CANDIDATES:
                getContext().getLog().info("Accepting invite to new group and forwarding to members");
                leaderId = msg.leaderId();
                forwardTeamInvitation(msg);
                sendTeamInvitationResponse(msg, ResponseType.RT_ACCEPT);
                return becomeMember();
            default:
                getContext().getLog().info("Declining invite");
                sendTeamInvitationResponse(msg, ResponseType.RT_DECLINE);
                return Behaviors.same();
        }
    }

    private Behavior<Team> onTeamInvitationResponse(TeamMessages.TeamInvitationResponse msg) {

        if (! currentState.equals(State.ACK_WAIT)) {
            getContext().getLog().warn("Received team invitation response while not in ACK_WAIT state - ignoring");
            return Behaviors.same();
        }

        PlatformId source = PlatformId.of(msg.sourceId().id(), msg.sourceId().subId());

        pendingResponses.remove(source);

        TeamInvitationResponseMsg responseMsg = msg.teamInvitationResponseMsg();

        switch (responseMsg.getResponse().getValue()) {
            case RT_ACCEPT:
                String responseGroupId = responseMsg.getInviteId().getHexString().getValue();
                if (! responseMsg.hasInviteId() || ! responseGroupId.equals(inviteId)) {
                    getContext().getLog().info("Received invitation response from a different group - ignoring");
                    return Behaviors.same();
                }

                groupMembers.add(source);
                startPlatformExpirationTimer(source);

                Set<PlatformId> combinedMembers = getKnownPlatforms();
                if (combinedMembers.containsAll(teamMembers)) {
                    getContext().getLog().info("All members accepted or known to be down - sending registration");
                    timers.cancel(TEAM_FORMATION_TIMEOUT_TIMER_NAME);
                    sendTeamRegistration();
                }

                break;
            case RT_DECLINE:
                getContext().getLog().info("Received an invitation decline");
                break;
            default:
                getContext().getLog().error("Unknown invitation response type");
                break;
        }

        if (pendingResponses.isEmpty()) {
            getContext().getLog().info("All members accounted for");
            timers.cancel(ACK_WAIT_TIMEOUT_TIMER_NAME);
            currentState = State.IDLE_LEADER;
        }

        return Behaviors.same();
    }

    private Behavior<Team> onPlatformPosition(DaedalusMessages.TeamPlatformPosition msg) {

        PlatformPosition platformPosition = msg.platformPosition();
        if (! teamMembers.contains(platformPosition.platformId())) {
            getContext().getLog().info("Received unexpected platform position - ignoring");
            return Behaviors.same();
        }

        if (currentState.isMemberState()) {
            if (platformPosition.platformId().equals(leaderId)) {
                getContext().getLog().info("Received platform position from leader - resetting timer");
                startPlatformExpirationTimer(platformPosition.platformId());
            } else {
                getContext().getLog().info("Received platform position from member - ignoring");
            }

            return Behaviors.same();
        } else if (currentState.isLeaderState() && groupMembers.contains(platformPosition.platformId())) {
            getContext().getLog().info("Received platform position from member - resetting timer");
            startPlatformExpirationTimer(platformPosition.platformId());
            return Behaviors.same();
        }

        candidateList.add(platformPosition.platformId());
        downList.remove(platformPosition.platformId());
        if (currentState.equals(State.GATHER_CANDIDATES)) {
            getContext().getLog().debug("Already gathering candidates - exiting");
            return Behaviors.same();
        }

        currentState = State.GATHER_CANDIDATES;
        startCandidateGatheringTimer();
        return Behaviors.same();
    }

    private Behavior<Team> onTeamRegistration(TeamMessages.TeamRegistration msg) {

        return Behaviors.same();
    }

    private Behavior<Team> onPlatformIdUpdated(DaedalusEvents.PlatformIdUpdated msg) {
        if (msg.platformId().equals(selfId)) {
            return Behaviors.same();
        }
        this.selfId = msg.platformId();
        inviteCounter++;
        inviteId = selfId.id() + "-" + inviteCounter;
        return Behaviors.same();
    }

    private Behavior<Team> onTeamFormationTimedOut(TeamFormationTimedOut a) {

        if (! currentState.isLeaderState()) {
            getContext().getLog().info("Got team formation timeout while not leader - ignoring");
            return Behaviors.same();
        }

        getContext().getLog().info("Team formation timed out");

        timers.cancelAll();
        pendingResponses.clear();

        sendTeamRegistration();

        return becomeLeader();
    }

    private Behavior<Team> onPlatformTimedOut(PlatformTimedOut msg) {

        if (currentState.equals(State.IDLE)) {
            getContext().getLog().info("Ignoring platform time out in IDLE state");
            return Behaviors.same();
        }

        if (currentState.isMemberState()) {
            if (msg.id().equals(leaderId)) {
                getContext().getLog().info("Leader timed out - recovering");
                return recover();
            } else {
                getContext().getLog().info("Team member timed out - ignoring");
            }

            return Behaviors.same();
        }

        getContext().getLog().info("Platform {} timed out", msg.id());

        groupMembers.remove(msg.id());
        downList.add(msg.id());

        Set<PlatformId> knownPlatforms = getKnownPlatforms();
        if (knownPlatforms.containsAll(teamMembers)) {
            getContext().getLog().debug("All members accepted or known to be down - sending registration message");
            timers.cancel(TEAM_FORMATION_TIMEOUT_TIMER_NAME);
            timers.cancel(ACK_WAIT_TIMEOUT_TIMER_NAME);
            sendTeamRegistration();
            return becomeLeader();
        }

        return Behaviors.same();
    }

    private Behavior<Team> onCandidateGatheringTimedOut(CandidateGatheringTimedOut msg) {

        if (! currentState.equals(State.GATHER_CANDIDATES)) {
            getContext().getLog().info("Skipping candidate list processing while not in GATHER_CANDIDATES state");
            return Behaviors.same();
        }

        getContext().getLog().info("Processing candidate list");

        List<PlatformId> orderedCandidates = new ArrayList<>(candidateList);
        orderedCandidates.add(selfId);
        orderedCandidates.sort((lhs, rhs) -> priorityMap.get(lhs) - priorityMap.get(rhs));

        int index = orderedCandidates.indexOf(selfId);

        startSendInvitationTimer(index);

        currentState = State.INVITATION_WAIT;
        return Behaviors.same();
    }

    private Behavior<Team> onSendInvitationsTimedOut(SendInvitationTimedOut msg) {

        if (currentState != State.INVITATION_WAIT) {
            getContext().getLog().info("Skipping invitation send while not in INVITATION_WAIT state");
            return Behaviors.same();
        }

        getContext().getLog().info("Sending team invitations");

        for (PlatformId candidate : candidateList) {
            sendTeamInvitation(candidate, inviteId);
            pendingResponses.add(candidate);
        }

        currentState = State.ACK_WAIT;

        startAckWaitTimer();

        return Behaviors.same();
    }

    private Behavior<Team> onAckWaitTimedOut(AckWaitTimedOut msg) {

        if (! currentState.equals(State.ACK_WAIT)) {
            getContext().getLog().info("Ignoring ACK_WAIT timeout while not in ACK_WAIT state");
            return Behaviors.same();
        }

        getContext().getLog().info("ACK_WAIT timeout occurred");

        pendingResponses.clear();
        return becomeLeader();
    }

    private Receive<Team> becomeIdle() {
        getContext().getLog().info("Becoming IDLE");
        currentState = State.IDLE;
        return baseReceiveHandler()
                .onMessage(TeamMessages.TeamAssignment.class, this::onTeamAssignment)
                .onMessage(TeamMessages.TeamInvitation.class, this::onTeamInvitation)
                .build();
    }

    private Receive<Team> becomeLeader() {
        getContext().getLog().info("Becoming LEADER");
        currentState = State.IDLE_LEADER;
        leaderId = selfId;
        return baseReceiveHandler()
                .onMessage(TeamMessages.TeamInvitation.class, this::onTeamInvitation)
                .build();
    }

    private Receive<Team> recover() {
        getContext().getLog().info("Recovering");
        currentState = State.IDLE_LEADER;
        leaderId = selfId;
        inviteCounter++;
        inviteId = selfId.id() + "-" + inviteCounter;
        groupMembers.clear();
        candidateList.clear();
        downList.clear();
        downList.addAll(teamMembers);
        pendingResponses.clear();
        return baseReceiveHandler()
                .onMessage(TeamMessages.TeamInvitation.class, this::onTeamInvitation)
                .build();
    }

    private Receive<Team> becomeMember() {
        getContext().getLog().info("Becoming MEMBER");
        currentState = State.IDLE_MEMBER;
        return baseReceiveHandler()
                .onMessage(TeamMessages.TeamInvitationResponse.class,
                        this::onTeamInvitationResponse)
                .onMessage(TeamMessages.TeamRegistration.class,
                        this::onTeamRegistration)
                .build();
    }

    private ReceiveBuilder<Team> baseReceiveHandler() {
        return newReceiveBuilder()
                .onMessage(DaedalusEvents.PlatformIdUpdated.class, this::onPlatformIdUpdated)
                .onMessage(TeamFormationTimedOut.class, this::onTeamFormationTimedOut)
                .onMessage(PlatformTimedOut.class, this::onPlatformTimedOut)
                .onMessage(CandidateGatheringTimedOut.class, this::onCandidateGatheringTimedOut)
                .onMessage(SendInvitationTimedOut.class, this::onSendInvitationsTimedOut)
                .onMessage(AckWaitTimedOut.class, this::onAckWaitTimedOut)
                .onMessage(DaedalusMessages.TeamPlatformPosition.class, this::onPlatformPosition);
    }

    private void startTeamFormationTimeout() {
        timers.startSingleTimer(
                TEAM_FORMATION_TIMEOUT_TIMER_NAME,
                new TeamFormationTimedOut(),
                Duration.ofSeconds(teamFormationExpiration));
    }

    private void startPlatformExpirationTimer(PlatformId id) {
        timers.startSingleTimer(
                PLATFORM_EXPIRATON_TIMER_PREFIX + id.toString(),
                PlatformTimedOut.of(id),
                Duration.ofSeconds(platformExpiration));
    }

    private void startCandidateGatheringTimer() {
        timers.startSingleTimer(PROCESS_CANDIDATES_TIMER_NAME,
                new CandidateGatheringTimedOut(),
                Duration.ofMillis(candidateExpiration));
    }

    private void startSendInvitationTimer(int index) {
        timers.startSingleTimer(SEND_INVITATIONS_TIMER_NAME,
                new SendInvitationTimedOut(),
                Duration.ofMillis(sendInvitationExpiration * index));
    }

    private void startAckWaitTimer() {
        timers.startSingleTimer(ACK_WAIT_TIMEOUT_TIMER_NAME,
                new AckWaitTimedOut(),
                Duration.ofMinutes(ackWaitExpiration));
    }

    private void sendTeamInvitation(PlatformId platform, String groupId) {
        TeamInvitationMsg teamInvitationMsg = TeamInvitationMsg.newBuilder()
                .setLeaderId(NcctIdUtils.toNcctId(leaderId))
                .setInviteId(Uuid.newBuilder().setHexString(StringValue.of(groupId)))
                .build();

        TeamMessages.TeamInvitation teamInvitation = TeamMessages.TeamInvitation.builder()
                .classification(security)
                .sourceId(selfId.toSourceId())
                .messageId(NcctIdUtils.newMessageId(selfId.toSourceId()))
                .creationTime(Instant.now())
                .leaderId(leaderId)
                .teamInvitationMsg(teamInvitationMsg)
                .build();

        getContext().getLog().debug("Sending TeamInvitation");
        publisher.tell(TeamInternal.SendMsg.of(
                Daedalus.TEAM_INVITATION_TEAM(TeamId.of(1L)),
                platform.toSourceId(),
                teamInvitation));
    }

    private void sendTeamInvitationResponse(TeamMessages.TeamInvitation msg, ResponseType response) {
        TeamInvitationResponseMsg teamInvitationResponseMsg = TeamInvitationResponseMsg.newBuilder()
                .setInviteId(msg.teamInvitationMsg().getInviteId())
                .setResponse(ResponseTypeValue.newBuilder().setValue(response))
                .build();

        TeamMessages.TeamInvitationResponse teamInvitationResponse = TeamMessages.TeamInvitationResponse.builder()
                .classification(security)
                .sourceId(selfId.toSourceId())
                .messageId(NcctIdUtils.newMessageId(selfId.toSourceId()))
                .creationTime(Instant.now())
                .teamInvitationResponseMsg(teamInvitationResponseMsg)
                .build();

        getContext().getLog().debug("Sending TeamInvitationResponse");
        publisher.tell(TeamInternal.SendMsg.of(
                Daedalus.TEAM_INVITATION_RESPONSE_TEAM(TeamId.of(1L)),
                msg.sourceId(),
                teamInvitationResponse));
    }

    private void sendTeamRegistration() {
        Set<PlatformId> fullGroupList = new HashSet<>(groupMembers);
        fullGroupList.add(selfId);

        TeamRegistrationMsg teamRegistrationMsg = TeamRegistrationMsg.newBuilder()
                .setTeamId(Int64Value.of(teamId.id()))
                .setLeaderId(NcctIdUtils.toNcctId(leaderId))
                .addAllMemberIds(fullGroupList.stream()
                        .map(NcctIdUtils::toNcctId)
                        .collect(Collectors.toList()))
                .build();

        TeamMessages.TeamRegistration teamRegistration = TeamMessages.TeamRegistration.builder()
                .classification(security)
                .sourceId(selfId.toSourceId())
                .messageId(NcctIdUtils.newMessageId(selfId.toSourceId()))
                .creationTime(Instant.now())
                .leaderId(leaderId)
                .memberIds(fullGroupList)
                .teamRegistrationMsg(teamRegistrationMsg)
                .build();

        getContext().getLog().debug("Sending TeamRegistration");
        for (PlatformId p : groupMembers) {
            publisher.tell(TeamInternal.SendMsg.of(
                    Daedalus.TEAM_REGISTRATION_TEAM(TeamId.of(1L)),
                    p.toSourceId(),
                    teamRegistration));
        }

    }

    private void forwardTeamInvitation(TeamMessages.TeamInvitation msg) {

        for (PlatformId platform : groupMembers) {
            sendTeamInvitation(platform, msg.teamInvitationMsg().getInviteId().getHexString().getValue());
        }
    }

    private void updateTeamId(TeamId teamId) {
        this.teamId = teamId;
        eventDepot.teamEventBus().publish(DaedalusEvents.TeamIdUpdated.of(teamId));
    }

    private Set<PlatformId> getKnownPlatforms() {
        return Sets.union(Sets.union(groupMembers, downList), Collections.singleton(selfId));
    }

}
