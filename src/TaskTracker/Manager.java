package TaskTracker;
import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    int lastID; // здесь хранитися последний сгенерированный id всех задач
    private HashMap<Integer, Epic> epicTasks = new HashMap<>(); // для хранения всех TaskTracker.Epic задач
    private HashMap<Integer, Subtask> subtaskTasks = new HashMap<>(); // для хранения всех TaskTracker.Subtask задач
    private HashMap<Integer, Task> taskTasks = new HashMap<>(); // для хранения всех TaskTracker.Task задач

    // МЕТОДЫ ДЛЯ EPIC ==============================================================================================
    public void makeNewEpic(Epic epic) {              // новая Эпик задача
        Epic newEpic = epic;
        int uniqueEpicId = makeID();
        newEpic.setMyEpicId(uniqueEpicId);
        epicTasks.put(uniqueEpicId, newEpic);         // сохранили объект, содержащий полное описание Epic задачи
    }
    public Epic getEpicById(int idForSearch) {        //Получение задачи Epic по идентификатору.
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
                    dellTaskById(mySubtask.getIdOfSubtask());
                }
            }
            epicTasks.clear();
        }
    }


    // МЕТОДЫ ДЛЯ SUBTASKS-------------------------------------------------------------------------------------------
    public void makeNewSubtask(Subtask subtask) {
        Subtask newSubtask = subtask;              // создали подзадачу. уже здесь есть статус и есть инфо об Эпике
        int uniqSubtaskId = makeID();                  // присвоили подзадаче уникальный id
        newSubtask.setSubtaskId(uniqSubtaskId);        // присвоили подзадаче уникальный id
        getEpicById(newSubtask.getIdOfMyEpic()).setMySubtasksId(uniqSubtaskId);   // отправить Id подзадачи в эпика
        getEpicById(newSubtask.getIdOfMyEpic()).setMySubtask(newSubtask);         // отправить подзадачу в эпик
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
            dellTaskById(subtask.getIdOfSubtask());   // очищаем список подзадач эпика от старого эпика
            getEpicById(subtask.getIdOfMyEpic()).setMySubtasksId(subtask.getIdOfSubtask()); ;   // отправить Id подзадачи в эпика
            getEpicById(subtask.getIdOfMyEpic()).setMySubtask(subtask);         // отправить подзадачу в эпик
            subtaskTasks.put(idForUpdate, subtask);
            statusChecker(subtask);
        }
    }

    public void statusChecker(Subtask newSubtask) {   // метод проверки и пересчёта статусов для Эпиков
        int counter = 0;
        int counterNEW = 0;
        int counterINPROGRESS = 0;
        int counterDone = 0;

        for (Subtask mySubtask : getEpicById(newSubtask.getIdOfMyEpic()).getMySubtasks()) {
            if (mySubtask.getMyStatus().equals("NEW"))    {
                counterNEW += 1;
            } else if (mySubtask.getMyStatus().equals("IN_PROGRESS")) {
                counterINPROGRESS += 1;
            } else if (mySubtask.getMyStatus().equals("DONE")) {
                counterDone += 1;
            }
            counter += 1;
        }

        if (counterNEW == counter) {
            getEpicById(newSubtask.getIdOfMyEpic()).setMyStatus("NEW");
        } else if (counterDone == counter) {
            getEpicById(newSubtask.getIdOfMyEpic()).setMyStatus("DONE");
        } else {
            getEpicById(newSubtask.getIdOfMyEpic()).setMyStatus("IN_PROGRESS");
        }
    }

    // МЕТОДЫ ДЛЯ TASK   =============================================================================================
    public void makeNewTask(Task task) {   // новая задача
        Task newTask = task;
        int uniqueId = makeID();
        newTask.setTaskId(uniqueId);
        taskTasks.put(uniqueId, newTask);   // сохранили объект, содержащий полное описание задачи в хранилище
    }
    public Task getTaskById(int idForSearch) {                   //Получение задачи Task по идентификатору.
        if (taskTasks.containsKey(idForSearch)) {
            return taskTasks.get(idForSearch);
        } else {
            return null;
        }
    }

    public void updateTask(int idForUpdate, Task newTask) {   //Обновление задач Task
        if (taskTasks.containsKey(idForUpdate)) {
            taskTasks.put(idForUpdate, newTask);
        }
    }
    public void clearTask() {                                        // Очистка списка всех задач Task
        if (!taskTasks.isEmpty()) {
            taskTasks.clear();
        }
    }
    public ArrayList<Task> getListAllTasksFromTask() {                //Получение списка всех Task задач
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
                dellTaskById(mySubtask.getIdOfSubtask());
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


/*
//for
        //(Subtask mySubtask : getEpicById(newSubtask.getIdOfMyEpic()).getMySubtasks()) {  // перебор подзадач эпика
            counter +=1;
          //  if (mySubtask.getMyStatus().equals("NEW")) {
                counterNEW += 1;
            } else if (mySubtask.getMyStatus().equals("DONE")) {
                counterDone += 1;
            }
        }

        (newSubtask.getMyStatus().equals("IN_PROGRESS")) {
                counterINPROGRESS += 1;
            } else if (newSubtask.getMyStatus().equals("NEW")) {
                counterNEW += 1;

 */