package com.nonmus.nonmus.modules.channel.controller;

import com.nonmus.nonmus.modules.channel.service.ChannelLogoService;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.user.dto.request.PresignedUrlRequest;
import com.nonmus.nonmus.modules.user.dto.response.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/channel/logo")
@RequiredArgsConstructor
public class ChannelLogoController {

    private final ChannelLogoService channelLogoService;

    @PutMapping("/presign-url")
    public ResponseEntity<PresignedUrlResponse> updateChannelLogo(@RequestBody PresignedUrlRequest request) {
        String email = AuthUtil.getPrincipal();
        PresignedUrlResponse response = channelLogoService.generatePresignUrl(request, email);
        return ResponseEntity.ok(response);
    }
}
