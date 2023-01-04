package ru.yandex.tmanager;
import ru.yandex.tasks.Task;

import java.util.LinkedList;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    LinkedList<Task> getHistory();
}
