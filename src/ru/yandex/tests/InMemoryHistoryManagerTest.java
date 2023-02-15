package ru.yandex.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tmanager.HistoryManager;
import ru.yandex.tmanager.Managers;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    /*
Для HistoryManager — тесты для всех методов интерфейса. Граничные условия:
 a. Пустая история задач.
 b. Дублирование.
 с. Удаление из истории: начало, середина, конец.
 */

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void linkLast() {
    }

    @Test
    void removeNode() {
    }

    @Test
    void getHistory() {
    }

    @Test
    void add() {
    }

    @Test
    void remove() {
    }
}