package com.batch.Utilities;

import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import javafx.scene.control.DatePicker;

public class DateTimeFunctions {

    public static java.sql.Date fromDatePicker(DatePicker datpicker) {

        LocalDate localdate = datpicker.getValue();
        Instant instant = Instant.from(localdate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);
        java.sql.Date sqldte = new java.sql.Date(date.getTime());
        return sqldte;

    }
    public static LocalDate fromSQLDate(java.sql.Date sqldate) {
        
        long time = sqldate.getTime();
        Date date = new Date(time);
        Instant instant = date.toInstant();
        LocalDate localdate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return localdate;

    }
    
    public static Time getFromLocalTimeFormat(LocalTime localtime){
        return java.sql.Time.valueOf(localtime);
    }
    
     public static LocalTime getFromSQLTime(java.sql.Time time){
        return LocalTime.ofSecondOfDay(time.getSeconds());
    }
     
    public static java.sql.Date getCurrentDate(){
        return java.sql.Date.valueOf(LocalDate.now()); 
    }
    public static Time getCurrentTime(){
        return java.sql.Time.valueOf(LocalTime.now());
    }
}
