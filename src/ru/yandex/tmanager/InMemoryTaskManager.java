package ru.yandex.tmanager;
import ru.yandex.tasks.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager  {
    private int lastID; //  здесь хранитися последний сгенерированный id всех задач
    private HashMap<Integer, Epic> epicTasks = new HashMap<>(); // для хранения всех ru.yandex.tasks.Epic задач
    private HashMap<Integer, Subtask> subtaskTasks = new HashMap<>(); // для хранения всех ru.yandex.tasks.Subtask задач
    private HashMap<Integer, Task> taskTasks = new HashMap<>(); // для хранения всех ru.yandex.tasks.Task задач
    private HistoryManager historyManager = Managers.getDefaultHistory();

    Comparator<Task> comparator = (task, t1) ->  task.getStartTime().isBefore(t1.getStartTime()) ? 1 : -1;
    Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    public Set<Task> getPrioritizedTasks() {
        if (!prioritizedTasks.isEmpty()) {
            return prioritizedTasks;
        } else {
            return null;
        }
    }

    public HashMap<Integer, Epic> getEpicTasks() {
        return epicTasks;
    }
    public HashMap<Integer, Subtask> getSubtaskTasks() {
        return subtaskTasks;
    }
    public HashMap<Integer, Task> getTaskTasks() {
        return taskTasks;
    }
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    // МЕТОДЫ ДЛЯ EPIC ==============================================================================================
    @Override
    public void makeNewEpic(Epic epic) {              // новая Эпик задача
        if (epic != null) {
        Epic newEpic = epic;
            int uniqueEpicId = makeID();
            newEpic.setId(uniqueEpicId);
            epicTasks.put(uniqueEpicId, newEpic);         // сохранили объект с описанием ru.yandex.tasks.Epic задачи
            //prioritizedTasks.add(newEpic);
        }
    }

    @Override
    public Epic getEpicById(int idForSearch) {        //Получение задачи ru.yandex.tasks.Epic по идентификатору.
        if (epicTasks.containsKey(idForSearch)) {
            Epic tmpEpic = epicTasks.get(idForSearch); // временная переменная для вытаскивания объекта из hashMap
            historyManager.add(tmpEpic);               // Добавляем объект в хранилище
            return tmpEpic;
        } else {
            return null;
        }
    }
    @Override
    public void updateEpic(int idForUpdate, Epic epic)  {   // Обновление Эпика по id
        if (epicTasks.containsKey(idForUpdate) && epic.getId() == idForUpdate) {
            epicTasks.put(idForUpdate, epic);
        }
    }
    @Override
    public ArrayList<Subtask> getListSubtasksOfEpic(Epic epic) {   //Получение списка всех подзадач определённого эпика
        ArrayList<Subtask> subtasksList = new ArrayList<Subtask>();
        if (epic !=null && !epic.getMySubtasks().isEmpty()) {
            for (Subtask mySubtask : epic.getMySubtasks()) {
                subtasksList.add(mySubtask);
            }
            return subtasksList;
        } else {
            return null;
        }
    }
    @Override
    public void dellAllEpic() {    //Удаление всех задач и подзадач типа Эпик из хранилища
        if (!epicTasks.isEmpty()) {
            for (Integer id : epicTasks.keySet()) {
                if (epicTasks.get(id).getMySubtasks() != null) {
                    for (Subtask mySubtask : epicTasks.get(id).getMySubtasks()) {
                        if (mySubtask != null) {
                            dellTaskById(mySubtask.getId());
                            historyManager.remove(mySubtask.getId());     // удаление задачи из истории просмотров
                        }
                    }
                }
            }
            epicTasks.clear();
        }
    }

    // МЕТОДЫ ДЛЯ SUBTASKS-------------------------------------------------------------------------------------------
    @Override
    public void makeNewSubtask(Subtask subtask)  {
        if (subtask != null) {
            Subtask newSubtask = subtask;              // создали подзадачу. уже здесь есть статус и есть инфо об Эпике
            int uniqSubtaskId = makeID();                  // присвоили подзадаче уникальный id
            newSubtask.setId(uniqSubtaskId);               // присвоили подзадаче уникальный id
            if (getEpicById(newSubtask.getEpicID()) != null) {
                getEpicById(newSubtask.getEpicID()).setMySubtask(newSubtask);         // отправить подзадачу в эпик
                subtaskTasks.put(uniqSubtaskId, newSubtask);   // записали  подзадачу в хранилище
                statusChecker(getEpicById(subtask.getEpicID()));       // проверить статусы всех субтасков, входящих в Эпик,
                // скорректировать статус эпика, если необходимо
            }
        }
    }
    @Override
    public Subtask getSubTaskById(int idForSearch) {       //Получение задачи subTask по идентификатору.
        if (subtaskTasks.containsKey(idForSearch) && !subtaskTasks.get(idForSearch).equals(null)) {
            Subtask tmpSubTask = subtaskTasks.get(idForSearch);
            historyManager.add(tmpSubTask);   // Добавляем объект в хранилище
            return tmpSubTask;
        } else {
            return null;
        }
    }
    @Override
    public void updateSubtask(int idForUpdate, Subtask subtask) {
        if (subtaskTasks.containsKey(idForUpdate) && subtask.getId() == idForUpdate) {
            getEpicById(subtask.getEpicID()).getMySubtasks().remove(getSubTaskById(subtask.getId()));
            dellTaskById(subtask.getId());   // очищаем список подзадач эпика и главное хранилище подзадач
            getEpicById(subtask.getEpicID()).setMySubtask(subtask);         // отправить подзадачу в эпик
            subtaskTasks.put(idForUpdate, subtask);
        }
        statusChecker(getEpicById(subtask.getEpicID()));
    }

    @Override
    public void statusChecker(Epic newEpic) {   // метод проверки и пересчёта статусов для Эпиков
        int counterNEW = 0;
        int counterINPROGRESS = 0;
        int counterDone = 0;
        for (Subtask mySubtask : newEpic.getMySubtasks()) {
//            System.out.println("Cтатус " + mySubtask + " = " + mySubtask.getStatus()  );
            switch (mySubtask.getStatus()){
                case NEW:
                    counterNEW++;
                    break;
                case IN_PROGRESS:
                    counterINPROGRESS++;
                    break;
                case DONE:
                    counterDone++;
                    break;
            }

        }
//        System.out.println("Счётчики статусов: " + "\n" + "NEW = " + counterNEW + "\n" + "IN_PROGRESS = "
//                + counterINPROGRESS + "\n" + "DONE = " + counterDone );

        if (counterNEW >= 0 && counterINPROGRESS == 0 && counterDone == 0) {
            newEpic.setStatus(Status.NEW);
        } else if (counterDone > 0 && counterNEW == 0 &&  counterINPROGRESS == 0) {
            newEpic.setStatus(Status.DONE);
        } else {
            newEpic.setStatus(Status.IN_PROGRESS);
        }
    }

    // МЕТОДЫ ДЛЯ TASK   =============================================================================================
    @Override
    public void makeNewTask(Task task) {   // новая задача
        if (task != null) {
            if (task.getName() != null || task.getDescription() !=null || task.getStatus() != null) {
                Task newTask = task;

            int uniqueId = makeID();
            newTask.setId(uniqueId);
            taskTasks.put(uniqueId, newTask);   // сохранили объект, содержащий полное описание задачи в хранилище
            }
        }
    }
    @Override
    public Task getTaskById(int idForSearch) {                   //Получение задачи ru.yandex.tasks.Task по идентификатору.
        if (taskTasks.containsKey(idForSearch)) {
            Task tmpTask = taskTasks.get(idForSearch);
            historyManager.add(tmpTask);      // Добавляем объект в хранилище
            return tmpTask;
        } else {
            return null;
        }
    }
    @Override
    public void updateTask(int idForUpdate, Task newTask)  {   //Обновление задач ru.yandex.tasks.Task
        if (taskTasks.containsKey(idForUpdate) && newTask.getId() == idForUpdate) {
            taskTasks.put(idForUpdate, newTask);
        }
    }
    @Override
    public void clearTask()  {                                        // Очистка списка всех задач ru.yandex.tasks.Task
        if (!taskTasks.isEmpty()) {
            taskTasks.clear();
        }
    }
    @Override
    public ArrayList<Task> getListAllTasksFromTask() {          //Получение списка всех ru.yandex.tasks.Task задач
        ArrayList<Task> tasksList = new ArrayList<Task>();
        if (!taskTasks.isEmpty()) {
            for (Task value : taskTasks.values()) {
                tasksList.add(value);
            }
            return tasksList;
        } else {
            return null;
        }
    }
    // МЕТОДЫ ДЛЯ ЗАДАЧ ВСЕХ типов сразу  ===========================================================================
    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }



    public void printHistory()  {
        System.out.println("================= ИСТОРИЯ ПРОСМОТРА ЗАДАЧ ============================");
        for (Task task : getHistory()) {
            System.out.println(task);
        }
    }
    @Override
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
    @Override
    public void dellThemAll()  {    //Удаление всех задач.
        if (!taskTasks.isEmpty()) {
            taskTasks.clear();
        } if (!epicTasks.isEmpty()) {
            epicTasks.clear();
        } if (!subtaskTasks.isEmpty()) {
            subtaskTasks.clear();
        }
    }
    @Override
    public void dellTaskById(int idForDell)  {  //Удаление по идентификатору.
        if (taskTasks.containsKey(idForDell)) {
            taskTasks.remove(idForDell);
            historyManager.remove(idForDell);
        } else if (epicTasks.containsKey(idForDell)) {
            for (Subtask mySubtask : epicTasks.get(idForDell).getMySubtasks()) {
                dellTaskById(mySubtask.getId());
                historyManager.remove(idForDell);
            }
            epicTasks.remove(idForDell);
        } else if (subtaskTasks.containsKey(idForDell)) {
            subtaskTasks.remove(idForDell);
            historyManager.remove(idForDell);
        }
    }

    int makeID() {              // генератор id для задач всех типов
        return ++lastID;
    }

    String toStringForFile(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() +  "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getStartTime()  + "," + task.getDuration();
    }
}


