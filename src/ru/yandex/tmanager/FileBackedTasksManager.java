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
    public FileBackedTasksManager(File file) {
       this.file = file;     }

    public static void main(String[] args) {
        File file = new File("storage.csv");
        FileBackedTasksManager loadedFromFile = loadFromFile(file);

         if (loadedFromFile != null) {
            loadedFromFile.dellTaskById(3);
            loadedFromFile.getTaskTasks().get(6);
            loadedFromFile.getHistory().toString();
            loadedFromFile.dellTaskById(6);
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

        try (Writer fileWriter = new FileWriter("storageOUT.csv")) {
            fileWriter.write("id,type,name,status,description,epic" + "\n");
            for (Task value : tmpStorage.values()) {
                fileWriter.write(toStringForFile(value)+ "\n");
            }
            fileWriter.write("\n" + "там выше была пустая строка, а там ниже так и не появляется история");
            fileWriter.write(historyToString(getHistoryManager()));

        } catch (IOException exception) {
            throw new ManagerSaveException("Во время записи файла произошла " +
                    "ошибка!", exception);
        }
    }

    static String historyToString(HistoryManager manager) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < manager.getHistory().size(); i++) {
            builder.append(manager.getHistory());
        }


        for (Task task : manager.getHistory()  ) {
            builder.append(task.getId());
            builder.append(",");
        }
        return builder.toString();
    }

    @Override
    String toStringForFile(Task task) {
        return super.toStringForFile(task);
    }


// =============================== ЗАГРАЗКА ИЗ ФАЙЛА =====================================
    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager loadedFromFile = new FileBackedTasksManager(file);
        List<String> tmp = new ArrayList<>();
        try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                String line = br.readLine();
                tmp.add(line);
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }
        for (int i = 1; i < tmp.size(); i++) {
            if ((loadedFromFile != null)) {
                if (!tmp.get(i).isBlank()) {
                    if (tmp.get(i).split(",")[1].equals("TASK")) {
                        Task tmpTask = fromString(tmp.get(i));
                        if (tmpTask != null) {
                            loadedFromFile.getTaskTasks().put(tmpTask.getId(), tmpTask);
                        }
                    } else if (tmp.get(i).split(",")[1].equals("EPIC")) {
                        Epic tmpEpic = (Epic) fromString(tmp.get(i));
                        if (tmpEpic != null){
                            loadedFromFile.getEpicTasks().put(tmpEpic.getId(), tmpEpic);}
                    } else if (tmp.get(i).split(",")[1].equals("SUBTASK")) {
                        Subtask tmpSubtask = (Subtask) fromString(tmp.get(i));
                        if (tmpSubtask != null) {
                            loadedFromFile.getSubtaskTasks().put(tmpSubtask.getId(), tmpSubtask);}
                    } else {
                        for (Integer id : historyFromString(tmp.get(i))) {
                            if (loadedFromFile.getTaskTasks().keySet().contains(id)) {
                                loadedFromFile.getHistoryManager().add(loadedFromFile.getTaskById(id));
                            } else if (loadedFromFile.getEpicTasks().keySet().contains(id)) {
                                loadedFromFile.getHistoryManager().add(loadedFromFile.getEpicById(id));
                            } else {
                                loadedFromFile.getHistoryManager().add(loadedFromFile.getSubTaskById(id));
                            }
                        }
                    }
                }
            }
        }
        return loadedFromFile;
    }

    static Task fromString(String s) {         // создание задачи из строки
        for (int i = 0; i < s.split(",").length; i++) {
            if (s.split(",").length == 5) {
                int id = Integer.parseInt(s.split(",")[0]);
                Type type = (s.split(",")[1].equals("TASK") ? Type.TASK
                        : s.split(",")[1].equals("EPIC") ? Type.EPIC : Type.SUBTASK);
                String name = s.split(",")[2];
                Status status = (s.split(",")[3].equals("NEW") ? Status.NEW
                        : (s.split(",")[3].equals("DONE") ? Status.DONE : Status.IN_PROGRESS));
                String description = s.split(",")[4];

                if (type.equals("TASK")) {
                    Task taskFromFile = new Task(name, description, id, status);
                    return taskFromFile;
                } else {
                    Epic taskFromFile = new Epic(name, description, id, status);
                    return taskFromFile;
                }

            } else if (s.split(",").length == 6) {
                int id = Integer.parseInt(s.split(",")[0]);
                Type type = (s.split(",")[1].equals("TASK") ? Type.TASK
                        : s.split(",")[1].equals("EPIC") ? Type.EPIC : Type.SUBTASK);
                String name = s.split(",")[2];
                Status status = (s.split(",")[3].equals("NEW") ? Status.NEW
                        : (s.split(",")[3].equals("DONE") ? Status.DONE : Status.IN_PROGRESS));
                String description = s.split(",")[4];
                int myEpicId = Integer.parseInt(s.split(",")[5]);
                Subtask taskFromFile = new Subtask(name, description, id, status, myEpicId);
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
    public void makeNewEpic(Epic epic) {
        super.makeNewEpic(epic);
        save();
    }

    @Override
    public Epic getEpicById(int idForSearch) {
        return super.getEpicById(idForSearch);

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
    public void makeNewSubtask(Subtask subtask)  {
        super.makeNewSubtask(subtask);
        save();
    }

    @Override
    public Subtask getSubTaskById(int idForSearch) {
        return super.getSubTaskById(idForSearch);
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
    public void makeNewTask(Task task)  {
        super.makeNewTask(task);
        save();
    }

    @Override
    public Task getTaskById(int idForSearch) {
        return super.getTaskById(idForSearch);
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




