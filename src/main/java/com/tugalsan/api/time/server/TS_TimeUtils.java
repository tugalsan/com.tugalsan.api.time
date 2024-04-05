package com.tugalsan.api.time.server;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinBase.*;
import com.sun.jna.win32.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.union.client.TGS_Union;
import com.tugalsan.api.union.server.TS_UnionUtils;
import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public class TS_TimeUtils {

    public static String toString(Duration dur) {
        return String.format("%d:%02d:%02d",
                dur.toHours(),
                dur.toMinutesPart(),
                dur.toSecondsPart());
    }

    public static Instant toInstant(Duration duration) {
        return Instant.now().plusSeconds(duration.getSeconds());
    }

    public static String getTimeZoneId(TimeZone timeZone) {
        var milliDiff = Calendar.getInstance().get(Calendar.ZONE_OFFSET);
        return Arrays.stream(TimeZone.getAvailableIDs()).filter(id -> TimeZone.getTimeZone(id).getRawOffset() == milliDiff).findAny().orElse(null);
    }

    public static ZoneOffset getOffset(TimeZone timeZone) { //for using ZoneOffsett class
        var zi = timeZone.toZoneId();
        var zr = zi.getRules();
        return zr.getOffset(LocalDateTime.now());
    }

    public static int getOffsetHours(TimeZone timeZone) { //just hour offset
        var zo = getOffset(timeZone);
        return (int) TimeUnit.SECONDS.toHours(zo.getTotalSeconds());
    }

    public static interface WinKernel32 extends StdCallLibrary {

        boolean SetLocalTime(SYSTEMTIME st);
        WinKernel32 instance = (WinKernel32) Native.load("kernel32.dll", WinKernel32.class);
    }

    public static TGS_Union<Boolean> setDateAndTime(TGS_Time dateAndTime) {
        if (Platform.isWindows()) {
            var st = new SYSTEMTIME();
            st.wYear = (short) dateAndTime.getYear();
            st.wMonth = (short) dateAndTime.getMonth();
            st.wDay = (short) dateAndTime.getDay();
            st.wHour = (short) dateAndTime.getHour();
            st.wMinute = (short) dateAndTime.getMinute();
            st.wSecond = (short) dateAndTime.getSecond();
            return TGS_Union.of(WinKernel32.instance.SetLocalTime(st));
        } else {
            var b1 = run(TGS_StringUtils.concat("date +%Y%m%d -s \"" + dateAndTime.getYear(), make2Chars(dateAndTime.getMonth()), make2Chars(dateAndTime.getDay()), "\""));
            if (b1.isEmpty()) {
                return b1;
            }
            var b2 = run(TGS_StringUtils.concat("date +%T -s \"", make2Chars(dateAndTime.getHour()), ":", make2Chars(dateAndTime.getMinute()), ":", make2Chars(dateAndTime.getSecond()), "\""));
            if (b1.isEmpty()) {
                return b2;
            }
            return TGS_Union.of(true);
        }
    }

    //NO DEP FUNCTION
    private static String make2Chars(int i) {
        var is = String.valueOf(i);
        return is.length() < 2 ? TGS_StringUtils.concat("0", is) : is;
    }

    //NO DEP FUNCTION
    private static TGS_Union<Boolean> run(CharSequence commandLine) {
        try {
            var p = Runtime.getRuntime().exec(commandLine.toString());
            p.waitFor();
            return TGS_Union.of(true);
        } catch (IOException e) {
            return TGS_Union.ofThrowable(e);
        } catch (InterruptedException ex) {
            return TS_UnionUtils.throwIfRuntimeException(ex);
        }
    }
}
