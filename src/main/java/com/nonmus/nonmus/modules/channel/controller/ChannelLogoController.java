package com.nonmus.nonmus.modules.channel.controller;

import com.nonmus.nonmus.modules.channel.service.ChannelLogoService;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.user.dto.request.PresignedUrlRequest;
import com.nonmus.nonmus.modules.user.dto.response.PresignedUrlResponse;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.security.AuthenticatedUser;
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
        AuthenticatedUser authenticatedUser = AuthUtil.getPrincipal();
        String email = authenticatedUser.getEmail();
        Provider provider = authenticatedUser.getProvider();
        PresignedUrlResponse response = channelLogoService.generatePresignUrl(request, email, provider);
        return ResponseEntity.ok(response);
    }
}
