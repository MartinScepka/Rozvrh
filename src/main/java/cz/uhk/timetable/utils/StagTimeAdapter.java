package cz.uhk.timetable.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalTime;

public class StagTimeAdapter extends TypeAdapter<LocalTime> {
    @Override
    public void write(JsonWriter jsonWriter, LocalTime localTime) throws IOException {

    }

    @Override
    public LocalTime read(JsonReader in) throws IOException {
        in.beginObject(); // {
        in.nextName(); //      "value:"
        var timeStr = in.nextString(); // "HH:MM"
        in.endObject(); // }

        return LocalTime.parse(timeStr);
    }
}
