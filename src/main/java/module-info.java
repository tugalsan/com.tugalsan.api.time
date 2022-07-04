module com.tugalsan.api.time {
    requires gwt.user;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires com.tugalsan.api.cast;
    requires com.tugalsan.api.unsafe;
    requires com.tugalsan.api.compiler;
    requires com.tugalsan.api.string;
    exports com.tugalsan.api.time.client;
    exports com.tugalsan.api.time.server;
}
