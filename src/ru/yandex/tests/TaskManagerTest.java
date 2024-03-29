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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
   T manager;

    // ======= ДЛЯ ======= TASK ===================
    @Test
    void shouldCreateTaskAndGetTaskById() throws IOException {

        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        Task taskTestNull = new Task(null, null, 0, null,
                "01.01.2001--12:00", 3600);

        manager.makeNewTask(taskTest);
       // manager.makeNewTask(taskTestNull);

        assertEquals(taskTest, manager.getTaskById(taskTest.getId()),
                "К сожалению, Task объект не создан, либо создан с ошибками");
        assertNull(manager.getTaskById(taskTestNull.getId()),
                "Ошибка! Создан task c пустыми полями");
        }

    @Test
    void shouldUpdateTask() throws IOException {
        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2020--12:00", 3600);
        manager.makeNewTask(taskTest);
        Task taskTestUpdate = new Task("Test new name", "Test new description",
                taskTest.getId(), Status.IN_PROGRESS, "01.01.2020--12:00", 3600);
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
        Task taskTest1 = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        manager.makeNewTask(taskTest1);
        Task taskTest2 = new Task("Test name", "Test description", 0,
                Status.NEW, "01.03.2000--12:00", 3600);
        manager.makeNewTask(taskTest2);
        manager.clearTask();
        assertNull(manager.getTaskById(taskTest1.getId()), "К сожалению, Task объект не удалён");
        assertNull(manager.getTaskById(taskTest2.getId()), "К сожалению, Task объект не удалён");
    }

    @Test
    void shouldGetListAllTasksFromTask() throws IOException {
        Task taskTest1 = new Task("Test name", "Test description", 0, Status.NEW,
                "01.02.2000--12:00", 3600);
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
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewEpic(epicTest);
        assertEquals(epicTest, manager.getEpicById(epicTest.getId()),
                "К сожалению, Epic объект не создан, либо создан с ошибками");
    }

    @Test
    void shouldUpdateEpic() throws IOException {
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
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewEpic(epicTest);
        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.NEW, epicTest.getId(), "01.03.2000--12:00", 3600);
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

        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewEpic(epicTest);

        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.NEW, epicTest.getId(), "01.03.2000--12:00", 3600);
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
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewEpic(epicTest);
        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.NEW, epicTest.getId(), "01.01.2000--12:00", 3600);
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.NEW, epicTest.getId(), "01.04.2000--12:00", 3600);
        manager.makeNewSubtask(subtaskTest1);
        manager.makeNewSubtask(subtaskTest2);

        assertEquals(subtaskTest1, manager.getSubTaskById(subtaskTest1.getId()),
                "К сожалению, Subtask объект не создан, либо создан с ошибками");
        assertEquals(subtaskTest2, manager.getSubTaskById(subtaskTest2.getId()),
                "К сожалению, Subtask объект не создан, либо создан с ошибками");
    }

    @Test
    void shouldUpdateSubtask() throws IOException {
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
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewTask(taskTest);
        manager.makeNewEpic(epicTest);
        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.NEW, epicTest.getId(), "01.02.2000--12:00", 3600);
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.NEW, epicTest.getId(), "01.03.2000--12:00", 3600);
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
        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        Subtask subtaskTest = new Subtask("Subtask name", "Subtask description",
                0, Status.NEW, epicTest.getId(), "01.03.2000--12:00", 3600);
        manager.makeNewTask(taskTest);
        manager.makeNewEpic(epicTest);
        manager.makeNewSubtask(subtaskTest);

        manager.dellThemAll();
        assertNull(manager.getTaskById(taskTest.getId()),"Task не удалён");
        assertNull(manager.getEpicById(epicTest.getId()),"Epic не удалён");
        assertNull(manager.getSubTaskById(subtaskTest.getId()), "Подзадача не удалена");
    }

    @Test
    void shouldGetStartTime() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy--HH:mm");
        TaskManager manager = Managers.getInMemoryTaskManager();
        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        Subtask subtaskTest1 = new Subtask("Subtask name", "Subtask description",
                0, Status.NEW, epicTest.getId(), "01.03.2000--12:00", 3600);

        manager.makeNewTask(taskTest);
        manager.makeNewEpic(epicTest);
        manager.makeNewSubtask(subtaskTest1);

        Task task = manager.getTaskById(taskTest.getId());
        Subtask subtask = manager.getSubTaskById(subtaskTest1.getId());
        Epic epic = manager.getEpicById(epicTest.getId());
        LocalDateTime testTimeStart = LocalDateTime.parse("01.01.2000--12:00", formatter);

        if (task != null) {
            assertEquals(testTimeStart, task.getStartTime(),
                    "Время старта объекта Task не получено");
        }
        if (subtask != null) {
            assertEquals(testTimeStart, subtask.getStartTime(),
                    "Время старта объекта SubTask не получено");
        }
        if (epic != null && epic.getStartTime() != null){
            assertEquals(testTimeStart, epic.getStartTime(),
                    "Время старта объекта Epic не получено");
        }
    }

    @Test
    void shouldGetDuration() throws IOException {
        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        Subtask subtaskTest1 = new Subtask("Subtask name", "Subtask description",
                0, Status.NEW, epicTest.getId(), "01.04.2000--12:00", 3600);

        manager.makeNewTask(taskTest);
        manager.makeNewEpic(epicTest);
        manager.makeNewSubtask(subtaskTest1);

        Task task = manager.getTaskById(taskTest.getId());
        Subtask subtask = manager.getSubTaskById(subtaskTest1.getId());
        Epic epic = manager.getEpicById(epicTest.getId());
        long duration = 3600;

        if (task != null) {
            assertEquals(duration, task.getDuration().toMinutes(),
                    "Длительность объекта Task не получена");
        }
        if (subtask != null) {
            assertEquals(duration, subtask.getDuration().toMinutes(),
                    "Длительность объекта SubTask не получена");
        }
        if (epic != null && epic.getDuration() != null){
            assertEquals(duration, epic.getDuration().toMinutes(),
                    "Длительность объекта Epic не рассчитана, либо не получена");
        }
    }

    @Test
    void shouldCalculateDataStartAndDurationInEpic() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy--HH:mm");
        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        Epic epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        Subtask subtaskTest1 = new Subtask("Subtask name", "Subtask description",
                0, Status.NEW, epicTest.getId(), "01.02.2000--12:00", 3600);
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.NEW, epicTest.getId(), "01.03.2000--10:00", 3600);

        manager.makeNewTask(taskTest);
        manager.makeNewEpic(epicTest);
        manager.makeNewSubtask(subtaskTest1);
        manager.makeNewSubtask(subtaskTest2);

        int idOfSub1 = subtaskTest1.getId();
        int idOfSub2 = subtaskTest2.getId();
        int idOfEpic = epicTest.getId();

        LocalDateTime first = LocalDateTime.parse("01.01.2000--10:00", formatter);
        Epic epic = manager.getEpicById(idOfEpic);
        Subtask sub1 = manager.getSubTaskById(idOfSub1);
        Subtask sub2 = manager.getSubTaskById(idOfSub2);

        if (epic != null && epic.getStartTime() != null) {
            assertEquals(true, first.isEqual(epic.getStartTime()),
                    "Время старта Эпика рассчитано не верно");
        }

        if (sub1 != null && sub2 != null && sub2.getDuration() != null && sub1.getDuration() != null) {
            long duration = sub2.getDuration().toMinutes() + sub1.getDuration().toMinutes();
        if (epic != null && epic.getDuration() != null) {
            assertEquals(duration, epic.getDuration().toMinutes(), "Длительность Эпика рассчитано не верно");
        }}
  }
}

