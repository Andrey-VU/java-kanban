package ru.yandex.tmanager.adapter;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy--HH:mm");
    private final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("dd.MM.yyyy--HH:mm");
    private final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("dd.MM.yyyy--HH:mm");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        // приводим localDate к необходимому формату
        jsonWriter.value(localDateTime.format(formatterWriter));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), formatterReader);
    }
}
