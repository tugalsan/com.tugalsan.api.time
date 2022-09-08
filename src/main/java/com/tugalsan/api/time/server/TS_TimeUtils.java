package com.tugalsan.api.time.server;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinBase.*;
import com.sun.jna.win32.*;
import com.tugalsan.api.string.client.*;
import com.tugalsan.api.time.client.*;
import com.tugalsan.api.unsafe.client.*;
import java.time.*;
import java.time.zone.*;
import java.util.*;
import java.util.concurrent.*;

@Deprecated
public class TS_TimeUtils {

    public static String getTimeZoneId(TimeZone timeZone) {
        var milliDiff = Calendar.getInstance().get(Calendar.ZONE_OFFSET);
        return Arrays.stream(TimeZone.getAvailableIDs()).filter(id -> TimeZone.getTimeZone(id).getRawOffset() == milliDiff).findAny().orElse(null);
    }

    public static ZoneOffset getOffset(TimeZone timeZone) { //for using ZoneOffsett class
        ZoneId zi = timeZone.toZoneId();
        ZoneRules zr = zi.getRules();
        return zr.getOffset(LocalDateTime.now());
    }

    public static int getOffsetHours(TimeZone timeZone) { //just hour offset
        ZoneOffset zo = getOffset(timeZone);
        return (int) TimeUnit.SECONDS.toHours(zo.getTotalSeconds());
    }

    public static interface WinKernel32 extends StdCallLibrary {

        boolean SetLocalTime(SYSTEMTIME st);
        WinKernel32 instance = (WinKernel32) Native.load("kernel32.dll", WinKernel32.class);
    }

    public static boolean setDateAndTime(TGS_Time dateAndTime) {
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows")) {
            var st = new SYSTEMTIME();
            st.wYear = (short) dateAndTime.getYear();
            st.wMonth = (short) dateAndTime.getMonth();
            st.wDay = (short) dateAndTime.getDay();
            st.wHour = (short) dateAndTime.getHour();
            st.wMinute = (short) dateAndTime.getMinute();
            st.wSecond = (short) dateAndTime.getSecond();
            return WinKernel32.instance.SetLocalTime(st);
        } else {
            var b1 = execute(TGS_StringUtils.concat("date +%Y%m%d -s \"" + dateAndTime.getYear(), make2Chars(dateAndTime.getMonth()), make2Chars(dateAndTime.getDay()), "\""));
            var b2 = execute(TGS_StringUtils.concat("date +%T -s \"", make2Chars(dateAndTime.getHour()), ":", make2Chars(dateAndTime.getMinute()), ":", make2Chars(dateAndTime.getSecond()), "\""));
            return b1 && b2;
        }
    }

    private static String make2Chars(int i) {
        var is = String.valueOf(i);
        return is.length() < 2 ? TGS_StringUtils.concat("0", is) : is;
    }

    
    //NO DEP FUNCTION
    private static boolean execute(CharSequence commandLine) {
        return TGS_UnSafe.compile(() -> {
            var p = Runtime.getRuntime().exec(commandLine.toString());
            p.waitFor();
            return true;
        }, e -> {
            System.out.println(TS_TimeUtils.class.getSimpleName() + "->execute(CharSequence \"" + commandLine + "\")");
            e.printStackTrace();
            return false;
        });
    }
}
