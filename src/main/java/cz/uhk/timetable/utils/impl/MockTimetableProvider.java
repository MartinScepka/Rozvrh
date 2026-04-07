package cz.uhk.timetable.utils.impl;

import cz.uhk.timetable.model.Activity;
import cz.uhk.timetable.model.LocationTimetable;
import cz.uhk.timetable.utils.TimetableProvider;

import java.time.LocalTime;
import java.util.List;

/**
 * falesna trida providera se vzorovymi daty
 */
public class MockTimetableProvider implements TimetableProvider {
    @Override
    public LocationTimetable read(String building, String room) {
        var tt = new LocationTimetable(building, room);
        var activities = List.of(
            new Activity("PRO1", "Programovani 1", "utery", LocalTime.of(12,25), LocalTime.of(13, 55), "Kozel"),
            new Activity("OS1A", "Operacni Systemy 1", "streda", LocalTime.of(14,50), LocalTime.of(16, 25), "Almer"),
            new Activity("ZMI2", "Zaklady Matematiky 2", "pondeli", LocalTime.of(11,35), LocalTime.of(13, 10), "Bauer")
        );
        tt.setActivities(activities);
        return tt;
    }
}
