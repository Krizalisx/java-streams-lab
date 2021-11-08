package com.demo;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateTimeApi {

    public static void main(String[] args) {

        LocalDate todayDate = LocalDate.of(2021, Month.NOVEMBER, 8);
        LocalTime timeNow = LocalTime.now();
        LocalDateTime dateTimeNow = LocalDateTime.of(todayDate, timeNow);

        System.out.println("LocalDate: " + todayDate);
        System.out.println("LocalTime: " + timeNow);
        System.out.println("LocalDateTime: " + dateTimeNow);

        System.out.println();

        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTimeNow, ZoneId.of("Europe/Moscow"));
        Instant instantDateTime = zonedDateTime.toInstant();// GMT/UTC
        OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTimeNow, ZoneOffset.of("+03:00"));

        System.out.println("ZonedDateTime: " + zonedDateTime);
        System.out.println("Instant: " + instantDateTime);
        System.out.println("OffsetDateTime: " + offsetDateTime);

        ZonedDateTime newYorkZonedDateTime = ZonedDateTime.of(dateTimeNow, ZoneId.of("America/New_York"));
        OffsetDateTime newYorkOffsetDateTime = OffsetDateTime.of(dateTimeNow, ZoneOffset.of("-05:00"));

        System.out.println();
        System.out.println("newYorkZoned: " + newYorkZonedDateTime);
        System.out.println("newYorkOffset: " + newYorkOffsetDateTime);

        System.out.println();
        System.out.println(newYorkZonedDateTime.plusMonths(6));
        System.out.println(newYorkOffsetDateTime.plusMonths(6));
        System.out.println(newYorkZonedDateTime.toEpochSecond() - newYorkOffsetDateTime.toEpochSecond());
        System.out.println(newYorkZonedDateTime.plusMonths(6).toEpochSecond() - newYorkOffsetDateTime.plusMonths(6).toEpochSecond());

        System.out.println(newYorkZonedDateTime.plusMonths(6).toLocalDateTime());
        System.out.println(newYorkOffsetDateTime.plusMonths(6).toLocalDateTime());

        LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.now());
        ZonedDateTime nowWithZone = ZonedDateTime.of(now, ZoneId.systemDefault());
        Instant instant = nowWithZone.toInstant();
        ZonedDateTime zonedDateTimeInNewYorkNow = ZonedDateTime.ofInstant(instant, ZoneId.of("America/New_York"));
        System.out.println(zonedDateTimeInNewYorkNow);
        System.out.println(zonedDateTimeInNewYorkNow.toLocalDateTime());
    }

}
