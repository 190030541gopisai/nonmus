package com.nonmus.nonmus.modules.channel.service;

import com.nonmus.nonmus.modules.channel.dto.internal.CreateChannelInviteResult;
import com.nonmus.nonmus.modules.channel.dto.request.CreateChannelInviteRequest;
import com.nonmus.nonmus.modules.channel.dto.request.JoinChannelRequest;
import com.nonmus.nonmus.modules.channel.dto.request.UpdateChannelInviteRequest;
import com.nonmus.nonmus.modules.channel.dto.response.CreateChannelInviteResponse;
import com.nonmus.nonmus.modules.channel.dto.response.InviteResponse;
import com.nonmus.nonmus.modules.channel.dto.response.JoinChannelResponse;
import com.nonmus.nonmus.modules.channel.entity.Channel;
import com.nonmus.nonmus.modules.channel.entity.ChannelMember;
import com.nonmus.nonmus.modules.channel.entity.ChannelStatistics;
import com.nonmus.nonmus.modules.channel.entity.Invite;
import com.nonmus.nonmus.modules.channel.entity.InviteJoinRule;
import com.nonmus.nonmus.modules.channel.enums.ChannelRole;
import com.nonmus.nonmus.modules.channel.enums.JoinType;
import com.nonmus.nonmus.modules.channel.repository.ChannelMemberRepository;
import com.nonmus.nonmus.modules.channel.repository.ChannelRepository;
import com.nonmus.nonmus.modules.channel.repository.InviteRepository;
import com.nonmus.nonmus.modules.common.exception.*;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelInviteService {
    private static final String TOKEN_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 10;
    private static final int MAX_TOKEN_ATTEMPTS = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final InviteRepository inviteRepository;
    private final UsersService usersService;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CreateChannelInviteResult createChannelInvite(CreateChannelInviteRequest request, String email) {
        Users user = usersService.getUsersByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        Channel channel = channelRepository.findById(request.getChannelId()).orElseThrow(
                () -> new ChannelNotFoundException("Channel not found")
        );

        ChannelMember channelMember = channelMemberRepository
                .findByChannelIdAndUserIdAndLeftAtIsNull(channel.getId(), user.getId())
                .orElseThrow(() -> new ChannelAccessDeniedException("You are not a member of this channel"));

        if (channelMember.getRole() != ChannelRole.OWNER) {
            throw new ChannelAccessDeniedException("Only channel owners can create invites");
        }

        JoinType joinType = parseJoinType(request.getJoinType());

        Invite invite = new Invite();
        invite.setChannel(channel);
        invite.setUser(user);
        invite.setToken(generateUniqueToken());
        invite.setExpiry(request.getExpiry());
        invite.setMaxUses(request.getMaxUses());
        invite.setCurrentUses(0L);

        InviteJoinRule joinRule = new InviteJoinRule();
        joinRule.setType(joinType);
        joinRule.setInvite(invite);

        if (joinType == JoinType.PASSWORD) {
            if (!StringUtils.hasText(request.getPassword())) {
                throw new InvalidInviteException("Password is required for PASSWORD join type");
            }
            joinRule.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        invite.setInviteJoinRule(joinRule);

        inviteRepository.save(invite);

        return CreateChannelInviteResult.builder()
                .invite(invite)
                .build();
    }

    @Transactional(readOnly = true)
    public List<CreateChannelInviteResponse> listChannelInvites(UUID channelId, String email) {
        Users user = usersService.getUsersByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        Channel channel = channelRepository.findById(channelId).orElseThrow(
                () -> new ChannelNotFoundException("Channel not found")
        );

        ChannelMember channelMember = channelMemberRepository
                .findByChannelIdAndUserIdAndLeftAtIsNull(channel.getId(), user.getId())
                .orElseThrow(() -> new ChannelAccessDeniedException("You are not a member of this channel"));

        if (channelMember.getRole() != ChannelRole.OWNER) {
            throw new ChannelAccessDeniedException("Only channel owners can view invites");
        }

        return inviteRepository.findByChannelId(channel.getId()).stream()
                .map(invite -> CreateChannelInviteResponse.builder()
                        .inviteId(invite.getId())
                        .channelId(channel.getId())
                        .token(invite.getToken())
                        .expiry(invite.getExpiry())
                        .maxUses(invite.getMaxUses())
                        .currentUses(invite.getCurrentUses())
                        .joinType(invite.getInviteJoinRule().getType().name())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public InviteResponse getInviteInfo(String token) {
        Invite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new InvalidInviteException("Invite not found"));

        if (invite.getExpiry() != null && invite.getExpiry().isBefore(java.time.Instant.now())) {
            throw new InvalidInviteException("Invite has expired");
        }

        if (invite.getMaxUses() != null && invite.getCurrentUses() >= invite.getMaxUses()) {
            throw new InvalidInviteException("Invite has reached its maximum number of uses");
        }

        Channel channel = invite.getChannel();
        return InviteResponse.builder()
                .inviteId(invite.getId())
                .channelId(channel.getId())
                .channelName(channel.getName())
                .channelLogo(channel.getLogo())
                .joinType(invite.getInviteJoinRule().getType().name())
                .expiry(invite.getExpiry())
                .currentUses(invite.getCurrentUses())
                .maxUses(invite.getMaxUses())
                .build();
    }

    @Transactional
    public JoinChannelResponse joinChannel(String token, JoinChannelRequest request, String email) {
        Users user = usersService.getUsersByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        Invite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new InvalidInviteException("Invite not found"));

        if (invite.getExpiry() != null && invite.getExpiry().isBefore(java.time.Instant.now())) {
            throw new InvalidInviteException("Invite has expired");
        }

        if (invite.getMaxUses() != null && invite.getCurrentUses() >= invite.getMaxUses()) {
            throw new InvalidInviteException("Invite has reached its maximum number of uses");
        }

        Channel channel = invite.getChannel();

        if (channelMemberRepository.existsByChannelIdAndUserIdAndLeftAtIsNull(channel.getId(), user.getId())) {
            throw new UserAlreadyExistsInChannelException("You are already a member of this channel");
        }

        InviteJoinRule joinRule = invite.getInviteJoinRule();
        if (joinRule.getType() == JoinType.PASSWORD) {
            if (request == null || !StringUtils.hasText(request.getPassword())
                    || !passwordEncoder.matches(request.getPassword(), joinRule.getPasswordHash())) {
                throw new InvalidInviteException("Invalid invite password");
            }
        }

        ChannelMember member = new ChannelMember();
        member.setUser(user);
        member.setChannel(channel);
        member.setRole(ChannelRole.MEMBER);

        channel.addMember(member);
        channelMemberRepository.save(member);

        invite.setCurrentUses(invite.getCurrentUses() + 1);
        inviteRepository.save(invite);

        ChannelStatistics statistics = channel.getChannelStatistics();
        if (statistics != null) {
            statistics.setSubscribersCount(statistics.getSubscribersCount() + 1);
        }

        return JoinChannelResponse.builder()
                .channelId(channel.getId())
                .channelName(channel.getName())
                .channelLogo(channel.getLogo())
                .role(ChannelRole.MEMBER)
                .joinedAt(member.getJoinedAt())
                .build();
    }

    @Transactional
    public CreateChannelInviteResponse updateChannelInvite(UUID inviteId, UpdateChannelInviteRequest request, String email) {
        Users user = usersService.getUsersByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        Invite invite = inviteRepository.findById(inviteId).orElseThrow(
                () -> new InvalidInviteException("Invite not found")
        );

        Channel channel = invite.getChannel();

        ChannelMember channelMember = channelMemberRepository
                .findByChannelIdAndUserIdAndLeftAtIsNull(channel.getId(), user.getId())
                .orElseThrow(() -> new ChannelAccessDeniedException("You are not a member of this channel"));

        if (channelMember.getRole() != ChannelRole.OWNER) {
            throw new ChannelAccessDeniedException("Only channel owners can update invites");
        }

        if (request.getExpiry() != null) {
            invite.setExpiry(request.getExpiry());
        }

        if (request.getMaxUses() != null) {
            invite.setMaxUses(request.getMaxUses());
        }

        InviteJoinRule joinRule = invite.getInviteJoinRule();

        if (StringUtils.hasText(request.getJoinType())) {
            JoinType joinType = parseJoinType(request.getJoinType());
            joinRule.setType(joinType);

            if (joinType == JoinType.PASSWORD) {
                if (!StringUtils.hasText(request.getPassword())) {
                    throw new InvalidInviteException("Password is required for PASSWORD join type");
                }
                joinRule.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            } else {
                joinRule.setPasswordHash(null);
            }
        } else if (StringUtils.hasText(request.getPassword())
                && joinRule.getType() == JoinType.PASSWORD) {
            joinRule.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        inviteRepository.save(invite);

        return CreateChannelInviteResponse.builder()
                .inviteId(invite.getId())
                .channelId(channel.getId())
                .token(invite.getToken())
                .expiry(invite.getExpiry())
                .maxUses(invite.getMaxUses())
                .currentUses(invite.getCurrentUses())
                .joinType(joinRule.getType().name())
                .build();
    }

    private String generateUniqueToken() {
        for (int attempt = 0; attempt < MAX_TOKEN_ATTEMPTS; attempt++) {
            String token = generateToken();
            if (!inviteRepository.existsByToken(token)) {
                return token;
            }
        }
        throw new InvalidInviteException("Failed to generate a unique invite token");
    }

    private String generateToken() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(TOKEN_ALPHABET.charAt(SECURE_RANDOM.nextInt(TOKEN_ALPHABET.length())));
        }
        return token.toString();
    }

    private JoinType parseJoinType(String joinType) {
        if (!StringUtils.hasText(joinType)) {
            throw new InvalidInviteException("Join type is required");
        }

        try {
            return JoinType.valueOf(joinType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInviteException("Invalid join type: " + joinType);
        }
    }
}
