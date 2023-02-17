package ru.yandex.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tasks.Status;
import ru.yandex.tasks.Task;
import ru.yandex.tmanager.HistoryManager;
import ru.yandex.tmanager.Managers;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @AfterEach
    void tearDown() {
        if (historyManager.getHistory() != null) {
            historyManager.getHistory().clear();
        }
    }

    @Test
    void isHistoryManagerEmpty() {
        assertNull(historyManager.getHistory(),"Ошибка! " +
                "Новый менеджер истории не пустой");
    }

    @Test
    void shouldAddLastItemOneTime() {
        Task historyTask = new Task("name HistoryTask","description HistoryTask",1, Status.NEW,
                "01.01.2000--12:00", 3600);
        historyManager.linkLast(historyTask);
        assertEquals(true,historyManager.getHistory().contains(historyTask),
                "Task не добавлен в историю");
        historyManager.linkLast(historyTask);
        assertEquals(1,historyManager.getHistory().size(),"одна и та же задача добавлена повторно!!!");
    }

    @Test
    void shouldGetHistory() {
        Task historyTask = new Task("name HistoryTask","description HistoryTask",1, Status.NEW,
                "01.01.2000--12:00", 3600);
        historyManager.linkLast(historyTask);
        assertEquals(true,historyManager.getHistory().contains(historyTask),
                "История не сформирована, либо содержит ошибки");
    }

    @Test
    void add() {
    }

    @Test
    void shouldRemove() {
        Task historyTask10 = new Task("name HistoryTask10",
                "description HistoryTask1",10, Status.NEW, "01.01.2000--12:00", 3600);
        historyManager.linkLast(historyTask10);
        historyManager.remove(historyTask10.getId());
        assertNull(historyManager.getHistory(), "История не очищена");

        Task historyTask1 = new Task("name HistoryTask1",
                "description HistoryTask1",1, Status.NEW, "01.01.2000--12:00", 3600);
        Task historyTask2 = new Task("name HistoryTask2",
                "description HistoryTask2",2, Status.NEW, "01.01.2000--12:00", 3600);
        Task historyTask3 = new Task("name HistoryTask3",
                "description HistoryTask3",3, Status.NEW, "01.01.2000--12:00", 3600);

        historyManager.linkLast(historyTask1);
        historyManager.linkLast(historyTask2);
        historyManager.linkLast(historyTask3);

        historyManager.remove(historyTask2.getId());
        assertEquals(false,historyManager.getHistory().contains(historyTask2),
                "Удаление элемента из середины не произошло");
        historyManager.remove(historyTask1.getId());
        assertEquals(false,historyManager.getHistory().contains(historyTask1),
                "Удаление элемента из начала не произошло");

        historyManager.linkLast(historyTask1);
        historyManager.linkLast(historyTask2);
        historyManager.linkLast(historyTask3);

        historyManager.remove(historyTask3.getId());
        assertEquals(false,historyManager.getHistory().contains(historyTask3),
                "Удаление элемента из конца не произошло");


    }

}