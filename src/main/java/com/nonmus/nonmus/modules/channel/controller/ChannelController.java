package com.nonmus.nonmus.modules.channel.controller;

import com.nonmus.nonmus.modules.channel.dto.internal.ChannelCursor;
import com.nonmus.nonmus.modules.channel.dto.internal.ChannelResult;
import com.nonmus.nonmus.modules.channel.dto.internal.CreateChannelResult;
import com.nonmus.nonmus.modules.channel.dto.internal.CursorPage;
import com.nonmus.nonmus.modules.channel.dto.request.CreateChannelRequest;
import com.nonmus.nonmus.modules.channel.dto.response.ChannelResponse;
import com.nonmus.nonmus.modules.channel.dto.response.ChannelsResponse;
import com.nonmus.nonmus.modules.channel.dto.response.CreateChannelInviteResponse;
import com.nonmus.nonmus.modules.channel.dto.response.CreateChannelResponse;
import com.nonmus.nonmus.modules.channel.dto.response.HandleAvailabilityResponse;
import com.nonmus.nonmus.modules.channel.service.ChannelInviteService;
import com.nonmus.nonmus.modules.channel.service.ChannelService;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/channels")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;
    private final ChannelInviteService channelInviteService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<CreateChannelResponse> createChannel(@RequestBody CreateChannelRequest request) {
        String email = AuthUtil.getAuthenticatedUserEmail();

        CreateChannelResult createChannelResult = channelService.createChannel(request, email);
        CreateChannelResponse channelResponse = modelMapper.map(createChannelResult, CreateChannelResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
    }

    @GetMapping("/id/{channelId}")
    public ResponseEntity<ChannelResponse> getChannel(@PathVariable UUID channelId) {
        String email = AuthUtil.getAuthenticatedUserEmail();

        ChannelResult channelResult = channelService.getChannel(email, channelId);
        ChannelResponse channelResponse = modelMapper.map(channelResult, ChannelResponse.class);

        return ResponseEntity.ok(channelResponse);
    }

    @GetMapping("/handle/{handle}")
    public ResponseEntity<ChannelResponse> getChannel(@PathVariable String handle) {
        String email = AuthUtil.getAuthenticatedUserEmail();

        ChannelResult channelResult = channelService.getChannel(handle, email);
        ChannelResponse channelResponse = modelMapper.map(channelResult, ChannelResponse.class);

        return ResponseEntity.ok(channelResponse);
    }

    @GetMapping("/subscribed")
    public ResponseEntity<ChannelsResponse> getMyAllSubscribedChannels(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int limit) {
        String email = AuthUtil.getAuthenticatedUserEmail();

        int pageSize = Math.min(Math.max(limit, 1), 50);

        ChannelCursor channelCursor = null;

        if(StringUtils.hasText(cursor)) {
            channelCursor = ChannelCursor.from(cursor);
        }

        CursorPage<ChannelResult> cursorPage = channelService.getAllSubscribedChannels(email, channelCursor, pageSize);
        List<ChannelResponse> channelResponses = cursorPage.items().stream()
                .map(channelResult -> modelMapper.map(channelResult, ChannelResponse.class))
                .toList();

        return ResponseEntity.ok(
                ChannelsResponse.builder()
                        .channels(channelResponses)
                        .nextCursor(cursorPage.nextCursor())
                        .hasNext(cursorPage.hasNext())
                        .build()
        );
    }

    @GetMapping("/{channelId}/invites")
    public ResponseEntity<List<CreateChannelInviteResponse>> getChannelInvites(@PathVariable UUID channelId) {
        String email = AuthUtil.getAuthenticatedUserEmail();

        List<CreateChannelInviteResponse> invites = channelInviteService.listChannelInvites(channelId, email);

        return ResponseEntity.ok(invites);
    }

    @GetMapping("/handle/{handle}/availability")
    public ResponseEntity<HandleAvailabilityResponse> getHandleAvailability(@PathVariable String handle) {
        boolean isHandleAvailable = channelService.getHandleAvailability(handle);

        return ResponseEntity.ok(
                HandleAvailabilityResponse.builder()
                        .isAvailable(isHandleAvailable)
                        .build()
        );
    }
}
