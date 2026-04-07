package cz.uhk.timetable.utils;

import cz.uhk.timetable.model.LocationTimetable;

public interface TimetableProvider {
    LocationTimetable read(String building, String room);
}
