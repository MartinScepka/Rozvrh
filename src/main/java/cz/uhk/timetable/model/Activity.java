package cz.uhk.timetable.model;

import java.time.LocalTime;

/**
 * POJO (Plain Old Java Object) Rozvrhove aktivity
 */

public class Activity {
    private String code;
    private String name;
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String teacher;


    public Activity() {
    }

    public Activity(String code, String name, String day, LocalTime startTime, LocalTime endTime, String teacher) {
        this.code = code;
        this.name = name;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teacher = teacher;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
