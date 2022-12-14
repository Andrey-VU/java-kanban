package ru.yandex.taskManager;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int lastID; // здесь хранитися последний сгенерированный id всех задач
    private HashMap<Integer, Epic> epicTasks = new HashMap<>(); // для хранения всех ru.yandex.tasks.Epic задач
    private HashMap<Integer, Subtask> subtaskTasks = new HashMap<>(); // для хранения всех ru.yandex.tasks.Subtask задач
    private HashMap<Integer, Task> taskTasks = new HashMap<>(); // для хранения всех ru.yandex.tasks.Task задач

    // МЕТОДЫ ДЛЯ EPIC ==============================================================================================
    public void makeNewEpic(Epic epic) {              // новая Эпик задача
        Epic newEpic = epic;
        int uniqueEpicId = makeID();
        newEpic.setId(uniqueEpicId);
        epicTasks.put(uniqueEpicId, newEpic);         // сохранили объект, содержащий полное описание ru.yandex.tasks.Epic задачи
    }
    public Epic getEpicById(int idForSearch) {        //Получение задачи ru.yandex.tasks.Epic по идентификатору.
        if (epicTasks.containsKey(idForSearch)) {
            return epicTasks.get(idForSearch);
        } else {
            return null;
        }
    }
    public void updateEpic(int idForUpdate, Epic epic) {   // Обновление Эпика по id
        if (epicTasks.containsKey(idForUpdate)) {
            epicTasks.put(idForUpdate, epic);
        }
    }
    public ArrayList<Subtask> getListSubtasksOfEpic(Epic epic) {   //Получение списка всех подзадач определённого эпика
        ArrayList<Subtask> subtasksList = new ArrayList<Subtask>();
        if (!epic.getMySubtasks().isEmpty()) {
            for (Subtask mySubtask : epic.getMySubtasks()) {
                subtasksList.add(mySubtask);
            }
            return subtasksList;
        } else {
            return null;
        }
    }

    public void dellAllEpic() {    //Удаление всех задач и подзадач Эпика
        if (!epicTasks.isEmpty()) {
            for (Integer integer : epicTasks.keySet()) {
                for (Subtask mySubtask : epicTasks.get(integer).getMySubtasks()) {
                    dellTaskById(mySubtask.getId());
                }
            }
            epicTasks.clear();
        }
    }


    // МЕТОДЫ ДЛЯ SUBTASKS-------------------------------------------------------------------------------------------
    public void makeNewSubtask(Subtask subtask) {
        Subtask newSubtask = subtask;              // создали подзадачу. уже здесь есть статус и есть инфо об Эпике
        int uniqSubtaskId = makeID();                  // присвоили подзадаче уникальный id
        newSubtask.setId(uniqSubtaskId);               // присвоили подзадаче уникальный id
        getEpicById(newSubtask.getEpicID()).setMySubtask(newSubtask);         // отправить подзадачу в эпик
        statusChecker(newSubtask);                      // проверить статусы всех субтасков, входящих в Эпик,
                                                       // скорректировать статус эпика, если необходимо
        subtaskTasks.put(uniqSubtaskId, newSubtask);   // записали  подзадачу в хранилище
    }

    public Subtask getSubTaskById(int idForSearch) {       //Получение задачи subTask по идентификатору.
        if (subtaskTasks.containsKey(idForSearch)) {
            return subtaskTasks.get(idForSearch);
        } else {
            return null;
        }
    }
    public void updateSubtask(int idForUpdate, Subtask subtask) {
        if (subtaskTasks.containsKey(idForUpdate)) {
            dellTaskById(subtask.getId());   // очищаем список подзадач эпика от старого эпика
            getEpicById(subtask.getEpicID()).setMySubtask(subtask);         // отправить подзадачу в эпик
            subtaskTasks.put(idForUpdate, subtask);
            statusChecker(subtask);
        }
    }

    public void statusChecker(Subtask newSubtask) {   // метод проверки и пересчёта статусов для Эпиков
        int counter = 0;
        int counterNEW = 0;
        int counterINPROGRESS = 0;
        int counterDone = 0;

        for (Subtask mySubtask : getEpicById(newSubtask.getEpicID()).getMySubtasks()) {
            if (mySubtask.getStatus().equals("NEW"))    {
                counterNEW += 1;
            } else if (mySubtask.getStatus().equals("IN_PROGRESS")) {
                counterINPROGRESS += 1;
            } else if (mySubtask.getStatus().equals("DONE")) {
                counterDone += 1;
            }
            counter += 1;
        }

        if (counterNEW == counter) {
            getEpicById(newSubtask.getEpicID()).setStatus("NEW");
        } else if (counterDone == counter) {
            getEpicById(newSubtask.getEpicID()).setStatus("DONE");
        } else {
            getEpicById(newSubtask.getEpicID()).setStatus("IN_PROGRESS");
        }
    }

    // МЕТОДЫ ДЛЯ TASK   =============================================================================================
    public void makeNewTask(Task task) {   // новая задача
        Task newTask = task;
        int uniqueId = makeID();
        newTask.setId(uniqueId);
        taskTasks.put(uniqueId, newTask);   // сохранили объект, содержащий полное описание задачи в хранилище
    }
    public Task getTaskById(int idForSearch) {                   //Получение задачи ru.yandex.tasks.Task по идентификатору.
        if (taskTasks.containsKey(idForSearch)) {
            return taskTasks.get(idForSearch);
        } else {
            return null;
        }
    }

    public void updateTask(int idForUpdate, Task newTask) {   //Обновление задач ru.yandex.tasks.Task
        if (taskTasks.containsKey(idForUpdate)) {
            taskTasks.put(idForUpdate, newTask);
        }
    }
    public void clearTask() {                                        // Очистка списка всех задач ru.yandex.tasks.Task
        if (!taskTasks.isEmpty()) {
            taskTasks.clear();
        }
    }
    public ArrayList<Task> getListAllTasksFromTask() {                //Получение списка всех ru.yandex.tasks.Task задач
        ArrayList<Task> tasksList = new ArrayList<Task>();
        if (!taskTasks.isEmpty()) {
            for (Integer id : taskTasks.keySet()) {
                tasksList.add(taskTasks.get(id));
            }
            return tasksList;
        } else {
        return null;
        }
    }
    // МЕТОДЫ ДЛЯ ЗАДАЧ ВСЕХ типов сразу  ===========================================================================
    public ArrayList<Object> getListAllTasks() {                      //Получение списка всех задач всех типов
        ArrayList<Object> taskEpicSubtaskList = new ArrayList<Object>();
        if (!taskTasks.isEmpty()) {
            for (Integer id : taskTasks.keySet()) {
                taskEpicSubtaskList.add(taskTasks.get(id));
            }
        }
        if (!epicTasks.isEmpty()) {
            for (Integer id : epicTasks.keySet()) {
                taskEpicSubtaskList.add(epicTasks.get(id));
            }
        }
        if (!subtaskTasks.isEmpty()) {
            for (Integer id : subtaskTasks.keySet()) {
                taskEpicSubtaskList.add(subtaskTasks.get(id));
            }
        }
        return taskEpicSubtaskList;
    }
    public void dellThemAll() {    //Удаление всех задач.
        if (!taskTasks.isEmpty()) {
            taskTasks.clear();
        } if (!epicTasks.isEmpty()) {
            epicTasks.clear();
        } if (!subtaskTasks.isEmpty()) {
            subtaskTasks.clear();
        }
    }
    public void dellTaskById(int idForDell) {  //Удаление по идентификатору.
        if (taskTasks.containsKey(idForDell)) {
            taskTasks.remove(idForDell);
        } else if (epicTasks.containsKey(idForDell)) {
            for (Subtask mySubtask : epicTasks.get(idForDell).getMySubtasks()) {
                dellTaskById(mySubtask.getId());
            }
            epicTasks.remove(idForDell);
        } else if (subtaskTasks.containsKey(idForDell)) {
            subtaskTasks.remove(idForDell);
        }
    }

    int makeID() {              // генератор id для задач всех типов
        int id = lastID + 1;
        lastID = id;
        return id;
    }
}

