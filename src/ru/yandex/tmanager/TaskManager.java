package ru.yandex.tmanager;
import ru.yandex.exceptions.IntersectionException;
import ru.yandex.exceptions.ManagerSaveException;
import ru.yandex.http.KVTaskClient;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    boolean findIntersection(Task newTask) throws IntersectionException;

    List<Task> getPrioritizedTasks();
    void save();

    // МЕТОДЫ ДЛЯ EPIC ==============================================================================================
    void makeNewEpic(Epic epic) throws IOException, ManagerSaveException;                           // создать/сохранить новую Эпик задачу
    Epic getEpicById(int idForSearch);                     //Получить задачи  ru.yandex.tasks.Epic по идентификатору
    void updateEpic(int idForUpdate, Epic epic) throws IOException, ManagerSaveException;           // Обновление Эпика по id
    ArrayList<Subtask> getListSubtasksOfEpic(Epic epic);   //Получение списка всех подзадач определённого эпика
    void dellAllEpic() throws IOException, ManagerSaveException;                                     //Удаление всех задач и подзадач Эпика
    void statusChecker(Epic newEpic);                 // метод проверки и пересчёта статусов для Эпиков


    // МЕТОДЫ ДЛЯ SUBTASKS-------------------------------------------------------------------------------------------
    void makeNewSubtask(Subtask subtask) throws IOException, ManagerSaveException;                   //  создать новую подзадачу
    Subtask getSubTaskById(int idForSearch);                //  Получение задачи subTask по идентификатору.
    void updateSubtask(int idForUpdate, Subtask subtask) throws IOException, ManagerSaveException;   //    Обновить подзадачу

    void dellAllSubtasks() throws IOException, ManagerSaveException; // удаление всех подзадач


    // МЕТОДЫ ДЛЯ TASK   =============================================================================================
    void makeNewTask(Task task) throws IOException, ManagerSaveException;                            // создать новую задачу
    Task getTaskById(int idForSearch);                      //Получение задачи ru.yandex.tasks.Task по идентификатору.
    void updateTask(int idForUpdate, Task newTask) throws IOException, ManagerSaveException;
    void clearTask() throws IOException, ManagerSaveException;
    void dellAllTasks() throws IOException, ManagerSaveException; // удаление всех задач

    // МЕТОДЫ ДЛЯ ЗАДАЧ ВСЕХ типов сразу  ==========================================================================

    ArrayList<Task> getHistory();
    ArrayList<Task> getListAllTasks();                      //Получение списка всех Task
    List<Epic> getListAllEpics();
    List<Subtask> getListAllSubtasks();

    void dellThemAll() throws IOException, ManagerSaveException;                                       //Удаление всех задач.
    void dellTaskById(int idForDell) throws IOException, ManagerSaveException;                         //Удаление по идентификатору.

    void printHistory();

    void setKey(String artem2);

    KVTaskClient getKvTaskClient();
}