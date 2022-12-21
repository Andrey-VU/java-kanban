package ru.yandex.tmanager;
import ru.yandex.tasks.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{

    private LinkedList<Task> historyOfView = new LinkedList<Task>();

    @Override
    public void add(Task task) {
        isHistoryStorageIsFull();
        historyOfView.add(task);
    }

    @Override
    public LinkedList<Task> getHistory() {
        return historyOfView;
        }

    private void isHistoryStorageIsFull() {
        if (historyOfView.size() > 9) {
            historyOfView.remove(0);
        }
    }
}
