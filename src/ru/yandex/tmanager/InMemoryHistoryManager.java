package ru.yandex.tmanager;
import ru.yandex.tasks.Task;

import java.beans.Introspector;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{
    Map<Integer, Node<Task>> historyOfView = new HashMap<>();  // для хранения истории просмотров
    List<Node<Task>> rangeOfView = new ArrayList<Node<Task>>();     // для хранения порядка просмотра
    List<Integer> rangeOfViewId = new ArrayList<Integer>();     // для хранения порядка просмотра по Ид
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
        rangeOfViewId.add(task.getId());             // для сохранения порядка хранения по Ид
    }
    public void removeNode(Node<Task> node) {
        if (historyOfView.keySet().contains(node.item.getId())) {
            Node<Task> prevNode = node.prev;
            Node<Task> nextNode = node.next;
            historyOfView.remove(node.item.getId());
            if (prevNode != null) {
                prevNode.next = nextNode;
            }
            if (nextNode != null) {
                nextNode.prev = prevNode;
            }
        }
    }

    public List<Node<Task>> getTask(){
        List<Node<Task>> listOfTask = new ArrayList<>();
        for (Node<Task> value : historyOfView.values()) {
            listOfTask.add(value);
        }
        return listOfTask;
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
            removeNode(historyOfView.remove(id));
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> tmp = new ArrayList<>();
        for (Node<Task> taskNode : getTask()) {
            tmp.add(taskNode.item);
        }
        return tmp;
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


/*
               !node.prev.equals(null)
               int indexPrev = rangeOfViewId.indexOf((node.item.getId()) - 1);
                Integer idOfPrev = rangeOfViewId.get(indexPrev);
                prevNode = historyOfView.get(idOfPrev);
                prevNode.prev = node.prev; */
/*
                !node.next.equals(null)
                int indexNext = rangeOfViewId.indexOf((node.item.getId()) + 1);
                Integer idOfNext = rangeOfViewId.get(indexNext);
                nextNode = historyOfView.get(idOfNext);
                nextNode.next = node.next; */