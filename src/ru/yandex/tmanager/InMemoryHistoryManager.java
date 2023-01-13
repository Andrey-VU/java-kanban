package ru.yandex.tmanager;
import ru.yandex.tasks.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{
    Map<Integer, Node<Task>> historyOfView = new HashMap<>();  // для хранения истории просмотров
    private Node<Task> head;       // Указатель на первый элемент списка. Он же first
    private Node<Task> tail;       // Указатель на последний элемент списка. Он же last
    private int size = 0;          // Размер хранилища

    public void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.prev = newNode;
        size++;
        historyOfView.put(task.getId(), newNode);
    }

    public ArrayList<Task> getTask(){
        List<Task> listOfView = new ArrayList<Task>();
        for (Node<Task> value : historyOfView.values()) {
            listOfView.add(value.item);
        }
        return (ArrayList<Task>) listOfView;
    }

    public void removeNode(Node<Task> node) {
        if (historyOfView.keySet().contains(node.item.getId())) {
        historyOfView.remove(node.item.getId());
        }
    }

    @Override
    public void add(Task task) {
        if (historyOfView.keySet().contains(task.getId())) {
            removeNode(historyOfView.get(task.getId()));
        }
        linkLast(task);
        }

    @Override
    public void remove(int id) {
        historyOfView.remove(id);
    }
    @Override
    public ArrayList<Task> getHistory() {
        return getTask();
    }

    private static class Node<Task> {
        Task item;
        Node<Task> next;
        Node<Task> prev;

        public Node(Node<Task> prev, Task item, Node<Task> next) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }
}


