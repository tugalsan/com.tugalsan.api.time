module com.tugalsan.api.time {
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires com.tugalsan.api.cast;
    requires com.tugalsan.api.union;
    requires com.tugalsan.api.charset;
    requires com.tugalsan.api.function;
    requires com.tugalsan.api.string;
    exports com.tugalsan.api.time.client;
    exports com.tugalsan.api.time.server;
}
