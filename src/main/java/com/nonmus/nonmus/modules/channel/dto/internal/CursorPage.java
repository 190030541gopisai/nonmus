package com.nonmus.nonmus.modules.channel.dto.internal;

import java.util.List;

public record CursorPage<T>(List<T> items, String nextCursor, boolean hasNext) {
}
