package ru.yandex.tmanager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}



/*
Добавьте в служебный класс Managers статический метод HistoryManager getDefaultHistory().
Он должен возвращать объект InMemoryHistoryManager — историю просмотров.

Проверьте, что теперь InMemoryTaskManager обращается к менеджеру истории через интерфейс HistoryManager
и использует реализацию, которую возвращает метод getDefaultHistory().


 */
