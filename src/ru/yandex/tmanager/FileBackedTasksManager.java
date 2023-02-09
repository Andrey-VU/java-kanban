package ru.yandex.tmanager;
import ru.yandex.exceptions.ManagerSaveException;
import ru.yandex.tasks.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public static void main(String[] args) throws IOException {
        File file = new File("storage.csv");
        // TaskManager newManager = Managers.getFileBackedManager();
        TaskManager recoveredFromFile = loadFromFile(file);

        if (recoveredFromFile != null) {
            // recoveredFromFile.dellThemAll();
            recoveredFromFile.getSubTaskById(3);
            recoveredFromFile.dellTaskById(3);
            recoveredFromFile.getTaskById(6);
            recoveredFromFile.dellTaskById(6);
            recoveredFromFile.getSubTaskById(4);
            recoveredFromFile.getEpicById(2);   // если запрашивать getEpicById всё ломается
            recoveredFromFile.getTaskById(7);
            recoveredFromFile.getTaskById(1);
            recoveredFromFile.getSubTaskById(5);
        }
    }

    private void save() throws ManagerSaveException {             // сохранение изменений в файл
        Map<Integer, Task> tmpStorage = new HashMap<>();
        for (Task value :  getTaskTasks().values()) {
            tmpStorage.put(value.getId(), value);
        }
        for (Epic value : getEpicTasks().values()) {
            tmpStorage.put(value.getId(), value);
        }
        for (Subtask value : getSubtaskTasks().values()) {
            tmpStorage.put(value.getId(), value);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("storageNEW.csv")) ) {  //OUT
            bw.write("id,type,name,status,description,epic");
            bw.newLine();
            for (Task value : tmpStorage.values()) {
                bw.write(toStringForFile(value));
                bw.newLine();
            }
            bw.newLine();
            bw.write(historyToString(getHistoryManager()));

        } catch (IOException exception) {
            throw new ManagerSaveException("Во время записи файла произошла ошибка!", exception);
        }
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (Task task : manager.getHistory()) {
            builder.append(task.getId());
            count++;
            if (count < manager.getHistory().size()) {              // проверяем нужно ли добавлять ","
                builder.append(",");
            }
        }
        return builder.toString();
    }

    @Override
    String toStringForFile(Task task) {
        return super.toStringForFile(task);
    }


    // =============================== ЗАГРАЗКА ИЗ ФАЙЛА =====================================
    static FileBackedTasksManager loadFromFile(File file) throws IOException {
        TaskManager loadedFromFile = Managers.getFileBackedManager();
        List<String> tmp = new ArrayList<>();
        String isHistory = "";
        try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                String line = isHistory + br.readLine();
                if (!line.isBlank()) {
                    tmp.add(line);
                } else {
                    isHistory = "It is history:";
                }
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }

        for (int i = 1; i < tmp.size(); i++) {
            if (tmp != null) {
                if (!tmp.get(i).isBlank() && !tmp.get(i).contains("It is history:"))  {
                    String[] tmpArray = tmp.get(i).split(",");
                    switch (Type.valueOf(tmpArray[1])) {
                        case TASK:
                            Task tmpTask = fromString(tmp.get(i));
                            loadedFromFile.makeNewTask(tmpTask);
                            break;
                        case EPIC:
                            Epic tmpEpic = (Epic) fromString(tmp.get(i));
                            loadedFromFile.makeNewEpic(tmpEpic);
                            break;
                        case SUBTASK:
                            Subtask tmpSubtask = (Subtask) fromString(tmp.get(i));
                            loadedFromFile.makeNewSubtask(tmpSubtask);
                            break;
                    }
                } else if (tmp.get(i).isBlank()) {
                    continue;
                } else {
                    String historyIdsFromTask = tmp.get(i).substring(isHistory.length());
                    for (Integer id : historyFromString(historyIdsFromTask)) {
                                loadedFromFile.getTaskById(id);
                                loadedFromFile.getEpicById(id);
                                loadedFromFile.getSubTaskById(id);
                        }
                    }
                }
            }
        return (FileBackedTasksManager) loadedFromFile;
    }

    static Task fromString(String s) {         // создание задачи из строки
        String[] tmpArray = s.split(",");
        for (int i = 0; i < tmpArray.length; i++) {
            if (tmpArray.length == 5) {
                if (Type.valueOf(tmpArray[1]).equals("TASK")) {
                    Task taskFromFile = new Task(tmpArray);
                    return taskFromFile;
                } else {
                    Epic taskFromFile = new Epic(tmpArray);
                    return taskFromFile;
                }
            } else if (tmpArray.length == 6) {
                Subtask taskFromFile = new Subtask(tmpArray);
                return taskFromFile;
            }
        }
        return null;
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> listOfId = new ArrayList<>();
        if (value.length() > 2) {
            for (String s : value.split(",")) {
                listOfId.add(Integer.parseInt(s));
            }
        } else {listOfId.add(Integer.parseInt(value));}
        return listOfId;
    }

    // ========================================= ПЕРЕОПРЕДЕЛЁННЫЕ МЕТОДЫ =============================
    @Override
    public Epic getEpicById(int idForSearch) {
        Epic tmpEpic = super.getEpicById(idForSearch);
        save();
        return tmpEpic;
    }

    @Override
    public Subtask getSubTaskById(int idForSearch) {
        Subtask tmpSubtask = super.getSubTaskById(idForSearch);
        save();
        return tmpSubtask;
    }

    @Override
    public Task getTaskById(int idForSearch) {
        Task tmpTask = super.getTaskById(idForSearch);
        save();
        return tmpTask;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    @Override
    public void updateEpic(int idForUpdate, Epic epic)  {
        super.updateEpic(idForUpdate, epic);
        save();
    }

    @Override
    public ArrayList<Subtask> getListSubtasksOfEpic(Epic epic) {
        return super.getListSubtasksOfEpic(epic);
    }

    @Override
    public void dellAllEpic()  {
        super.dellAllEpic();
        save();
    }

    @Override
    public void makeNewTask(Task task)  {
        super.makeNewTask(task);
        save();
    }

    @Override
    public void makeNewSubtask(Subtask subtask)  {
        super.makeNewSubtask(subtask);
        save();
    }

    @Override
    public void makeNewEpic(Epic epic) {
        super.makeNewEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(int idForUpdate, Subtask subtask)  {
        super.updateSubtask(idForUpdate, subtask);
        save();
    }

    @Override
    public void statusChecker(Epic newEpic) {
        super.statusChecker(newEpic);
    }

    @Override
    public void updateTask(int idForUpdate, Task newTask)  {
        super.updateTask(idForUpdate, newTask);
        save();
    }

    @Override
    public void clearTask()  {
        super.clearTask();
        save();
    }

    @Override
    public ArrayList<Task> getListAllTasksFromTask() {
        return super.getListAllTasksFromTask();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public void printHistory()  {
        super.printHistory();
        save();
    }

    @Override
    public ArrayList<Object> getListAllTasks() {
        return super.getListAllTasks();
    }

    @Override
    public void dellThemAll()  {
        super.dellThemAll();
        save();
    }

    @Override
    public void dellTaskById(int idForDell) {
        super.dellTaskById(idForDell);
        save();
    }

    @Override
    int makeID() {
        return super.makeID();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}