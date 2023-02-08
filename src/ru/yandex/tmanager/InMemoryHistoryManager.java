package ru.yandex.tmanager;
import ru.yandex.tasks.Task;
import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
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
            oldTail.next = newNode;
        size++;
        historyOfView.put(task.getId(), newNode);
    }

    public void removeNode(Node<Task> node) {
            if (historyOfView.keySet().contains(node.item.getId()) && node != null) {
            Node<Task> prevNode = node.equals(head) ? head = null : node.prev;
            Node<Task> nextNode = node.equals(tail) ? tail = null : node.next;
            historyOfView.remove(node.item.getId());
            if (prevNode != null) {
                prevNode.next = nextNode;
            }
            if (nextNode != null) {
                nextNode.prev = prevNode;
            }
            size--;
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        List<Task> rangeOfView = new ArrayList<Task>();
        if (head!= null) {
            Node<Task> tmpNode = head;
            while (tmpNode.next != null) {
                rangeOfView.add(tmpNode.item);
                tmpNode = tmpNode.next;
            }
            rangeOfView.add(tmpNode.item);
        }
        return (ArrayList<Task>) rangeOfView;
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
        if (historyOfView.keySet().contains(id)) {
            removeNode(historyOfView.remove(id));
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
