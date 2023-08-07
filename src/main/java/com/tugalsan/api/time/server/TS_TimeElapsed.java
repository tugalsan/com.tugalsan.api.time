package com.tugalsan.api.time.server;

import java.time.Duration;
import java.time.Instant;

public class TS_TimeElapsed {

    private TS_TimeElapsed() {
        start = Instant.now();
    }
    public Instant start;
    public Duration timeElapsed = null;

    public static TS_TimeElapsed of() {
        return new TS_TimeElapsed();
    }

    public TS_TimeElapsed restart() {
        start = Instant.now();
        timeElapsed = null;
        return this;
    }

    public TS_TimeElapsed end() {
        timeElapsed = Duration.between(start, Instant.now());
        return this;
    }
}
