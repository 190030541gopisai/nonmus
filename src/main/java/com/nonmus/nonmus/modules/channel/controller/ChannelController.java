package com.nonmus.nonmus.modules.channel.controller;

import com.nonmus.nonmus.modules.channel.dto.response.ChannelResponse;
import com.nonmus.nonmus.modules.channel.service.ChannelService;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nonmus.nonmus.modules.channel.dto.request.CreateChannelRequest;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/channel")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }
    
    @PostMapping
    public ResponseEntity<ChannelResponse> createChannel(@RequestBody CreateChannelRequest request) {
        String email = AuthUtil.getPrincipal();
        ChannelResponse channelResponse = channelService.createChannel(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(channelResponse);
    }
}
