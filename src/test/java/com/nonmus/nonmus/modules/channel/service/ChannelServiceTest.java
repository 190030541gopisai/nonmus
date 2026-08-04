package com.nonmus.nonmus.modules.channel.service;

import com.nonmus.nonmus.modules.channel.dto.internal.ChannelCursor;
import com.nonmus.nonmus.modules.channel.dto.internal.ChannelResult;
import com.nonmus.nonmus.modules.channel.dto.internal.CursorPage;
import com.nonmus.nonmus.modules.channel.entity.Channel;
import com.nonmus.nonmus.modules.channel.entity.ChannelMember;
import com.nonmus.nonmus.modules.channel.entity.ChannelStatistics;
import com.nonmus.nonmus.modules.channel.repository.ChannelMemberRepository;
import com.nonmus.nonmus.modules.channel.repository.ChannelRepository;
import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.service.UsersService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private UsersService usersService;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ChannelMemberRepository channelMemberRepository;

    @InjectMocks
    private ChannelService channelService;

    private final String email = "test@test.com";
    private final UUID userId = UUID.randomUUID();

    private Users user() {
        Users user = new Users();
        user.setId(userId);
        return user;
    }

    private Channel channel(String name, String handle) {
        Channel channel = new Channel();
        channel.setId(UUID.randomUUID());
        channel.setName(name);
        channel.setHandle(handle);
        channel.setDescription("Description for " + name);
        channel.setLogo("logo-" + handle);

        ChannelStatistics statistics = new ChannelStatistics();
        statistics.setSubscribersCount(1L);
        channel.setChannelStatistics(statistics);
        return channel;
    }

    private ChannelMember member(UUID id, Channel channel, Instant joinedAt) {
        ChannelMember member = new ChannelMember();
        member.setId(id);
        member.setChannel(channel);
        member.setJoinedAt(joinedAt);
        return member;
    }

    @Test
    void getAllSubscribedChannels_whenMoreThanLimit_returnsHasNextAndCursor() {
        when(usersService.getUsersByEmail(email)).thenReturn(Optional.of(user()));

        List<ChannelMember> members = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            members.add(member(UUID.randomUUID(), channel("Channel " + i, "c" + i), Instant.now().minusSeconds(i)));
        }
        when(channelMemberRepository.findSubscribed(eq(userId), any(Pageable.class))).thenReturn(members);

        CursorPage<ChannelResult> page = channelService.getAllSubscribedChannels(email, null, 2);

        assertEquals(2, page.items().size());
        assertTrue(page.hasNext());
        assertNotNull(page.nextCursor());

        ChannelCursor decoded = ChannelCursor.from(page.nextCursor());
        assertEquals(members.get(1).getId(), decoded.memberId());
        assertEquals(members.get(1).getJoinedAt(), decoded.joinedAt());
        verify(channelMemberRepository).findSubscribed(eq(userId), any(Pageable.class));
        verify(channelMemberRepository, never()).findSubscribedAfter(any(), any(), any(), any());
    }

    @Test
    void getAllSubscribedChannels_whenLastPage_returnsHasNextFalseAndNullCursor() {
        when(usersService.getUsersByEmail(email)).thenReturn(Optional.of(user()));
        when(channelMemberRepository.findSubscribed(eq(userId), any(Pageable.class)))
                .thenReturn(List.of(
                        member(UUID.randomUUID(), channel("A", "a"), Instant.now()),
                        member(UUID.randomUUID(), channel("B", "b"), Instant.now().minusSeconds(1))
                ));

        CursorPage<ChannelResult> page = channelService.getAllSubscribedChannels(email, null, 2);

        assertEquals(2, page.items().size());
        assertFalse(page.hasNext());
        assertNull(page.nextCursor());
    }

    @Test
    void getAllSubscribedChannels_withCursor_forwardsDecodedCursorToRepository() {
        when(usersService.getUsersByEmail(email)).thenReturn(Optional.of(user()));
        when(channelMemberRepository.findSubscribedAfter(any(), any(), any(), any())).thenReturn(List.of());

        ChannelCursor cursor = new ChannelCursor(Instant.parse("2026-01-01T00:00:00Z"), UUID.randomUUID());

        CursorPage<ChannelResult> page = channelService.getAllSubscribedChannels(email, cursor, 10);

        assertTrue(page.items().isEmpty());
        assertFalse(page.hasNext());
        assertNull(page.nextCursor());
        verify(channelMemberRepository).findSubscribedAfter(
                eq(userId), eq(cursor.joinedAt()), eq(cursor.memberId()), any(Pageable.class));
        verify(channelMemberRepository, never()).findSubscribed(any(), any());
    }

    @Test
    void getAllSubscribedChannels_whenUserNotFound_throws() {
        when(usersService.getUsersByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> channelService.getAllSubscribedChannels(email, null, 10));
    }
}
