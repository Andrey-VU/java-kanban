package ru.yandex.tmanager;
import ru.yandex.tasks.Task;
import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    ArrayList<Task> getHistory();

}
