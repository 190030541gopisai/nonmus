package com.nonmus.nonmus.modules.channel.controller;

import com.nonmus.nonmus.modules.channel.dto.internal.CreateChannelInviteResult;
import com.nonmus.nonmus.modules.channel.dto.request.CreateChannelInviteRequest;
import com.nonmus.nonmus.modules.channel.dto.request.JoinChannelRequest;
import com.nonmus.nonmus.modules.channel.dto.request.UpdateChannelInviteRequest;
import com.nonmus.nonmus.modules.channel.dto.response.CreateChannelInviteResponse;
import com.nonmus.nonmus.modules.channel.dto.response.InviteResponse;
import com.nonmus.nonmus.modules.channel.dto.response.JoinChannelResponse;
import com.nonmus.nonmus.modules.channel.entity.Invite;
import com.nonmus.nonmus.modules.channel.service.ChannelInviteService;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/channels/invite")
@RequiredArgsConstructor
public class ChannelInviteController {

    private final ChannelInviteService channelInviteService;

    @PostMapping
    public ResponseEntity<CreateChannelInviteResponse> createChannelInvite(@RequestBody CreateChannelInviteRequest request) {
        String email = AuthUtil.getAuthenticatedUserEmail();

        CreateChannelInviteResult createChannelInviteResult = channelInviteService.createChannelInvite(request, email);
        Invite invite = createChannelInviteResult.getInvite();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CreateChannelInviteResponse.builder()
                        .inviteId(invite.getId())
                        .channelId(invite.getChannel().getId())
                        .token(invite.getToken())
                        .expiry(invite.getExpiry())
                        .maxUses(invite.getMaxUses())
                        .currentUses(invite.getCurrentUses())
                        .joinType(invite.getInviteJoinRule().getType().name())
                        .build()
        );
    }

    @GetMapping("/{token}")
    public ResponseEntity<InviteResponse> getInviteInfo(@PathVariable String token) {
        InviteResponse inviteResponse = channelInviteService.getInviteInfo(token);
        return ResponseEntity.ok(inviteResponse);
    }

    @PostMapping("/join/{token}")
    public ResponseEntity<JoinChannelResponse> joinChannel(@PathVariable String token, @RequestBody(required = false) JoinChannelRequest request) {
        String email = AuthUtil.getAuthenticatedUserEmail();

        JoinChannelResponse joinChannelResponse = channelInviteService.joinChannel(token, request, email);
        return ResponseEntity.ok(joinChannelResponse);
    }

    @PutMapping("/{inviteId}")
    public ResponseEntity<CreateChannelInviteResponse> updateChannelInvite(@PathVariable UUID inviteId, @RequestBody UpdateChannelInviteRequest request) {
        String email = AuthUtil.getAuthenticatedUserEmail();

        CreateChannelInviteResponse response = channelInviteService.updateChannelInvite(inviteId, request, email);
        return ResponseEntity.ok(response);
    }
}
