package com.nonmus.nonmus.modules.channel.service;

import com.nonmus.nonmus.modules.channel.dto.request.CreateChannelRequest;
import com.nonmus.nonmus.modules.channel.dto.response.ChannelResponse;
import com.nonmus.nonmus.modules.channel.entity.Channels;
import com.nonmus.nonmus.modules.channel.repository.ChannelsRepository;
import com.nonmus.nonmus.modules.common.exception.UserNotFoundException;
import com.nonmus.nonmus.modules.common.util.AuthUtil;
import com.nonmus.nonmus.modules.user.entity.Users;
import com.nonmus.nonmus.modules.user.enums.Provider;
import com.nonmus.nonmus.modules.user.repository.UsersRepository;
import com.nonmus.nonmus.modules.user.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final UsersService usersService;
    private final ChannelsRepository channelsRepository;

    public ChannelResponse createChannel(CreateChannelRequest request, String email, Provider provider) {
        if(!usersService.existsByEmailAndProvider(email, provider)) {
            throw new UserNotFoundException("User not found");
        }

        Channels channel = new Channels();

        channel.setName(request.getName());
        channel.setDescription(request.getDescription());
        channel.setType(request.getType());

        Users user = usersService.getUsersByEmailAndProvider(email, provider).orElseThrow(() -> new UserNotFoundException("User not found"));
        channel.setCreatedBy(user);
        channel.setUpdatedBy(user);

        channelsRepository.save(channel);

        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setChannelId(channel.getId());

        return channelResponse;
    }
}
