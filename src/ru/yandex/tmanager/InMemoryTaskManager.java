package ru.yandex.tmanager;
import ru.yandex.exceptions.IntersectionException;
import ru.yandex.tasks.*;

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
    public boolean findIntersection(Task newTask) {
        // если список ранжированных задач пуст - возвращает false = разрешает создать задачу...
        if (getPrioritizedTasks() != null && getPrioritizedTasks().isEmpty()){
            return false;
        } else if (newTask.getStartTime() == null) {   // если время старта newT не задано
            return false;
        } else {
            for (Task prioritizedTask : getPrioritizedTasks()) {
                if (prioritizedTask.getStartTime() == null) {
                    return false;
                } else if (newTask.getStartTime().isAfter(prioritizedTask.getEndTime()) ||
                            newTask.getEndTime().isBefore(prioritizedTask.getStartTime())) {
                    return false;
                    }
            }
        }
        // иначе нельзя создавать задачу!
        return true;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List prioritizedList = new ArrayList<>();
        if (!prioritizedTasks.isEmpty()) {
            for (Task prioritizedTask : prioritizedTasks) {
                prioritizedList.add(prioritizedTask);
            }
        }
        return prioritizedList;
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
            try {
                if (findIntersection(subtask) == false) {      // если пересечения не найдены
                    if (getPrioritizedTasks() != null) {          // если список приоритетных задач существует
                        prioritizedTasks.add(subtask);       // добавляем новую задачу в список приоритетов
                                                                  // рарзрешаем создать новую задачу
                        int uniqSubtaskId = makeID();                  // присвоили подзадаче уникальный id
                        subtask.setId(uniqSubtaskId);               // присвоили подзадаче уникальный id
                        if (getEpicById(subtask.getEpicID()) != null) {
                            getEpicById(subtask.getEpicID()).setMySubtask(subtask);         // отправить подзадачу в эпик
                            subtaskTasks.put(uniqSubtaskId, subtask);   // записали  подзадачу в хранилище
                            statusChecker(getEpicById(subtask.getEpicID()));       // проверить статусы всех субтасков, входящих в Эпик,
                            //   скорректировать статус эпика, если необходимо
                        }
                    }
                } else {
                    throw new IntersectionException("Конфликт времени исполнения! Задача не может быть добавлена");
                }
            } catch (IntersectionException exception) {
                System.out.println(exception.getMessage());
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

            try {
                if (findIntersection(subtask) == false) {  // проверяем наличие пересечений!!!
                    prioritizedTasks.add(subtask);
                    getEpicById(subtask.getEpicID()).getMySubtasks().remove(getSubTaskById(subtask.getId()));
                    dellTaskById(subtask.getId());   // очищаем список подзадач эпика и главное хранилище подзадач
                    getEpicById(subtask.getEpicID()).setMySubtask(subtask);         // отправить подзадачу в эпик
                    subtaskTasks.put(idForUpdate, subtask);

                } else {
                    throw new IntersectionException("Конфликт времени исполнения! Задача не может быть добавлена");
                }
            } catch (IntersectionException exception) {
                System.out.println(exception.getMessage());
            }
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
            if (task.getName() != null || task.getDescription() != null || task.getStatus() != null) {

                try {
                    if (findIntersection(task) == false) {   // проверяем наличие пересечений!!!
                        if (getPrioritizedTasks() != null) {
                            prioritizedTasks.add(task);

                            int uniqueId = makeID();
                            task.setId(uniqueId);
                            taskTasks.put(uniqueId, task);   // сохранили объект в хранилище
                        }
                    } else {
                        throw new IntersectionException("Конфликт времени исполнения! Задача не может быть добавлена");
                    }
                } catch (IntersectionException exception) {
                    System.out.println(exception.getMessage());
                }
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
            try {
                prioritizedTasks.remove(idForUpdate);
                getPrioritizedTasks().remove(idForUpdate);
                if (findIntersection(newTask) == false) {  // проверяем наличие пересечений!!!
                    prioritizedTasks.add(newTask);
                    taskTasks.put(idForUpdate, newTask);
                } else {
                    throw new IntersectionException("Конфликт времени исполнения! Задача не может быть добавлена");
                }
            } catch (IntersectionException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
    @Override
    public void clearTask()  {                                        // Очистка списка всех задач ru.yandex.tasks.Task
        if (!taskTasks.isEmpty()) {
            taskTasks.clear();
            for (Task prioritizedTask : prioritizedTasks) {
                if (prioritizedTask.type.equals(Type.TASK)) {
                    prioritizedTasks.remove(prioritizedTask);
                }
            }
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
        if (!prioritizedTasks.isEmpty()) {
            prioritizedTasks.clear();
        }
    }
    @Override
    public void dellTaskById(int idForDell)  {  //Удаление по идентификатору.
        if (taskTasks.containsKey(idForDell)) {
            taskTasks.remove(idForDell);
            historyManager.remove(idForDell);
            if (prioritizedTasks.contains(getTaskById(idForDell))) {
                prioritizedTasks.remove(getTaskById(idForDell));
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
            }
        }

    }

    int makeID() {              // генератор id для задач всех типов
        return ++lastID;
    }

//    String toStringForFile(Task task) {
//        return task.getId() + "," + task.getType() + "," + task.getName() +  "," + task.getStatus() + ","
//                + task.getDescription() + "," + task.getStartTime()  + "," + task.getDuration();
//    }
}



