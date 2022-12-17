package ru.yandex.taskManager;
import ru.yandex.tasks.Task;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class InMemoryHistoryManager implements HistoryManager{
    private List<Task> historyOfView = new List<Task>() {
        @Override
        public int size() {
            return 0;
        }
        @Override
        public boolean isEmpty() {
            return false;
        }
        @Override
        public boolean contains(Object o) {
            return false;
        }
        @Override
        public Iterator<Task> iterator() {
            return null;
        }
        @Override
        public Object[] toArray() {
            return new Object[0];
        }
        @Override
        public <T> T[] toArray(T[] ts) {
            return null;
        }
        @Override
        public boolean add(Task task) {
            return false;
        }
        @Override
        public boolean remove(Object o) {
            return false;
        }
        @Override
        public boolean containsAll(Collection<?> collection) {
            return false;
        }
        @Override
        public boolean addAll(Collection<? extends Task> collection) {
            return false;
        }
        @Override
        public boolean addAll(int i, Collection<? extends Task> collection) {
            return false;
        }
        @Override
        public boolean removeAll(Collection<?> collection) {
            return false;
        }
        @Override
        public boolean retainAll(Collection<?> collection) {
            return false;
        }
        @Override
        public void clear() {
        }
        @Override
        public Task get(int i) {
            return null;
        }
        @Override
        public Task set(int i, Task task) {
            return null;
        }
        @Override
        public void add(int i, Task task) {
        }
        @Override
        public Task remove(int i) {
            return null;
        }
        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }
        @Override
        public ListIterator<Task> listIterator() {
            return null;
        }
        @Override
        public ListIterator<Task> listIterator(int i) {
            return null;
        }
        @Override
        public List<Task> subList(int i, int i1) {
            return null;
        }
    };

    @Override
    public void add(Task task) {
        isHistoryStorageIsFull();
        historyOfView.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyOfView;
        }

    private void isHistoryStorageIsFull() {
        if (historyOfView.size() > 9) {
            historyOfView.remove(0);
        }
    }
}
