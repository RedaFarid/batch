//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.batch.Utilities;

import java.sql.Date;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import javafx.scene.control.DatePicker;

public class DateTimeFunctions {
    public static Date fromDatePicker(DatePicker datpicker) {
        LocalDate localdate = (LocalDate)datpicker.getValue();
        Instant instant = Instant.from(localdate.atStartOfDay(ZoneId.systemDefault()));
        java.util.Date date = java.util.Date.from(instant);
        Date sqldte = new Date(date.getTime());
        return sqldte;
    }

    public static LocalDate fromSQLDate(Date sqldate) {
        long time = sqldate.getTime();
        java.util.Date date = new java.util.Date(time);
        Instant instant = date.toInstant();
        LocalDate localdate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return localdate;
    }

    public static Time getFromLocalTimeFormat(LocalTime localtime) {
        return Time.valueOf(localtime);
    }

    public static LocalTime getFromSQLTime(Time time) {
        return LocalTime.ofSecondOfDay((long)time.getSeconds());
    }

    public static Date getCurrentDate() {
        return Date.valueOf(LocalDate.now());
    }

    public static Time getCurrentTime() {
        return Time.valueOf(LocalTime.now());
    }
}
