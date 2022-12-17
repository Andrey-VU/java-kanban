package ru.yandex.taskManager;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;
import ru.yandex.tasks.Status;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

interface TaskManager {
    // МЕТОДЫ ДЛЯ EPIC ==============================================================================================
    void makeNewEpic(Epic epic);                           // создать/сохранить новую Эпик задачу
    Epic getEpicById(int idForSearch);                     //Получить задачи  ru.yandex.tasks.Epic по идентификатору
    void updateEpic(int idForUpdate, Epic epic);           // Обновление Эпика по id
    ArrayList<Subtask> getListSubtasksOfEpic(Epic epic);   //Получение списка всех подзадач определённого эпика
    void dellAllEpic();                                     //Удаление всех задач и подзадач Эпика

    // МЕТОДЫ ДЛЯ SUBTASKS-------------------------------------------------------------------------------------------
    void makeNewSubtask(Subtask subtask);                   //  создать новую подзадачу
    Subtask getSubTaskById(int idForSearch);                //  Получение задачи subTask по идентификатору.
    void updateSubtask(int idForUpdate, Subtask subtask);   //    Обновить подзадачу
    void statusChecker(Subtask newSubtask);                 // метод проверки и пересчёта статусов для Эпиков

    // МЕТОДЫ ДЛЯ TASK   =============================================================================================
    void makeNewTask(Task task);                            // создатьновую задачу
    Task getTaskById(int idForSearch);                      //Получение задачи ru.yandex.tasks.Task по идентификатору.
    void updateTask(int idForUpdate, Task newTask);
    void clearTask();
    ArrayList<Task> getListAllTasksFromTask();               //Получение списка всех ru.yandex.tasks.Task задач

    // МЕТОДЫ ДЛЯ ЗАДАЧ ВСЕХ типов сразу  ===========================================================================
    ArrayList<Object> getListAllTasks();                      //Получение списка всех задач всех типов
    void dellThemAll();                                       //Удаление всех задач.
    void dellTaskById(int idForDell);                         //Удаление по идентификатору.
    //int makeID();
}