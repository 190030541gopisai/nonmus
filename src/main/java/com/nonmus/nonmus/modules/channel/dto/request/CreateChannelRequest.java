package com.nonmus.nonmus.modules.channel.dto.request;

import com.nonmus.nonmus.modules.channel.enums.ChannelType;
import lombok.Data;

@Data
public class CreateChannelRequest {
    private String name;
    private String description;
    private ChannelType type;
    private String handle;
}
