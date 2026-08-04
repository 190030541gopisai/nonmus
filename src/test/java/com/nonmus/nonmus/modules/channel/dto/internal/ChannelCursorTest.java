package com.nonmus.nonmus.modules.channel.dto.internal;

import com.nonmus.nonmus.modules.common.exception.InvalidCursorException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChannelCursorTest {

    @Test
    void encodeDecode_roundTrip() {
        ChannelCursor cursor = new ChannelCursor(Instant.parse("2026-01-01T00:00:00Z"), UUID.randomUUID());

        ChannelCursor decoded = ChannelCursor.from(cursor.encode());

        assertEquals(cursor, decoded);
    }

    @Test
    void from_withInvalidCursor_throws() {
        assertThrows(InvalidCursorException.class, () -> ChannelCursor.from("not-a-valid-cursor"));
        assertThrows(InvalidCursorException.class, () -> ChannelCursor.from(""));
    }
}
