package ru.yandex.tmanager;
import ru.yandex.exceptions.IntersectionException;
import ru.yandex.exceptions.ManagerSaveException;
import ru.yandex.http.KVTaskClient;
import ru.yandex.tasks.*;

import java.io.IOException;
import java.util.*;
import java.util.Comparator;

public class InMemoryTaskManager implements TaskManager  {
    private int lastID; //  здесь хранитися последний сгенерированный id всех задач
    private HashMap<Integer, Epic> epicTasks = new HashMap<>(); // для хранения всех ru.yandex.tasks.Epic задач
    private HashMap<Integer, Subtask> subtaskTasks = new HashMap<>(); // для хранения всех ru.yandex.tasks.Subtask задач
    private HashMap<Integer, Task> taskTasks = new HashMap<>(); // для хранения всех ru.yandex.tasks.Task задач
    private HistoryManager historyManager = Managers.getDefaultHistory();
    public Set<Task> prioritizedTasks;
    public FirstComparator comparator = new FirstComparator();

    public InMemoryTaskManager() {
        prioritizedTasks = new TreeSet<>(comparator);
    }

    class FirstComparator implements Comparator<Task> {
        @Override
        public int compare(Task t1, Task t2) {
            if (t1 != null && t2 != null) {
                if (t1.getStartTime() == null && t2.getStartTime() == null) {
                    return t1.getId() - t2.getId();
                } else if (t1.getStartTime() == null && t2.getStartTime() != null) {
                    return 1;
                } else if (t1.getStartTime() != null && t2.getStartTime() == null) {
                    return -1;
                } else {
                    return t1.getStartTime().isBefore(t2.getStartTime()) == true ? -1 : 1;
                }
            }
            return 0;
        }
    }

    @Override
    public boolean findIntersection(Task newTask)  {
        // если список ранжированных задач пуст - возвращает false = разрешает создать задачу...
        if (getPrioritizedTasks().isEmpty()){
            return false;
        } else if (newTask.getStartTime() == null) {   // если время старта newT не задано
            return false;
        } else {                      // если найдём пересечения, вернём true, во всех иных случаях вернём false
            for (Task prioritizedTask : getPrioritizedTasks()) {
                if (prioritizedTask.getStartTime().isBefore(newTask.getStartTime()) &&
                        prioritizedTask.getEndTime().isAfter(newTask.getStartTime())) {
                    return true;
                } else if (prioritizedTask.getStartTime().isBefore(newTask.getEndTime()) &&
                        (prioritizedTask.getEndTime().isAfter(newTask.getStartTime()))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void save() {

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
    public void makeNewSubtask(Subtask subtask) throws IntersectionException {

        if (subtask != null) {
            if (findIntersection(subtask)) {
               throw new IntersectionException("Конфликт времени исполнения! Задача не может быть добавлена");
           }
              // разрешаем создать новую задачу
            int uniqSubtaskId = makeID();                  // присвоили подзадаче уникальный id
            subtask.setId(uniqSubtaskId);               // присвоили подзадаче уникальный id
            if (getEpicById(subtask.getEpicID()) != null) {
                getEpicById(subtask.getEpicID()).setMySubtask(subtask);  // отправить подзадачу в эпик
                subtaskTasks.put(uniqSubtaskId, subtask);   // записали подзадачу в хранилище
                statusChecker(getEpicById(subtask.getEpicID()));   // проверить статусы всех субтасков,
                // входящих в Эпик, скорректировать статус эпика, если необходимо
                prioritizedTasks.add(subtask);       // добавляем новую задачу в список приоритетов
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
            Subtask tmpSubTask = getSubTaskById(idForUpdate);
            prioritizedTasks.removeIf(obj -> obj.getId() == subtask.getId());
            if (findIntersection(subtask)) {
                prioritizedTasks.add(tmpSubTask);
                throw new IntersectionException("Конфликт времени исполнения! Задача не может быть обновлена");
            }
            getEpicById(subtask.getEpicID()).getMySubtasks().remove(getSubTaskById(subtask.getId()));
            dellTaskById(subtask.getId());   // очищаем список подзадач эпика и главное хранилище подзадач
            getEpicById(subtask.getEpicID()).setMySubtask(subtask);         // отправить подзадачу в эпик
            subtaskTasks.put(idForUpdate, subtask);
            prioritizedTasks.add(subtask);
            statusChecker(getEpicById(subtask.getEpicID()));
        }
    }

    @Override
    public void dellAllSubtasks() throws IOException, ManagerSaveException {
        if (!subtaskTasks.isEmpty()) {
            for (Integer id : subtaskTasks.keySet()) {
                if (subtaskTasks.get(id) != null) {
                    dellTaskById(id);
                    historyManager.remove(id);     // удаление задачи из истории просмотров
                }
                subtaskTasks.clear();
            }
        }
    }

    @Override
    public void statusChecker(Epic newEpic) {   // метод проверки и пересчёта статусов для Эпиков
        int counterNEW = 0;
        int counterINPROGRESS = 0;
        int counterDone = 0;
        for (Subtask mySubtask : newEpic.getMySubtasks()) {
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
            if (task.getName() != null || task.getDescription() != null || task.getStatus() != null) {
               if (findIntersection(task)) {
                  throw new IntersectionException("Конфликт времени исполнения! Задача не может быть добавлена");
               }
               int uniqueId = makeID();
               task.setId(uniqueId);
               taskTasks.put(uniqueId, task);   // сохранили объект в хранилище
               prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public Task getTaskById(int idForSearch) {               //Получение задачи ru.yandex.tasks.Task по идентификатору.
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
            prioritizedTasks.removeIf(obj -> obj.getId() == newTask.getId());

            if (findIntersection(newTask)) {
                prioritizedTasks.add(getTaskById(idForUpdate));
                throw new IntersectionException("Конфликт времени исполнения! Задача не может быть обновлена");
            }
               prioritizedTasks.add(newTask);
            taskTasks.put(idForUpdate, newTask);
        }
    }
    @Override
    public void clearTask()  {                                        // Очистка списка всех задач ru.yandex.tasks.Task
        if (!taskTasks.isEmpty()) {
            taskTasks.clear();
            for (Task prioritizedTask : prioritizedTasks) {
                if (prioritizedTask.getType().equals(Type.TASK)) {
                    prioritizedTasks.remove(prioritizedTask);
                }
            }
            for (Task prioritizedTask : getPrioritizedTasks()) {
                if (prioritizedTask.getType().equals(Type.TASK)) {
                    getPrioritizedTasks().remove(prioritizedTask);
                }
            }
        }
    }

    @Override
    public void dellAllTasks() throws IOException, ManagerSaveException {
        if (!taskTasks.isEmpty()) {
            for (Integer id : taskTasks.keySet()) {
                if (taskTasks.get(id) != null) {
                    dellTaskById(id);
                    historyManager.remove(id);     // удаление задачи из истории просмотров
                }
                taskTasks.clear();
            }
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
    public void setKey(String artem2) {
    }

    @Override
    public KVTaskClient getKvTaskClient() {
        return null;
    }

    @Override
    public TaskManager loadFromKVServer() throws IOException, InterruptedException {
        return null;
    }

    @Override
    public ArrayList<Task> getListAllTasks() {                      //Получение списка всех задач всех типов
        ArrayList tasksList = new ArrayList<Task>();
        if (!taskTasks.isEmpty()) {
            for (Integer id : taskTasks.keySet()) {
                tasksList.add(taskTasks.get(id));
            }
            return tasksList;
        }
        return null;
    }

    @Override
    public ArrayList<Epic> getListAllEpics() {
        ArrayList epicsList = new ArrayList<Epic>();
        if (!epicTasks.isEmpty()) {
            for (Integer id : epicTasks.keySet()) {
                epicsList.add(epicTasks.get(id));
            }
            return epicsList;
        }
        return null;
    }

    @Override
    public List<Subtask> getListAllSubtasks() {
        ArrayList subtasksList = new ArrayList<Epic>();
        if (!subtaskTasks.isEmpty()) {
            for (Integer id : subtaskTasks.keySet()) {
                subtasksList.add(subtaskTasks.get(id));
            }
            return subtasksList;
        }
        return null;
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
        if (!prioritizedTasks.isEmpty()) {
            prioritizedTasks.clear();
            getPrioritizedTasks().clear();
        }
    }
    @Override
    public void dellTaskById(int idForDell)  {  //Удаление по идентификатору.
        if (taskTasks.containsKey(idForDell)) {
            taskTasks.remove(idForDell);
            historyManager.remove(idForDell);
            if (prioritizedTasks.contains(getTaskById(idForDell))) {
                prioritizedTasks.remove(getTaskById(idForDell));
                getPrioritizedTasks().remove(getTaskById(idForDell));
            }
        } else if (epicTasks.containsKey(idForDell)) {
            for (Subtask mySubtask : epicTasks.get(idForDell).getMySubtasks()) {
                dellTaskById(mySubtask.getId());
                historyManager.remove(idForDell);
            }
            epicTasks.remove(idForDell);

        } else if (subtaskTasks.containsKey(idForDell)) {
            subtaskTasks.remove(idForDell);
            historyManager.remove(idForDell);
            if (prioritizedTasks.contains(getSubTaskById(idForDell))) {
                prioritizedTasks.remove(getSubTaskById(idForDell));
                getPrioritizedTasks().remove(getSubTaskById(idForDell));
            }
        }

    }

    int makeID() {              // генератор id для задач всех типов
        return ++lastID;
    }
}



