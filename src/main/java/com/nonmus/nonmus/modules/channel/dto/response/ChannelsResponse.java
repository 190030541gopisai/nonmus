package com.nonmus.nonmus.modules.channel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelsResponse {
    private List<ChannelResponse> channels;
    private String nextCursor;
    private boolean hasNext;
}
