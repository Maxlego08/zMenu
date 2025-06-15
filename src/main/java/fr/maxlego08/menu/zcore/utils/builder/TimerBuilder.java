package fr.maxlego08.menu.zcore.utils.builder;

import fr.maxlego08.menu.api.utils.Message;

/**
 * Utility that formats a duration expressed in milliseconds into a human‑readable
 * string. In addition to seconds, minutes, hours and days, the class now
 * supports months (30 days) and years (365 days).
 */
public class TimerBuilder {

    // ---------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------

    private static final long SECOND = 1L;
    private static final long MINUTE = 60L;          // 60 seconds
    private static final long HOUR = 3600L;        // 60 minutes
    private static final long DAY = 86400L;       // 24 hours
    private static final long MONTH = 2592000L;     // 30 days  (≈ 2 592 000 s)
    private static final long YEAR = 31536000L;    // 365 days (≈ 31 536 000 s)

    // ---------------------------------------------------------------------
    // Public API
    // ---------------------------------------------------------------------

    public static String getStringTime(long second) {
        if (second < MINUTE) {
            return getFormatLongSecondes(second * 1000L);
        } else if (second < HOUR) {
            return getFormatLongMinutes(second * 1000L);
        } else if (second < DAY) {
            return getFormatLongHours(second * 1000L);
        } else if (second < MONTH) {
            return getFormatLongDays(second * 1000L);
        } else if (second < YEAR) {
            return getFormatLongMonths(second * 1000L);
        } else {
            return getFormatLongYears(second * 1000L);
        }
    }

    // ---------------------------------------------------------------------
    // Formatting helpers (largest → smallest unit)
    // ---------------------------------------------------------------------

    public static String getFormatLongYears(long millis) {
        long totalSecs = millis / 1000L;

        long years = totalSecs / YEAR;
        long months = (totalSecs % YEAR) / MONTH;
        long days = (totalSecs % MONTH) / DAY;
        long hours = (totalSecs % DAY) / HOUR;
        long minutes = (totalSecs % HOUR) / MINUTE;
        long seconds = totalSecs % MINUTE;

        String msg = Message.TIME_YEAR.msg();
        msg = msg.replace("%second%", (seconds <= 1 ? Message.FORMAT_SECOND : Message.FORMAT_SECONDS).msg());
        msg = msg.replace("%minute%", (minutes <= 1 ? Message.FORMAT_MINUTE : Message.FORMAT_MINUTES).msg());
        msg = msg.replace("%hour%", (hours <= 1 ? Message.FORMAT_HOUR : Message.FORMAT_HOURS).msg());
        msg = msg.replace("%day%", (days <= 1 ? Message.FORMAT_DAY : Message.FORMAT_DAYS).msg());
        msg = msg.replace("%month%", (months <= 1 ? Message.FORMAT_MONTH : Message.FORMAT_MONTHS).msg());
        msg = msg.replace("%year%", (years <= 1 ? Message.FORMAT_YEAR : Message.FORMAT_YEARS).msg());
        return format(String.format(msg, years, months, days, hours, minutes, seconds));
    }

    public static String getFormatLongMonths(long millis) {
        long totalSecs = millis / 1000L;

        long months = totalSecs / MONTH;
        long days = (totalSecs % MONTH) / DAY;
        long hours = (totalSecs % DAY) / HOUR;
        long minutes = (totalSecs % HOUR) / MINUTE;
        long seconds = totalSecs % MINUTE;

        String msg = Message.TIME_MONTH.msg();
        msg = msg.replace("%second%", (seconds <= 1 ? Message.FORMAT_SECOND : Message.FORMAT_SECONDS).msg());
        msg = msg.replace("%minute%", (minutes <= 1 ? Message.FORMAT_MINUTE : Message.FORMAT_MINUTES).msg());
        msg = msg.replace("%hour%", (hours <= 1 ? Message.FORMAT_HOUR : Message.FORMAT_HOURS).msg());
        msg = msg.replace("%day%", (days <= 1 ? Message.FORMAT_DAY : Message.FORMAT_DAYS).msg());
        msg = msg.replace("%month%", (months <= 1 ? Message.FORMAT_MONTH : Message.FORMAT_MONTHS).msg());
        return format(String.format(msg, months, days, hours, minutes, seconds));
    }

    public static String getFormatLongDays(long millis) {
        long totalSecs = millis / 1000L;

        long days = totalSecs / DAY;
        long hours = (totalSecs % DAY) / HOUR;
        long minutes = (totalSecs % HOUR) / MINUTE;
        long seconds = totalSecs % MINUTE;

        String msg = Message.TIME_DAY.msg();
        msg = msg.replace("%second%", (seconds <= 1 ? Message.FORMAT_SECOND : Message.FORMAT_SECONDS).msg());
        msg = msg.replace("%minute%", (minutes <= 1 ? Message.FORMAT_MINUTE : Message.FORMAT_MINUTES).msg());
        msg = msg.replace("%hour%", (hours <= 1 ? Message.FORMAT_HOUR : Message.FORMAT_HOURS).msg());
        msg = msg.replace("%day%", (days <= 1 ? Message.FORMAT_DAY : Message.FORMAT_DAYS).msg());
        return format(String.format(msg, days, hours, minutes, seconds));
    }

    public static String getFormatLongHours(long millis) {
        long totalSecs = millis / 1000L;

        long hours = totalSecs / HOUR;
        long minutes = (totalSecs % HOUR) / MINUTE;
        long seconds = totalSecs % MINUTE;

        String msg = Message.TIME_HOUR.msg();
        msg = msg.replace("%second%", (seconds <= 1 ? Message.FORMAT_SECOND : Message.FORMAT_SECONDS).msg());
        msg = msg.replace("%minute%", (minutes <= 1 ? Message.FORMAT_MINUTE : Message.FORMAT_MINUTES).msg());
        msg = msg.replace("%hour%", (hours <= 1 ? Message.FORMAT_HOUR : Message.FORMAT_HOURS).msg());
        return format(String.format(msg, hours, minutes, seconds));
    }

    public static String getFormatLongMinutes(long millis) {
        long totalSecs = millis / 1000L;

        long minutes = (totalSecs % HOUR) / MINUTE;
        long seconds = totalSecs % MINUTE;

        String msg = Message.TIME_MINUTE.msg();
        msg = msg.replace("%second%", (seconds <= 1 ? Message.FORMAT_SECOND : Message.FORMAT_SECONDS).msg());
        msg = msg.replace("%minute%", (minutes <= 1 ? Message.FORMAT_MINUTE : Message.FORMAT_MINUTES).msg());
        return format(String.format(msg, minutes, seconds));
    }

    public static String getFormatLongSecondes(long millis) {
        long totalSecs = millis / 1000L;
        long seconds = totalSecs % MINUTE;

        String msg = Message.TIME_SECOND.msg();
        msg = msg.replace("%second%", (seconds <= 1 ? Message.FORMAT_SECOND : Message.FORMAT_SECONDS).msg());
        return format(String.format(msg, seconds));
    }

    // ---------------------------------------------------------------------
    // Post‑processing: strip the zero‑value chunks ("00 secondes", etc.)
    // ---------------------------------------------------------------------

    public static String format(String message) {
        message = message.replace(" 00 " + Message.FORMAT_SECOND.msg(), "");
        message = message.replace(" 00 " + Message.FORMAT_MINUTE.msg(), "");
        message = message.replace(" 00 " + Message.FORMAT_HOUR.msg(), "");
        message = message.replace(" 00 " + Message.FORMAT_DAY.msg(), "");
        message = message.replace(" 00 " + Message.FORMAT_MONTH.msg(), "");
        message = message.replace(" 00 " + Message.FORMAT_YEAR.msg(), "");
        return message.trim();
    }
}
