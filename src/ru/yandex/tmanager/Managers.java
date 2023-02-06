package ru.yandex.tmanager;

public class Managers {
    public static TaskManager getDefault() { return new InMemoryTaskManager();}
    public static TaskManager getFileBackedManager() { return new FileBackedTasksManager();}

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}