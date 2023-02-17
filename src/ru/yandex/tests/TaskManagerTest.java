package ru.yandex.tests;

import org.junit.jupiter.api.Test;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Status;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;
import ru.yandex.tmanager.HistoryManager;
import ru.yandex.tmanager.Managers;
import ru.yandex.tmanager.TaskManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

abstract class TaskManagerTest<T extends TaskManager> {

    // ======= ДЛЯ ======= TASK ===================
    @Test
    void shouldCreateTaskAndGetTaskById() throws IOException {
        TaskManager manager = Managers.getDefault();

        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        Task taskTestNull = new Task(null, null, 0, null,
                "01.01.2000--12:00", 3600);

        manager.makeNewTask(taskTest);
        manager.makeNewTask(taskTestNull);

        assertEquals(taskTest, manager.getTaskById(taskTest.getId()),
                "К сожалению, Task объект не создан, либо создан с ошибками");
        assertNull(manager.getTaskById(taskTestNull.getId()),
                "Ошибка! Создан task c пустыми полями");
        }

    @Test
    void shouldUpdateTask() throws IOException {
        TaskManager manager = Managers.getDefault();
        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        manager.makeNewTask(taskTest);
        Task taskTestUpdate = new Task("Test new name", "Test new description",
                taskTest.getId(), Status.IN_PROGRESS, "01.01.2000--12:00", 3600);
        manager.updateTask(taskTest.getId(), taskTestUpdate);
        assertEquals(taskTestUpdate, manager.getTaskById(taskTest.getId()),         // нормальное поведение
                "К сожалению, Task объект не обновлён, либо обновлён с ошибками");

        Task updTaskWithIdError = new Task("IdError name", "IdError description", 333,
                Status.NEW, "01.01.2000--12:00", 3600);
        manager.updateTask(taskTest.getId(), updTaskWithIdError);
        assertEquals(taskTestUpdate, manager.getTaskById(taskTest.getId()),             // некорректный ИД
                "Задаче присвоен некорректный ID");
    }

    @Test
    void shouldClearTask() throws IOException {
        TaskManager manager = Managers.getDefault();
        Task taskTest1 = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        manager.makeNewTask(taskTest1);
        Task taskTest2 = new Task("Test name", "Test description", 0,
                Status.NEW, "01.01.2000--12:00", 3600);
        manager.makeNewTask(taskTest2);
        manager.clearTask();
        assertNull(manager.getTaskById(taskTest1.getId()), "К сожалению, Task объект не удалён");
        assertNull(manager.getTaskById(taskTest2.getId()), "К сожалению, Task объект не удалён");
    }

    @Test
    void shouldGetListAllTasksFromTask() throws IOException {
        TaskManager manager = Managers.getDefault();
        Task taskTest1 = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        Task taskTest2 = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        manager.makeNewTask(taskTest1);
        manager.makeNewTask(taskTest2);
        List listTask1and2 = new ArrayList<>();
        listTask1and2.add(taskTest1);
        listTask1and2.add(taskTest2);
        List listOfTasks = manager.getListAllTasks();
        assertEquals(listTask1and2, listOfTasks, "Список Task объектов сформирован не корректно");
    }

    // ========================= ДЛЯ =========================== EPIC ============================
    @Test
    void shouldCreateEpicAndGetEpicById() throws IOException {
        TaskManager manager = Managers.getDefault();
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewEpic(epicTest);
        assertEquals(epicTest, manager.getEpicById(epicTest.getId()),
                "К сожалению, Epic объект не создан, либо создан с ошибками");
    }

    @Test
    void shouldUpdateEpic() throws IOException {
        TaskManager manager = Managers.getDefault();
        Epic epicTest = new Epic("Epic name", "Epic description", 0, Status.NEW);
        manager.makeNewEpic(epicTest);
        Subtask subtaskTest = new Subtask("Subtask name", "Subtask description",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        manager.makeNewSubtask(subtaskTest);

        Epic epicUpdate = new Epic("Epic new name", "Epic new description",
                epicTest.getId(), Status.IN_PROGRESS);
        manager.updateEpic(epicTest.getId(), epicUpdate);
        assertEquals(epicUpdate, manager.getEpicById(epicTest.getId()),
                "К сожалению, Epic объект не обновлён, либо обновлён с ошибками");
    }

    @Test
    void shouldGetListSubtasksOfEpic() throws IOException {
        TaskManager manager = Managers.getDefault();

        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewEpic(epicTest);
        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        manager.makeNewSubtask(subtaskTest1);
        manager.makeNewSubtask(subtaskTest2);

        List listSubtask1and2 = new ArrayList<>();
        listSubtask1and2.add(subtaskTest1);
        listSubtask1and2.add(subtaskTest2);

        assertEquals(listSubtask1and2, epicTest.getMySubtasks(),
                "К сожалению, метод getListSubtasksOfEpic работает не корректно");
    }

    @Test
    void shouldDellAllEpic() throws IOException {
        TaskManager manager = Managers.getDefault();
        //  создаём Epic
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewEpic(epicTest);
        // создаём подзадачи для эпика
        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        manager.makeNewSubtask(subtaskTest1);
        manager.makeNewSubtask(subtaskTest2);
        int idSubtask1 = subtaskTest1.getId();
        int idSubtask2 = subtaskTest2.getId();

        //Создаём эпик без подзадач
        Epic epicTest2 = new Epic("Epic name2", "Epic description2", 0,
               Status.NEW);
        manager.makeNewEpic(epicTest2);

        // удаляем все Эпики из хранилища
        manager.dellAllEpic();

        assertNull(manager.getEpicById(epicTest.getId()),"Epic c подзадачами не удалён");
        assertNull(manager.getEpicById(epicTest2.getId()),"Epic без подзадач не удалён");
        assertNull(manager.getSubTaskById(idSubtask1), "1я подзадача не удалена");
        assertNull(manager.getSubTaskById(idSubtask2), "2я подзадача не удалена");
        }

    // ========================= ДЛЯ ===================== SUBTASK ============================

    @Test
    void shouldCreateAndGetSubtaskById() throws IOException {
        TaskManager manager = Managers.getDefault();
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewEpic(epicTest);
        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        manager.makeNewSubtask(subtaskTest1);
        manager.makeNewSubtask(subtaskTest2);

        assertEquals(subtaskTest1, manager.getSubTaskById(subtaskTest1.getId()),
                "К сожалению, Subtask объект не создан, либо создан с ошибками");
        assertEquals(subtaskTest2, manager.getSubTaskById(subtaskTest2.getId()),
                "К сожалению, Subtask объект не создан, либо создан с ошибками");
    }

    @Test
    void shouldUpdateSubtask() throws IOException {
        TaskManager manager = Managers.getDefault();
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewEpic(epicTest);
        Subtask subtaskTest = new Subtask("Subtask name", "Subtask description",
                0, Status.NEW, epicTest.getId(),"01.01.2000--12:00", 3600);
        manager.makeNewSubtask(subtaskTest);

        Subtask updSubtaskTest = new Subtask("updSubtask name", "updSubtask description",
                subtaskTest.getId(), Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        manager.updateSubtask(subtaskTest.getId(),updSubtaskTest);

        assertEquals(updSubtaskTest, manager.getSubTaskById(subtaskTest.getId()),
                "К сожалению, Subtask объект не обновлён, либо обновлён с ошибками");
    }

    // ============ ДЛЯ ВСЕХ ======================================================

    @Test
    void shouldGetHistory() throws IOException {
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewTask(taskTest);
        manager.makeNewEpic(epicTest);
        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        manager.makeNewSubtask(subtaskTest1);
        manager.makeNewSubtask(subtaskTest2);

        List historyTest = new ArrayList<>();
        historyTest.add(manager.getTaskById(taskTest.getId()));
        historyTest.add(manager.getEpicById(epicTest.getId()));
        historyTest.add(manager.getSubTaskById(subtaskTest1.getId()));
        historyTest.add(manager.getSubTaskById(subtaskTest2.getId()));
        assertEquals(historyTest, manager.getHistory(), "История сформирована с ошибками");
    }

    @Test
    void shouldDellThemAll() throws IOException {
        TaskManager manager = Managers.getDefault();
        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        Subtask subtaskTest = new Subtask("Subtask name", "Subtask description",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        manager.makeNewTask(taskTest);
        manager.makeNewEpic(epicTest);
        manager.makeNewSubtask(subtaskTest);

        manager.dellThemAll();
        assertNull(manager.getTaskById(taskTest.getId()),"Task не удалён");
        assertNull(manager.getEpicById(epicTest.getId()),"Epic не удалён");
        assertNull(manager.getSubTaskById(subtaskTest.getId()), "Подзадача не удалена");
    }
}

