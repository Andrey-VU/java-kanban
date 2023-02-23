package ru.yandex.tmanager;

public class Managers {
   // public static TaskManager getDefault() { return new HttpTaskManager();}
    public static TaskManager getFileBackedManager() { return new FileBackedTasksManager();}
    public static TaskManager getDefault() { return new InMemoryTaskManager();}
    // public static TaskManager getInMemoryTaskManager() { return new InMemoryTaskManager();}
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}