package ru.yandex.taskManager;
import ru.yandex.tasks.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
}
