package TaskTracker;
import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    String[] status = {"NEW", "IN_PROGRESS", "DONE"};   // список статусов работы над задачей
    Integer lastID; // здесь хранитися последний сгенерированный id всех задач
    private HashMap<Integer, Epic> epicTasks = new HashMap<>(); // для хранения всех TaskTracker.Epic задач
    private HashMap<Integer, Subtask> subtaskTasks = new HashMap<>(); // для хранения всех TaskTracker.Subtask задач
    private HashMap<Integer, Task> taskTasks = new HashMap<>(); // для хранения всех TaskTracker.Task задач

    // МЕТОДЫ В РАЗРАБОТКЕ
    // Получение списка всех подзадач определённого эпика.
    //


    // МЕТОДЫ ДЛЯ TASK
    public void makeNewTask(String nameOfTask, String descriptionOfTask, String status) {   // новая задача
        int uniqueId = makeID();
        Task task = new Task(uniqueId, nameOfTask, descriptionOfTask, status);
        taskTasks.put(uniqueId, task);           // сохранили объект, содержащий полное описание задачи
    }
    public Task getTaskById(int idForSearch) {    //Получение задачи Task по идентификатору.
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
    public void clearTask() {
        if (!taskTasks.isEmpty()) {
            taskTasks.clear();
        }
    }


    /*public void makeNewEpic(String nameOfTask, String descriptionOfTask, String status) {   // новая Эпик задача
        int uniqueEpicId = makeID();
        //makeNewSubtask(String nameOfTask, String descriptionOfTask, String status);
        Epic epic = new Epic(uniqueEpicId, nameOfTask, descriptionOfTask, status);
        epicTasks.put(uniqueEpicId, epic);           // сохранили объект, содержащий полное описание Epic задачи
    }*/
    public void makeNewSubtask(String nameOfTask, String descriptionOfTask, String status) {   // новая Эпик задача
        int uniqueSubtaskId = makeID();
        Subtask subtask = new Subtask(uniqueSubtaskId, nameOfTask, descriptionOfTask, status);
        subtaskTasks.put(uniqueSubtaskId, subtask);       // сохранили объект, содержащий полное описание subtask задачи
    }


    public Epic getEpicById(int idForSearch) {     //Получение задачи Epic по идентификатору.
        if (epicTasks.containsKey(idForSearch)) {
            return epicTasks.get(idForSearch);
        } else {
            return null;
        }
    }
    public Subtask getSubTaskById(int idForSearch) {       //Получение задачи subTask по идентификатору.
        if (subtaskTasks.containsKey(idForSearch)) {
            return subtaskTasks.get(idForSearch);
        } else {
            return null;
        }
    }


    // МЕТОДЫ ДЛЯ ЗАДАЧ ВСЕХ типов сразу
    public ArrayList<Object> getListAllTasks() {                      //Получение списка всех задач
        ArrayList<Object> taskEpicSubtaskList = new ArrayList<Object>();
        for (Integer id : taskTasks.keySet()) {
            taskEpicSubtaskList.add(taskTasks.get(id));
        }
        for (Integer id : epicTasks.keySet()) {
            taskEpicSubtaskList.add(epicTasks.get(id));
        }
        for (Integer id : subtaskTasks.keySet()) {
            taskEpicSubtaskList.add(subtaskTasks.get(id));
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
            epicTasks.remove(idForDell);
        } else if (subtaskTasks.containsKey(idForDell)) {
            subtaskTasks.remove(idForDell);
        }
    }

    Integer makeID() {  // метод который генерирует id для всех классов, при обращении к нему
        int id = lastID + 1;
        lastID = id;
        return id;
    }
}

/*
        Дополнительные методы:
        Получение списка всех подзадач определённого эпика.
        Управление статусами осуществляется по следующему правилу:
        Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче.
        По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.

        Для эпиков:
        если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
        если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
        во всех остальных случаях статус должен быть IN_PROGRESS.
     */




