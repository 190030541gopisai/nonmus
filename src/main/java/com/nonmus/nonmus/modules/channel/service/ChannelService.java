package com.nonmus.nonmus.modules.channel.service;

import com.nonmus.nonmus.modules.channel.dto.internal.ChannelCursor;
import com.nonmus.nonmus.modules.channel.dto.internal.ChannelResult;
import com.nonmus.nonmus.modules.channel.dto.internal.CreateChannelResult;
import com.nonmus.nonmus.modules.channel.dto.internal.CursorPage;
import com.nonmus.nonmus.modules.channel.dto.request.CreateChannelRequest;
import com.nonmus.nonmus.modules.channel.entity.Channel;
import com.nonmus.nonmus.modules.channel.entity.ChannelMember;
import com.nonmus.nonmus.modules.channel.entity.ChannelStatistics;
import com.nonmus.nonmus.modules.channel.enums.ChannelRole;
import com.nonmus.nonmus.modules.channel.enums.ChannelType;
import com.nonmus.nonmus.modules.channel.repository.ChannelMemberRepository;
import com.nonmus.nonmus.modules.channel.repository.ChannelRepository;
import com.nonmus.nonmus.modules.common.exception.*;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final UsersService usersService;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;

    public CreateChannelResult createChannel(CreateChannelRequest request, String email) {
        Users user = usersService.getUsersByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found")
                );

        Channel channel = new Channel();

        ChannelType type = request.getType();
        if (type == ChannelType.PUBLIC) {
            String handle = request.getHandle();

            if(!StringUtils.hasText(handle)) {
                throw new InvalidChannelHandleException("Channel handle is empty or invalid");
            }

            if(isHandleAlreadyTaken(handle)) {
                throw new ChannelHandleAlreadyExistsException("Channel handle '" + handle + "' is already taken");
            }

            channel.setHandle(handle.trim());
        }

        channel.setName(request.getName());
        channel.setDescription(request.getDescription());
        channel.setType(type);

        channel.setCreatedBy(user);
        channel.setUpdatedBy(user);

        ChannelStatistics statistics = new ChannelStatistics();
        statistics.setSubscribersCount(1L);
        statistics.setChannel(channel);

        channel.setChannelStatistics(statistics);

        ChannelMember owner = new ChannelMember();
        owner.setUser(user);
        owner.setRole(ChannelRole.OWNER);

        channel.addMember(owner);

        channelRepository.save(channel);

        return CreateChannelResult.builder()
                .channelId(channel.getId())
                .handle(channel.getHandle())
                .name(channel.getName())
                .description(channel.getDescription())
                .logo(channel.getLogo())
                .type(type.name())
                .createdAt(channel.getCreatedAt())
                .subscribersCount(channel.getChannelStatistics().getSubscribersCount())
                .build();
    }

    private boolean isHandleAlreadyTaken(String handle) {
        return channelRepository.existsByHandle(handle);
    }

    @Transactional(readOnly = true)
    public ChannelResult getChannel(String email, UUID channelId) {
        Users user = usersService.getUsersByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        Channel channel = channelRepository.findById(channelId).orElseThrow(
                () -> new ChannelNotFoundException("Channel not found")
        );

        boolean isMemberOfChannel = channelMemberRepository.existsByChannelIdAndUserIdAndLeftAtIsNull(channelId, user.getId());

        ChannelType type = channel.getType();

        ChannelResult channelResult = ChannelResult.builder()
                .channelId(channel.getId())
                .handle(channel.getHandle())
                .name(channel.getName())
                .description(channel.getDescription())
                .logo(channel.getLogo())
                .subscribersCount(channel.getChannelStatistics().getSubscribersCount())
                .createdAt(channel.getCreatedAt())
                .isOwner(user == channel.getCreatedBy())
                .isMember(isMemberOfChannel)
                .build();

        if (type == ChannelType.PUBLIC) {
            return channelResult;
        }

        if (!isMemberOfChannel) {
            throw new ChannelAccessDeniedException("You are not a member of this channel");
        }

        return channelResult;
    }

    @Transactional(readOnly = true)
    public ChannelResult getChannel(String handle, String email) {
        Channel channel = channelRepository.findByHandle(handle).orElseThrow(
                () -> new ChannelNotFoundException("Channel not found")
        );

        return getChannel(email, channel.getId());
    }

    @Transactional(readOnly = true)
    public boolean getHandleAvailability(String handle) {
        if (!StringUtils.hasText(handle)) {
            throw new InvalidChannelHandleException(
                    "Handle is empty"
            );
        }

        return channelRepository.existsByHandleIgnoreCase(handle);
    }

    public CursorPage<ChannelResult> getAllSubscribedChannels(String email, ChannelCursor cursor, int limit) {
        Users user = usersService.getUsersByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        PageRequest pageRequest = PageRequest.of(0, limit + 1);

        List<ChannelMember> members = cursor == null
                ? channelMemberRepository.findSubscribed(user.getId(), pageRequest)
                : channelMemberRepository.findSubscribedAfter(
                        user.getId(),
                        cursor.joinedAt(),
                        cursor.memberId(),
                        pageRequest
                );

        boolean hasNext = members.size() > limit;
        if (hasNext) {
            members = members.subList(0, limit);
        }

        List<ChannelResult> channelResults = members.stream()
                .map(member -> {
                    Channel channel = member.getChannel();
                    return ChannelResult.builder()
                            .channelId(channel.getId())
                            .handle(channel.getHandle())
                            .name(channel.getName())
                            .description(channel.getDescription())
                            .logo(channel.getLogo())
                            .subscribersCount(channel.getChannelStatistics().getSubscribersCount())
                            .createdAt(channel.getCreatedAt())
                            .build();
                }).toList();

        String nextCursor = hasNext && !members.isEmpty()
                ? new ChannelCursor(
                        members.get(members.size() - 1).getJoinedAt(),
                        members.get(members.size() - 1).getId()
                ).encode()
                : null;

        return new CursorPage<>(channelResults, nextCursor, hasNext);
    }
}
