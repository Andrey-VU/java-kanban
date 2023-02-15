import ru.yandex.exceptions.ManagerSaveException;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;
import ru.yandex.tasks.Status;
import ru.yandex.tmanager.Managers;
import ru.yandex.tmanager.TaskManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ManagerSaveException {
        TaskManager manager = Managers.getDefault();
        //HistoryManager historyManager = Managers.getDefaultHistory();
        System.out.println("Поехали!");

        //Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей
        Task taskTest1 = new Task("Пробежка", "не менее 30 минут и не менее 5 км",
                0, Status.NEW);
        Task taskTest2 = new Task("Гитарный конкурс", "Отвезти сына на гитарный конкурс к 15.00",
                0, Status.NEW);
        Epic epicTest1 = new Epic("Отправить на ревью ТЗ 3",     // Эпик c одной подзадачей
                "До 24.00 11.12 отправить ТЗ 3 на ревью", 0, Status.NEW);
        Epic epicTest2 = new Epic("Приготовить курицу",     // Эпик с двумя подзадачами
                "Куринные ноги, замариновать, запечь в духовке к 14.30 10.12", 0,
                Status.NEW);

        //  Отправляем новые задачи и эпики в трекер
        manager.makeNewTask(taskTest1);
        manager.makeNewTask(taskTest2);
        manager.makeNewEpic(epicTest1);
        manager.makeNewEpic(epicTest2);
        //  ПОДЗДАЧИ ДЛЯ ПЕРВОГО ЭПИКА
        Subtask subtaskTest1 = new Subtask("Получить рабочую версию программы",
                "Написать код для тестирования. Выполнить отладку", 0, Status.NEW,
                epicTest1.getId());
        // ПОДЗДААЧИ ДЛЯ ВТОРОГО ЭПИКА
        Subtask subtaskTest2 = new Subtask("Предобработка ног",
                "Перфорировать, добавить перец, паприку, чеснок, соль", 0, Status.NEW,
                epicTest2.getId());
         Subtask subtaskTest3 = new Subtask("Запекание",
                "40 минут на 200 градусов в духовке", 0, Status.NEW,
                epicTest2.getId());

        // ОТПРАВЛЯЕМ ПОДЗАДАЧИ В ТРЕКЕР
        manager.makeNewSubtask(subtaskTest1);
        manager.makeNewSubtask(subtaskTest2);
        manager.makeNewSubtask(subtaskTest3);
        // Распечать списки эпиков, задач и подзадач, через System.out.println(..)
      //  System.out.println(manager.getListAllTasks());

        //manager.printHistory();  // печатаем история просмотров

        // Проверяем id и статусы
      //  printIdAndStatus(manager, taskTest2, epicTest1, subtaskTest1, epicTest2,
       //         subtaskTest2, subtaskTest3, taskTest1);

        // Меняем статусы созданных объектов, распечатываем.
        // Простые задачи
        Task updateForTaskTest1 = new Task("Пробежка", "не менее 30 минут и не менее 5 км",
                taskTest1.getId(), Status.DONE);
        Task updateForTaskTest2 = new Task("Гитарный конкурс",
                "Отвезти сына на гитарный конкурс к 15.00",
                taskTest2.getId(), Status.DONE);
        manager.updateTask( taskTest1.getId(), updateForTaskTest1);
        manager.updateTask( taskTest2.getId(), updateForTaskTest2);

        // Подзадачи Эпиков
        Subtask updateForSubtaskTest1 = new Subtask("Получить рабочую версию программы",
                "Написать код для тестирования. Выполнить отладку", subtaskTest1.getId(),
                Status.IN_PROGRESS, epicTest1.getId());
        Subtask updateForSubtaskTest2 = new Subtask("Предобработка ног",
                "Перфорировать, добавить перец, паприку, чеснок, соль", subtaskTest2.getId(),
                Status.DONE, epicTest2.getId());
        Subtask updateForSubtaskTest3 = new Subtask("Запекание",
                "40 минут на 200 градусов в духовке", subtaskTest3.getId(),
                Status.DONE, epicTest2.getId());

        manager.updateSubtask(subtaskTest1.getId(), updateForSubtaskTest1);
        manager.updateSubtask(subtaskTest2.getId(), updateForSubtaskTest2);
        manager.updateSubtask(subtaskTest3.getId(), updateForSubtaskTest3);

        // Проверяем, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        //System.out.println(" =====================  ВЫХЛОП ПОСЛЕ ИЗМЕНЕНИЯ СТАТУСОВ ==============================");
       // printIdAndStatus(manager, taskTest2, epicTest1, subtaskTest1, epicTest2,
         //       subtaskTest2, subtaskTest3, taskTest1);

        // Удалить одну из задач и один из эпиков.
        manager.dellTaskById(taskTest1.getId());  // Удаляем ru.yandex.tasks.Task задачу
        manager.dellTaskById(epicTest2.getId());  // Удаляем ru.yandex.tasks.Epic 2
        //System.out.println("После удаления 1ой простой задачи и одного Эпика:");
        //System.out.println(manager.getListAllTasks());  // проверяем удалось ли удалить ru.yandex.tasks.Task задачу

        // Удалить все оставшиеся эпики и их сабтески
        manager.dellAllEpic();
//        System.out.println("После удаления оставшихся Эпиков:");
//        System.out.println(manager.getListAllTasks());

       // manager.printHistory();  // печатаем история просмотров
   }


//    public static void printIdAndStatus(TaskManager manager, Task taskTest2, Epic epicTest1, Subtask subtaskTest1,
//                                        Epic epicTest2, Subtask subtaskTest2, Subtask subtaskTest3, Task taskTest1) {
//        System.out.println(" ========= ПРОСТЫЕ ЗАДАЧИ ======================");
//        System.out.println("статус taskTest1 = " + manager.getTaskById(taskTest1.getId()).getStatus());
//        System.out.println("id taskTest1 = " + manager.getTaskById(taskTest1.getId()).getId());
//        System.out.println("статус taskTest2 = " + manager.getTaskById(taskTest2.getId()).getStatus());
//        System.out.println("id taskTest2 = " + manager.getTaskById(taskTest2.getId()).getId());
//        System.out.println(" =========  ЭПИК № 1 И ЕГО ПОДЗАДАЧи ======================");
//        System.out.println("id epicTest1 = " + manager.getEpicById(epicTest1.getId()).getId());
//        System.out.println("статус epicTest1 = " + manager.getEpicById(epicTest1.getId()).getStatus());
//        System.out.println("id subtaskTest1 = " + manager.getSubTaskById(subtaskTest1.getId()).getId());
//        System.out.println("статус subtaskTest1 = " + manager.getSubTaskById(subtaskTest1.getId()).getStatus());
//        System.out.println(" =========  ЭПИК № 2 И ЕГО ПОДЗАДАЧи ======================");
//        System.out.println("id epicTest2 = " + manager.getEpicById(epicTest2.getId()).getId());
//        System.out.println("статус epicTest2 = " + manager.getEpicById(epicTest2.getId()).getStatus());
//        System.out.println(" ПОДЗАДАЧА 1 ВТОРОГО ЭПИКА ======================");
//        System.out.println("id subtaskTest2 = " + manager.getSubTaskById(subtaskTest2.getId()).getId());
//        System.out.println("статус subtaskTest2 = " + manager.getSubTaskById(subtaskTest2.getId()).getStatus());
//        System.out.println(" ПОДЗАДАЧА 2 ВТОРОГО ЭПИКА ======================");
//        System.out.println("id subtaskTest3 = " + manager.getSubTaskById(subtaskTest3.getId()).getId());
//        System.out.println("статус subtaskTest3 = " + manager.getSubTaskById(subtaskTest3.getId()).getStatus());
//    }
}

