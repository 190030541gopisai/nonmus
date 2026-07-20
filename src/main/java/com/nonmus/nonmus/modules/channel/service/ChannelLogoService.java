package com.nonmus.nonmus.modules.channel.service;

import com.nonmus.nonmus.modules.channel.repository.ChannelsRepository;
import com.nonmus.nonmus.modules.user.dto.request.PresignedUrlRequest;
import com.nonmus.nonmus.modules.user.dto.response.PresignedUrlResponse;
import com.nonmus.nonmus.modules.user.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelLogoService {
    private final ChannelsRepository channelsRepository;

    public PresignedUrlResponse generatePresignUrl(PresignedUrlRequest request, String email) {
        return null;
    }
}
