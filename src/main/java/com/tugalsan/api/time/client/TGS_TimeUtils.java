package com.tugalsan.api.time.client;

import com.tugalsan.api.cast.client.TGS_CastUtils;
import com.tugalsan.api.function.client.maythrow.checkedexceptions.TGS_FuncMTCEUtils;
import java.util.Date;

public class TGS_TimeUtils {

    final public static boolean ZERO_DAY_CONTAINS_CURRENT_YEAR = true;

    public static int SECS_TIMEOUT_MINUS_ONE() {
        return -1;
    }

    public static int SECS_TIMEOUT_ZERO() {
        return 0;
    }

    public static int SECS_TIMEOUT_MINUTE() {
        return 60;
    } // 60 seconds * 1 minutes

    public static int SECS_TIMEOUT_HOUR() {
        return SECS_TIMEOUT_MINUTE() * 60;
    } // 60 seconds * 1 minutes

    public static int SECS_TIMEOUT_HOURS_WORK() {
        return SECS_TIMEOUT_HOUR() * 9;
    } // 60 seconds * 60 minutes * 9 hours

    public static int SECS_TIMEOUT_DAY() {
        return SECS_TIMEOUT_HOUR() * 24;
    }// 60 seconds * 60 minutes * 24 hours

    public static int convertYearToCurrentYearIfPossibleIfZeroDateIsCurrentYear(int yri) {
        if (!ZERO_DAY_CONTAINS_CURRENT_YEAR) {
            return yri;
        }
        if (convertYearToCurrentYearIfPossible_limit == null) {
            var currentYearInt = new Date().getYear() + 1900;
            var charArray = String.valueOf(currentYearInt).toCharArray();
            for (var i = 1; i < charArray.length; i++) {
                charArray[i] = '0';
            }
            charArray[0] = '1';
            var str = String.valueOf(charArray);
            convertYearToCurrentYearIfPossible_limit = TGS_CastUtils.toInt(str, 0);
        }
        return yri < convertYearToCurrentYearIfPossible_limit ? yri + TGS_TimeUtils.zeroDateYearInt() : yri;
    }
    private static Integer convertYearToCurrentYearIfPossible_limit = null;

    public static int zeroDateYearInt() {
        if (zeroDateYearInt != null) {
            return zeroDateYearInt;
        }
        zeroDateYearInt = TGS_CastUtils.toInt(zeroDateYearStr(), 0);
        return zeroDateYearInt;
    }
    private static Integer zeroDateYearInt = null;

    public static String zeroDateYearStr() {
        if (zeroDateYearStr != null) {
            return zeroDateYearStr;
        }
        if (ZERO_DAY_CONTAINS_CURRENT_YEAR) {
            var currentYearInt = new Date().getYear() + 1900;
            var charArray = String.valueOf(currentYearInt).toCharArray();
            for (var i = 1; i < charArray.length; i++) {
                charArray[i] = '0';
            }
            zeroDateYearStr = String.valueOf(charArray);
        } else {
            zeroDateYearStr = "0";
        }
        return zeroDateYearStr;
    }
    private static String zeroDateYearStr = null;

    public static long zeroDateLng() {
        return zeroDateYearInt() * 10000L;
    }

    public static long zeroDateFirstDayLng() {
        return zeroDateLng() + 101L;
    }

    public static String zeroDateReadable() {
        return "00.00." + zeroDateYearStr();
    }

    public static boolean isValidOrZeroDate(Long lngDate) {
        return isZeroDate(lngDate) || isValidDate(lngDate);
    }

    public static boolean isZeroDate(Long lngDate) {
        if (lngDate == null) {
            return false;
        }
        return lngDate == zeroDateLng();
    }

    public static boolean isValidDate(Long lngDate) {
        return TGS_FuncMTCEUtils.call(() -> {
            if (lngDate == null) {
                return false;
            }
            var date = TGS_Time.ofDate(lngDate);
            var maxMonthDays = 0;
            switch (date.getMonth()) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    maxMonthDays = 31;
                    break;
                case 2:
                    maxMonthDays = date.getMonth() % 4 == 0 ? 29 : 28;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    maxMonthDays = 30;
                    break;
                default:
            }
            return date.getDay() > 0 && date.getDay() <= maxMonthDays && date.getMonth() > 0 && date.getMonth() <= 12;
        }, e -> false);
    }

    public static boolean isValidTime(Long lngTime) {
        return TGS_FuncMTCEUtils.call(() -> {
            if (lngTime == null) {
                return false;
            }
            var time = TGS_Time.ofTime(lngTime);
            return time.getHour() >= 0 && time.getHour() <= 23 && time.getMinute() >= 0 && time.getMinute() <= 59 && time.getSecond() >= 0 && time.getSecond() <= 59;
        }, e -> false);
    }
}
