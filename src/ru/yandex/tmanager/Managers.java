package ru.yandex.tmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.tmanager.adapter.LocalDateTimeAdapter;

import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getFileBackedManager(String pathIn, String pathOut) {
        return new FileBackedTasksManager(pathIn, pathOut);
    }
    public static TaskManager getFileBackedManager(String path) {
        return new FileBackedTasksManager(path);
    }
    public static TaskManager getFileBackedManager() { return new FileBackedTasksManager(); }
    public static TaskManager getInMemoryTaskManager() { return new InMemoryTaskManager();}
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


    public static TaskManager getDefault(String path) { return new HttpTaskManager(path);}
    public static TaskManager getDefault(String s, String s1) { return new HttpTaskManager(s, s1);}


    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        return gson;
    }


}