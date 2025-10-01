package com.tugalsan.api.time.server;

import module com.sun.jna;
import module com.sun.jna.platform;
import module com.tugalsan.api.function;
import module com.tugalsan.api.string;
import module com.tugalsan.api.time;
import module com.tugalsan.api.union;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public class TS_TimeUtils {

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

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

        boolean SetLocalTime(WinBase.SYSTEMTIME st);
        WinKernel32 instance = (WinKernel32) Native.load("kernel32.dll", WinKernel32.class);
    }

    public static TGS_UnionExcuseVoid setDateAndTime(TGS_Time dateAndTime) {
        if (Platform.isWindows()) {
            var st = new WinBase.SYSTEMTIME();
            st.wYear = (short) dateAndTime.getYear();
            st.wMonth = (short) dateAndTime.getMonth();
            st.wDay = (short) dateAndTime.getDay();
            st.wHour = (short) dateAndTime.getHour();
            st.wMinute = (short) dateAndTime.getMinute();
            st.wSecond = (short) dateAndTime.getSecond();
            var result = WinKernel32.instance.SetLocalTime(st);
            if (!result) {
                return TGS_UnionExcuseVoid.ofExcuse(TS_TimeUtils.class.getSimpleName(), "setDateAndTime", "result is false");
            }
            return TGS_UnionExcuseVoid.ofVoid();
        } else {
            var b1 = run(TGS_StringUtils.cmn().concat("date +%Y%m%d -s \"" + dateAndTime.getYear(), make2Chars(dateAndTime.getMonth()), make2Chars(dateAndTime.getDay()), "\""));
            if (b1.isExcuse()) {
                return b1;
            }
            var b2 = run(TGS_StringUtils.cmn().concat("date +%T -s \"", make2Chars(dateAndTime.getHour()), ":", make2Chars(dateAndTime.getMinute()), ":", make2Chars(dateAndTime.getSecond()), "\""));
            if (b2.isExcuse()) {
                return b2;
            }
            return TGS_UnionExcuseVoid.ofVoid();
        }
    }

    //NO DEP FUNCTION
    private static String make2Chars(int i) {
        var is = String.valueOf(i);
        return is.length() < 2 ? TGS_StringUtils.cmn().concat("0", is) : is;
    }

    //NO DEP FUNCTION
    private static TGS_UnionExcuseVoid run(CharSequence commandLine) {
        return TGS_FuncMTCUtils.call(() -> {
            var p = Runtime.getRuntime().exec(commandLine.toString());
            p.waitFor();
            return TGS_UnionExcuseVoid.ofVoid();
        }, e -> {
            return TGS_UnionExcuseVoid.ofExcuse(e);
        });
    }
}
