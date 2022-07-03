package com.aw.assignment.memegene.meme;

import java.time.Instant;

public record ErrorResponse(int status, Instant timestamp, String message, String path) {
}
