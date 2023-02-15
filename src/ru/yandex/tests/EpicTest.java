package ru.yandex.tests;
import org.junit.jupiter.api.*;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Status;
import ru.yandex.tasks.Subtask;
import ru.yandex.tmanager.Managers;
import ru.yandex.tmanager.TaskManager;
import java.io.IOException;

class EpicTest {
    TaskManager manager;
    Epic epicTest;

    @BeforeEach                      // Перед каждым тестом заводим новый Epic
    public void beforeEach() throws IOException {
        manager = Managers.getDefault();
        epicTest = new Epic("Epic name", "Epic description", 0,
                Status.NEW);
        manager.makeNewEpic(epicTest);
    }

    @AfterEach                       // Удаляем все Epic и Subtasks после каждого теста для чистоты эксперимента
    void afterEach() throws IOException {
        manager.dellThemAll();
    }

    @Test           // Пустой список подзадач
    public void shouldCalculateEpicStatusWithEmptyListOfSubtasks() throws IOException  {
        manager.getEpicById(epicTest.getId()).setMySubtask(null);
        Status newStatus = manager.getEpicById(epicTest.getId()).getStatus();
        Assertions.assertEquals(Status.NEW, newStatus, "Статус новой Epic-задачи без подзадач установлен " +
                "не корректно");
        }

    @Test           // Все подзадачи со статусом NEW
    public void shouldCalculateEpicStatusWithNewSubtasks() throws IOException  {
        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.NEW, epicTest.getId());
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.NEW, epicTest.getId());
        manager.makeNewSubtask(subtaskTest1);
        manager.makeNewSubtask(subtaskTest2);
        manager.getEpicById(epicTest.getId()).setMySubtask(subtaskTest1);
        manager.getEpicById(epicTest.getId()).setMySubtask(subtaskTest2);
        Status newStatus = manager.getEpicById(epicTest.getId()).getStatus();
        Assertions.assertEquals(Status.NEW, newStatus, "Статус Epic-задачи c новыми подзадачами рассчитан " +
                "не корректно");
    }

    @Test           // Все подзадачи со статусом DONE
    public void shouldCalculateEpicStatusWithDoneSubtasks() throws IOException  {
        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.DONE, epicTest.getId());
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.DONE, epicTest.getId());
        manager.makeNewSubtask(subtaskTest1);
        manager.makeNewSubtask(subtaskTest2);
        Status newStatus = manager.getEpicById(epicTest.getId()).getStatus();
        Assertions.assertEquals(Status.DONE, newStatus, "Статус Epic-задачи c выполненными подзадачами " +
                "рассчитан не корректно");
    }

    @Test           // Подзадачи со статусами NEW и DONE
    public void shouldCalculateEpicStatusWithNewAndDoneSubtasks() throws IOException  {
        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.NEW, epicTest.getId());
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.DONE, epicTest.getId());
        manager.makeNewSubtask(subtaskTest1);
        manager.makeNewSubtask(subtaskTest2);
        manager.getEpicById(epicTest.getId()).setMySubtask(subtaskTest1);
        manager.getEpicById(epicTest.getId()).setMySubtask(subtaskTest2);
        Status newStatus = manager.getEpicById(epicTest.getId()).getStatus();
        Assertions.assertEquals(Status.IN_PROGRESS, newStatus, "Статус Epic-задачи c подзадачами NEW & DONE " +
                "рассчитан не корректно");
    }

    @Test           // Подзадачи со статусами NEW и DONE
    public void shouldCalculateEpicStatusWithInProgressSubtasks() throws IOException  {
        Subtask subtaskTest1 = new Subtask("Subtask name1", "Subtask description1",
                0, Status.IN_PROGRESS, epicTest.getId());
        Subtask subtaskTest2 = new Subtask("Subtask name2", "Subtask description2",
                0, Status.IN_PROGRESS, epicTest.getId());
        manager.makeNewSubtask(subtaskTest1);
        manager.makeNewSubtask(subtaskTest2);
        manager.getEpicById(epicTest.getId()).setMySubtask(subtaskTest1);
        manager.getEpicById(epicTest.getId()).setMySubtask(subtaskTest2);
        Status newStatus = manager.getEpicById(epicTest.getId()).getStatus();
        Assertions.assertEquals(Status.IN_PROGRESS, newStatus, "Статус Epic-задачи c подзадачами IN_Progress " +
                "рассчитан не корректно");
    }
}
