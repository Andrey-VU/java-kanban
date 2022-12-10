package TaskTracker;
public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        System.out.println("Поехали!");

        Epic epicTest1 = new Epic("Отправить на ревью ТЗ 3",     // Эпик c одной подзадачей
                "До 24.00 11.12 отправить ТЗ 3 на ревью", 0,
                "NEW");

        Epic epicTest2 = new Epic("Приготовить курицу",     // Эпик с двумя подзадачами
                "Куринные ноги, замариновать, запечь в духовке к 14.30 10.12", 0,
                "NEW");

        //  ПОДЗДАЧИ ДЛЯ ПЕРВОГО ЭПИКА
        /* Subtask subtaskTest3 = new Subtask("Получить рабочую версию программы",
                "Написать код для тестирования. Выполнить отладку", 0, "NEW",
                epicTest1.getMyEpicID());
        */

        // ПОДЗДААЧИ ДЛЯ ВТОРОГО ЭПИКА
        /* Subtask subtaskTest1 = new Subtask("Предобработка ног",
                "Перфорировать, добавить перец, паприку, чеснок, соль", 0, "NEW",
                epicTest2.getMyEpicID());
         Subtask subtaskTest2 = new Subtask("Запекание",
                "40 минут на 200 градусов в духовке", 0, "NEW",
                epicTest2.getMyEpicID()); */

        manager.makeNewEpic(epicTest1);
        manager.makeNewEpic(epicTest2);
        // manager.makeNewSubtask(subtaskTest1);
        // manager.makeNewSubtask(subtaskTest2);

}
}

/*
Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)
Измените статусы созданных объектов, распечатайте. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
И, наконец, попробуйте удалить одну из задач и один из эпиков.
 */