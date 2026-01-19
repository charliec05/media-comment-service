package com.charliec.commentservice.api.response;

import java.time.Instant;

public record ErrorResponse(
        String code,
        String message,
        String path,
        String traceId,
        Instant timestamp
) {}
