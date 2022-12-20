package ru.yandex.tmanager;
import ru.yandex.tasks.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{

    private LinkedList<Task> historyOfView = new LinkedList<Task>() {
        // Артём, добрый день! Я просто переписал LinkedList в строчке выше вместо List
        // а также удалил все автоматически доабвленные ранее методы.
        // До конца не уверен, что верно вношу здесь изменения
        // удивлен синтаксису следующей скобки ' }; '
        // первый раз такое вижу, чтобы точка с запятой после скобки
    };

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
