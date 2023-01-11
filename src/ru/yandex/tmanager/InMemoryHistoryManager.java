package ru.yandex.tmanager;
import ru.yandex.tasks.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager{
    Map<Integer, Task> historyOfView = new HashMap<>();  // для хранения истории просмотров
    List<Task> rangeOfView = new ArrayList<Task>();      // для хранения порядка вызовов удобно использовать список.
    private Node<Task> head;      // Указатель на первый элемент списка. Он же first
    private Node<Task> tail;      // Указатель на последний элемент списка. Он же last
    private int size = 0;         // Размер хранилища

    @Override
    public void add(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> oldHead = head;
        final Node<Task> newNode = new Node<>(oldHead, task, oldTail);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.prev = newNode;
        size++;
        historyOfView.put(task.getId(), task);
        rangeOfView.add(task);
    }

    @Override
    public void remove(int id) {
        historyOfView.remove(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) rangeOfView;
    }
    /*  Программа должна запоминать порядок вызовов метода add, ведь именно в этом порядке просмотры будут выстраиваться
    в истории. Внутри класса нужно реализовать методы linkLast, getTasks и removeNode
    Если какая-либо задача просматривалась несколько раз, в истории должен отобразиться только последний просмотр.
    Предыдущий просмотр должен быть удалён сразу же после появления нового     */

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
void remove(int id) для удаления задачи из просмотра. И реализовать его в классе InMemoryHistoryManager.
Добавьте его вызов при удалении задач, чтобы они также удалялись из истории просмотров.
 */


