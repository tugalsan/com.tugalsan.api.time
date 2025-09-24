package com.tugalsan.api.time.server;

import java.time.Duration;
import java.util.function.Supplier;

public class TS_Duration {

    private TS_Duration(Supplier<Duration> supplier) {
        this.supplier = supplier;
    }
    final private Supplier<Duration> supplier;

    public static TS_Duration ofSeconds(int seconds) {
        return new TS_Duration(() -> Duration.ofSeconds(seconds));
    }

    public static TS_Duration ofMinutes(int minutes) {
        return new TS_Duration(() -> Duration.ofMinutes(minutes));
    }

    public static TS_Duration ofHours(int hours) {
        return new TS_Duration(() -> Duration.ofHours(hours));
    }

    public static TS_Duration ofDays(int days) {
        return new TS_Duration(() -> Duration.ofDays(days));
    }
}
