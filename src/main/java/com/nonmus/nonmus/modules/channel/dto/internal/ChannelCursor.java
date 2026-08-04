package com.nonmus.nonmus.modules.channel.dto.internal;

import com.nonmus.nonmus.modules.common.exception.InvalidCursorException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

public record ChannelCursor(Instant joinedAt, UUID memberId) {

    private static final String SEPARATOR = ":";

    public static ChannelCursor from(String cursor) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split(SEPARATOR);
            return new ChannelCursor(
                    fromEpochNanos(Long.parseLong(parts[0])),
                    UUID.fromString(parts[1])
            );
        } catch (Exception e) {
            throw new InvalidCursorException("Invalid pagination cursor");
        }
    }

    public String encode() {
        long epochNanos = joinedAt.getEpochSecond() * 1_000_000_000L + joinedAt.getNano();
        String raw = epochNanos + SEPARATOR + memberId;
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private static Instant fromEpochNanos(long epochNanos) {
        return Instant.ofEpochSecond(
                Math.floorDiv(epochNanos, 1_000_000_000L),
                Math.floorMod(epochNanos, 1_000_000_000L)
        );
    }
}
