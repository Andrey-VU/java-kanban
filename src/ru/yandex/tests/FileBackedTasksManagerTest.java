package ru.yandex.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Status;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;
import ru.yandex.tmanager.FileBackedTasksManager;
import ru.yandex.tmanager.Managers;
import ru.yandex.tmanager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FileBackedTasksManagerTest extends TaskManagerTest {
    TaskManager inMemoryManager;
    TaskManager recoveredFromFile2;
    Task testTaskZ;
    Epic testEpicZ;
    Subtask testSubtaskZ;
    List historyForTest;

    @BeforeEach
    public void beforeEach() throws IOException {
        manager = Managers.getFileBackedManager();              // инициализация для тестов абстрактного класса
        inMemoryManager = Managers.getInMemoryTaskManager();
        recoveredFromFile2 = FileBackedTasksManager.loadFromFile("storageTestIn.csv",
                "storageTestOut1.csv");

        testTaskZ = new Task("Test_Task_name","Test_Task_description", 0, Status.NEW,
                "01.01.2004--12:00", 3600);
        testEpicZ = new Epic("Test_Epic_name","Test_Epic_description",0,Status.NEW);

        inMemoryManager.makeNewTask(testTaskZ);
        inMemoryManager.makeNewEpic(testEpicZ);

        testSubtaskZ = new Subtask("Test_Subtask_name","Test_Subtask_description",
                0, Status.NEW, testEpicZ.getId(), "01.01.2000--12:00", 3600);
        inMemoryManager.makeNewSubtask(testSubtaskZ);

        historyForTest = new ArrayList<>();
        historyForTest.add(inMemoryManager.getTaskById(1));
        historyForTest.add(inMemoryManager.getEpicById(2));
        historyForTest.add(inMemoryManager.getSubTaskById(3));
    }

    @AfterEach
    public void afterEach() throws IOException {
        manager.dellThemAll();
        inMemoryManager.dellThemAll();
        recoveredFromFile2.dellThemAll();
        if (recoveredFromFile2.getHistory() != null) {
            recoveredFromFile2.getHistory().clear();
            recoveredFromFile2.getPrioritizedTasks().clear();
        }
        if (recoveredFromFile2.getPrioritizedTasks() !=null) {
            recoveredFromFile2.getPrioritizedTasks().clear();
        }
    }

        @Test
    public void shouldLoadFromFile() throws IOException {
        Task taskFromFile = recoveredFromFile2.getTaskById(1);
        Epic epicFromFile = recoveredFromFile2.getEpicById(2);
        Subtask subtaskFromFile = recoveredFromFile2.getSubTaskById(3);
        List newHistory = new ArrayList<>();
        if (recoveredFromFile2.getHistory() != null) {
            newHistory = recoveredFromFile2.getHistory();
        }
            assertEquals(inMemoryManager.getTaskById(1), taskFromFile,
                    "Task из файла загружен не корректно");
            assertEquals(inMemoryManager.getEpicById(2), epicFromFile,
                    "Epic из файла загружен не корректно");
            assertEquals(inMemoryManager.getSubTaskById(3), subtaskFromFile,
                    "Subtask из файла загружен не корректно");
            assertEquals(historyForTest, newHistory,
                    "История из файла загружена не корректно");
        }

    @Test
    public void shouldSaveToFile() throws IOException {
        TaskManager recoveredFromFile3
                = FileBackedTasksManager.loadFromFile("storageTestOut1.csv", "storageTestOut2.csv");

        Task taskFromFile2 = recoveredFromFile3.getTaskById(1);
        Epic epicFromFile2 = recoveredFromFile3.getEpicById(2);
        Subtask subtaskFromFile2 = recoveredFromFile3.getSubTaskById(3);

        //Task testTask =

        assertEquals(inMemoryManager.getTaskById(1), taskFromFile2,
                "Сохранение задач в файл некорректно");
        assertEquals(inMemoryManager.getEpicById(2), epicFromFile2,
                "Epic из файла загружен не корректно");
        assertEquals(inMemoryManager.getSubTaskById(3), subtaskFromFile2,
                "Subtask из файла загружен не корректно");
    }

    @Test
    public void loadWhenEpicWithoutSubtask() throws IOException {
        // recoveredFromFile2 = FileBackedTasksManager.loadFromFile("storageTestIn.csv",
        //                "storageTestOut1.csv");

        TaskManager recoveredFromFileForEpicWithoutSub
                = FileBackedTasksManager.loadFromFile("storage_In_TestEpicWithoutSubtask.csv",
                "storage_Out_TestEpicWithoutSubtask.csv");

        TaskManager newManager = Managers.getInMemoryTaskManager();


        Epic epicWithoutSb = new Epic("Without sub name", "Without sub discr",
                0, Status.NEW);
        newManager.makeNewEpic(epicWithoutSb);
        Epic expectedEpic = newManager.getEpicById(epicWithoutSb.getId());

        recoveredFromFile2 = FileBackedTasksManager.loadFromFile("storage_In_TestEpicWithoutSubtask.csv",
                "storage_Out_TestEpicWithoutSubtask.csv");
        assertEquals(expectedEpic, recoveredFromFile2.getEpicById(1),
                "Эпик без подзадач не создан или создан с ошибками");
        assertNull(recoveredFromFile2.getListSubtasksOfEpic(inMemoryManager.getEpicById(1)),
                "создание Эпика без подзадач прошло с ошибками");
    }

    @Test
    public void loadWhenFileWithoutHistory() throws IOException {
        FileBackedTasksManager newRecoveredFromFile =
                FileBackedTasksManager.loadFromFile("storage_In_WithoutHistory.csv",
                        "storage_Out_WithoutHistory.csv");

        inMemoryManager.getHistory().clear();

        Task taskFromMemory = inMemoryManager.getTaskById(1);
        Epic epicFromMemory = inMemoryManager.getEpicById(2);
        Subtask subtaskFromMemory = inMemoryManager.getSubTaskById(3);

        Task taskFromFile = newRecoveredFromFile.getTaskById(1);
        Epic epicFromFile = newRecoveredFromFile.getEpicById(2);
        Subtask subtaskFromFile = newRecoveredFromFile.getSubTaskById(3);

        assertEquals(taskFromMemory, taskFromFile,
              "Task без истории не создан или создан с ошибками");
        assertEquals(epicFromMemory, epicFromFile, "Epic без истории не создан или создан с ошибками");
        assertEquals(subtaskFromMemory, subtaskFromFile, "SubTask без истории не создан или создан с ошибками");
    }
    @Test
    public void shouldMakePrioritizedList() throws IOException {
        String[] newTmpTask = {"4","TASK","Гитарный конкурс в Питере","NEW","Купить билеты",
                "01.01.1999--12:00","3600"};
        Task tmpTask = new Task(newTmpTask);
        recoveredFromFile2.makeNewTask(tmpTask);
        List<Task> tmpPriority = new ArrayList<>();
        tmpPriority.add(recoveredFromFile2.getTaskById(4));
        tmpPriority.add(recoveredFromFile2.getSubTaskById(3));
        tmpPriority.add(recoveredFromFile2.getTaskById(1));

        List<Task> priorityTest = recoveredFromFile2.getPrioritizedTasks();
        assertEquals(tmpPriority, priorityTest,
                "Задачи не удалось ранжировать по времени старта");
    }

    @Test
    public void shouldMakePrioritizedListWithoutData () throws IOException {

        Task tmpTask4 = new Task("Гитарный конкурс в Питере","Купить билеты",4,Status.NEW);
        Task tmpTask5 = new Task("Test_priority","Descr 5",5,Status.NEW);
        recoveredFromFile2.makeNewTask(tmpTask4);
        recoveredFromFile2.makeNewTask(tmpTask5);
        List<Task> tmpPrioroty = new ArrayList<>();
        tmpPrioroty.add(recoveredFromFile2.getSubTaskById(3));
        tmpPrioroty.add(recoveredFromFile2.getTaskById(1));
        tmpPrioroty.add(recoveredFromFile2.getTaskById(4));
        tmpPrioroty.add(recoveredFromFile2.getTaskById(5));

        List<Task> priorityTest = recoveredFromFile2.getPrioritizedTasks();
        assertEquals(tmpPrioroty, priorityTest,
                "Ошибка, когда у задач не прописано время старта");
    }

}