package ru.yandex.tmanager;
import ru.yandex.tasks.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Node<Task>> historyOfView = new HashMap<>();  // для хранения истории просмотров
    private Node<Task> head;       // Указатель на первый элемент списка. Он же first
    private Node<Task> tail;       // Указатель на последний элемент списка. Он же last
    private int size = 0;          // Размер хранилища

    public void linkLast(Task task) {
        if (historyOfView.containsKey(task.getId())) {
            remove(task.getId());
        }
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, task, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
            historyOfView.put(task.getId(), newNode);

    }

    public void removeNode(Node<Task> node) {
            if (historyOfView.keySet().contains(node.item.getId()) && node != null) {
                int idForDell = node.item.getId();
                if (head.equals(node) && tail.equals(node)) {                         // нода первая и последняя
                    node = null;
                    head = null;
                    tail = null;
                } else if (head.equals(node)) {                       // нода первая, но не последняя
                    head = node.next;
                    head.prev = null;
                } else if (!tail.equals(node)) {                      // нода не первая и не последняя
                    Node <Task> oldNodePrev = node.prev;
                    Node <Task> oldNodeNext = node.next;
                    oldNodePrev.next = oldNodeNext;
                    oldNodeNext.prev = oldNodePrev;
                } else {                                               // нода самая распоследняя
                    node.prev.next = null;
                    tail = node.prev;
                    node = null;

                }
                historyOfView.remove(idForDell);
            size--;
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        List<Task> rangeOfView = new ArrayList<Task>();
        Node<Task> tmpNode = head;
        while (tmpNode != null) {
            rangeOfView.add(tmpNode.item);
            tmpNode = tmpNode.next;
        }
        return rangeOfView.isEmpty() ? null : (ArrayList<Task>) rangeOfView;
    }

    public void add(Task task) {
        if (historyOfView.keySet().contains(task.getId())) {
            if (historyOfView.get(task.getId()) != null) {
                removeNode(historyOfView.get(task.getId()));
            }
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        if (historyOfView.get(id) != null) {
            removeNode(historyOfView.get(id));
        }
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
