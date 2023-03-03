package ru.yandex.tests;

import com.sun.tools.javac.Main;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.http.KVServer;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Status;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;
import ru.yandex.tmanager.Managers;
import ru.yandex.tmanager.TaskManager;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest {
    KVServer kvServer;
    TaskManager inMemoryManager;

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        manager = Managers.getDefault();
        manager.setKey("Artem2");
    }

    @AfterEach
    public void afterEach() throws IOException {
        kvServer.stop();
        if (inMemoryManager != null) {
            inMemoryManager.dellThemAll();
            if (inMemoryManager.getHistory() != null) {
                inMemoryManager.getHistory().clear();
            }
            inMemoryManager.getPrioritizedTasks().clear();
        }
        manager.dellThemAll();
        manager.getHistory();
        manager.getPrioritizedTasks().clear();
    }

    @Test
    public  void shouldSaveTaskToServer() throws IOException {
        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        manager.makeNewTask(taskTest);
        int codeFromKVServer = manager.getKvTaskClient().getCodeForTestFunctionSaveToKVServer();
        assertEquals(200, codeFromKVServer, "Сохранить Task на сервер не удалось. " +
                "Вернулся код " + codeFromKVServer);
    }

    @Test
    public  void shouldLoadTaskFromServer() throws IOException {
        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        inMemoryManager = Managers.getInMemoryTaskManager();
        inMemoryManager.makeNewTask(taskTest);
        Task taskFromMemory = inMemoryManager.getTaskById(taskTest.getId());
        manager.makeNewTask(taskTest);
        Task taskFromServer = manager.getTaskById(taskTest.getId());
        assertEquals(taskFromMemory, taskFromServer,
                "Task с сервера не вернулся или вернулся какой-то не такой");
    }

    @Test
    public  void shouldSaveSubtaskToServer() throws IOException {
        Epic epicForTest = new Epic("Epic for test",
                "Make newEpic and use It", 0, Status.NEW);
        inMemoryManager = Managers.getInMemoryTaskManager();
        inMemoryManager.makeNewEpic(epicForTest);

        Subtask subtaskForServer = new Subtask("Subtask to Server",
                "Make newSubtask and push It to server",
                0, Status.NEW, epicForTest.getId(), "01.01.1917--12:00", 0);
        manager.makeNewSubtask(subtaskForServer);


        int codeFromKVServer = manager.getKvTaskClient().getCodeForTestFunctionSaveToKVServer();
        assertEquals(200, codeFromKVServer, "Сохранить Subtask на сервер не удалось. " +
                "Вернулся код " + codeFromKVServer);
    }

    @Test
    public  void shouldLoadSubtaskFromServer() throws IOException {
        Epic epicForTest = new Epic("Epic to Json",
                "Make newEpic for test", 0, Status.NEW);
        inMemoryManager = Managers.getInMemoryTaskManager();
        inMemoryManager.makeNewEpic(epicForTest);

        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
                "01.01.2000--12:00", 3600);
        manager.makeNewTask(taskTest);
        Subtask subtaskForServer = new Subtask("Subtask to Json", "Make newSubtask and push " +
                "It to server",
                0, Status.NEW, epicForTest.getId(), "01.01.1917--12:00", 0);
        manager.makeNewSubtask(subtaskForServer);
        inMemoryManager.makeNewSubtask(subtaskForServer);

        Subtask subtaskFromServer = manager.getSubTaskById(subtaskForServer.getId());
        Subtask subtaskFromMemory = inMemoryManager.getSubTaskById(subtaskForServer.getId());

        assertEquals(subtaskFromMemory, subtaskFromServer,
                "Subtask с сервера не вернулся или вернулся какой-то не такой");

    }

    @Test
    public  void shouldSaveEpicToServer() throws IOException {
        Epic epicForTest = new Epic("Epic for server",
                "Make newEpic and push It to server", 0, Status.NEW);
        manager.makeNewEpic(epicForTest);
        int codeFromKVServer = manager.getKvTaskClient().getCodeForTestFunctionSaveToKVServer();
        assertEquals(200, codeFromKVServer, "Сохранить Epic на сервер не удалось. " +
                "Вернулся код " + codeFromKVServer);
    }

    @Test
    public  void shouldLoadEpicFromServer() throws IOException {
        Epic epicForTest = new Epic("Epic for server",
                "Make newEpic and push It to server", 0, Status.NEW);
        manager.makeNewEpic(epicForTest);
        inMemoryManager = Managers.getInMemoryTaskManager();
        inMemoryManager.makeNewEpic(epicForTest);
        Epic epicFromServer = manager.getEpicById(epicForTest.getId());
        Epic epicFromMemory = inMemoryManager.getEpicById(epicForTest.getId());
        assertEquals(epicFromMemory, epicFromServer,
                "Epic с сервера не вернулся или вернулся какой-то не такой");
    }
}
