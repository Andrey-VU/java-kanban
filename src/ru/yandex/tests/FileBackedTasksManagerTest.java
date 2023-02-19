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
    TaskManager recoveredFromFile;
    Task testTaskZ;
    Epic testEpicZ;
    Subtask testSubtaskZ;
    List historyForTest;

    @BeforeEach
    public void beforeEach() throws IOException {
        inMemoryManager = Managers.getDefault();
        recoveredFromFile = Managers.getFileBackedManager();
        FileBackedTasksManager.fileIn = new File("storageTestIn.csv");
        FileBackedTasksManager.fileOut = new File("storageTestOut1.csv");
        recoveredFromFile = FileBackedTasksManager.loadFromFile(FileBackedTasksManager.fileIn);

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
        inMemoryManager.dellThemAll();
        recoveredFromFile.dellThemAll();
        if (recoveredFromFile.getHistory() != null) {
            recoveredFromFile.getHistory().clear();
        }
        FileBackedTasksManager.fileIn = null;
        FileBackedTasksManager.fileOut = null;
        if (recoveredFromFile.getPrioritizedTasks() !=null) {
            recoveredFromFile.getPrioritizedTasks().clear();
        }
    }


    @Test
    public void shouldMakePrioritizedList () throws IOException {
        String[] newTmpTask = {"4","TASK","Гитарный конкурс в Питере","NEW","Купить билеты",
                "01.01.2005--12:00","3600"};
                Task tmpTask = new Task(newTmpTask);
        recoveredFromFile.makeNewTask(tmpTask);
        List<Task> tmpPrioroty = new ArrayList<>();
        tmpPrioroty.add(recoveredFromFile.getSubTaskById(3));
        tmpPrioroty.add(recoveredFromFile.getTaskById(1));
        tmpPrioroty.add(recoveredFromFile.getTaskById(4));

        List<Task> priorityTest = recoveredFromFile.getPrioritizedTasks();
        assertEquals(tmpPrioroty, priorityTest,
                "Задачи не удалось ранжировать по времени старта");
    }

    @Test
    public void shouldMakePrioritizedListWithoutData () throws IOException {

        Task tmpTask4 = new Task("Гитарный конкурс в Питере","Купить билеты",4,Status.NEW);
        Task tmpTask5 = new Task("Test_priority","Descr 5",5,Status.NEW);
        recoveredFromFile.makeNewTask(tmpTask4);
        recoveredFromFile.makeNewTask(tmpTask5);
        List<Task> tmpPrioroty = new ArrayList<>();
        tmpPrioroty.add(recoveredFromFile.getSubTaskById(3));
        tmpPrioroty.add(recoveredFromFile.getTaskById(1));
        tmpPrioroty.add(recoveredFromFile.getTaskById(4));
        tmpPrioroty.add(recoveredFromFile.getTaskById(5));

        List<Task> priorityTest = recoveredFromFile.getPrioritizedTasks();
        assertEquals(tmpPrioroty, priorityTest,
                "Ошибка, когда у задач не прописано время старта");
    }

        @Test
    public void shouldLoadFromFile() throws IOException {
        Task taskFromFile = recoveredFromFile.getTaskById(1);
        Epic epicFromFile = recoveredFromFile.getEpicById(2);
        Subtask subtaskFromFile = recoveredFromFile.getSubTaskById(3);
        List newHistory = recoveredFromFile.getHistory();

        assertEquals(inMemoryManager.getTaskById(1).toString(), taskFromFile.toString(),
                "Task из файла загружен не корректно");
        assertEquals(inMemoryManager.getEpicById(2), epicFromFile,
                "Epic из файла загружен не корректно");
        assertEquals(inMemoryManager.getSubTaskById(3), subtaskFromFile,
                "Subtask из файла загружен не корректно");
        assertEquals(historyForTest.toString(), newHistory.toString(),
                "История из файла загружена не корректно");
    }

    @Test
    public void shouldSaveToFile() throws IOException {
        FileBackedTasksManager.fileIn = new File("storageTestOut1.csv");
        FileBackedTasksManager.fileOut = new File("storageTestOut2.csv");

        recoveredFromFile.dellThemAll();
        recoveredFromFile.getHistory().clear();

        recoveredFromFile = FileBackedTasksManager.loadFromFile(FileBackedTasksManager.fileIn);

        Task taskFromFile2 = recoveredFromFile.getTaskById(1);
        Epic epicFromFile2 = recoveredFromFile.getEpicById(2);
        Subtask subtaskFromFile2 = recoveredFromFile.getSubTaskById(3);

        assertEquals(inMemoryManager.getTaskById(1).toString(), taskFromFile2.toString(),
                "Сохранение задач в файл некорректно");
        assertEquals(inMemoryManager.getEpicById(2), epicFromFile2,
                "Epic из файла загружен не корректно");
        assertEquals(inMemoryManager.getSubTaskById(3), subtaskFromFile2,
                "Subtask из файла загружен не корректно");
    }

    @Test
    public void loadWhenEpicWithoutSubtask() throws IOException {
        FileBackedTasksManager.fileIn = new File("storage_In_TestEpicWithoutSubtask.csv");
        FileBackedTasksManager.fileOut = new File("storage_Out_TestEpicWithoutSubtask.csv");

        TaskManager newManager = Managers.getDefault();
        // TaskManager newManager2 = Managers.getDefault();

        Epic epicWithoutSb = new Epic("Without sub name", "Without sub discr",
                0, Status.NEW);
        newManager.makeNewEpic(epicWithoutSb);
        Epic expectedEpic = newManager.getEpicById(epicWithoutSb.getId());

        recoveredFromFile = FileBackedTasksManager.loadFromFile(FileBackedTasksManager.fileIn);
        assertEquals(expectedEpic, recoveredFromFile.getEpicById(1),
                "Эпик без подзадач не создан или создан с ошибками");
        assertNull(recoveredFromFile.getListSubtasksOfEpic(inMemoryManager.getEpicById(1)),
                "создание Эпика без подзадач прошло с ошибками");
    }

    @Test
    public void loadWhenFileWithoutHistory() throws IOException {
        FileBackedTasksManager newRecoveredFromFile = new FileBackedTasksManager();

        inMemoryManager.getHistory().clear();

        FileBackedTasksManager.fileIn = new File("storage_In_WithoutHistory.csv");
        FileBackedTasksManager.fileOut = new File("storage_Out_WithoutHistory.csv");

        newRecoveredFromFile = FileBackedTasksManager.loadFromFile(FileBackedTasksManager.fileIn);

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
}