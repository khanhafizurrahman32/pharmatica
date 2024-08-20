package org.example.pharmaticb.utilities;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static final ZoneId ZONE_ID_DHAKA = ZoneId.of("Asia/Dhaka");
    public static final String MW_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS Z";
    public static final DateTimeFormatter MW_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(MW_DATE_TIME_FORMAT);
    private static final String OTP_SMS_HOUR_MINUTE_FORMAT = "HHmm";
    private static final DateTimeFormatter OTP_SMS_HOUR_MINUTE_FORMATTER = DateTimeFormatter.ofPattern(OTP_SMS_HOUR_MINUTE_FORMAT);

    public static String formattedDateTime() {
        return MW_DATE_TIME_FORMATTER.format(getZonedDateTime());
    }

    public static ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.ofInstant(Instant.now(), ZONE_ID_DHAKA);
    }

    public static long currentTimeInSecond() {
        return currentTimeInMillisecond() / 1000;
    }

    private static long currentTimeInMillisecond() {
        return Instant.now().toEpochMilli();
    }

    public static String getOtpSmsHourMinute() {
        return OTP_SMS_HOUR_MINUTE_FORMATTER.format(getZonedDateTime());
    }
}
