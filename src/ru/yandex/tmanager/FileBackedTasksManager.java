package ru.yandex.tmanager;
import ru.yandex.exceptions.ManagerSaveException;
import ru.yandex.tasks.*;
import java.io.*;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final String fileIn;
    private final String fileOut;

    public FileBackedTasksManager(String pathIn, String pathOut) {
        super();
        this.fileIn = pathIn;
        this.fileOut = pathOut;
        super.prioritizedTasks = new TreeSet<>(comparator);
    }

    public FileBackedTasksManager(String pathInOut) {
        super();
        this.fileIn = pathInOut;
        this.fileOut = fileIn;
        super.prioritizedTasks = new TreeSet<>(comparator);
    }

    public FileBackedTasksManager() {
        super();
        super.prioritizedTasks = new TreeSet<>(comparator);
        this.fileIn = "AbstractStorage.csv";
        this.fileOut = fileIn;
    }

    public String getFileOut() {
        return fileOut;
    }

    //public String getFileIn() { return fileIn; }


    public static void main(String[] args) throws IOException, InterruptedException {
        TaskManager recoveredFromFile = loadFromFile("storage.csv", "storage_OUT_NEW.csv");

        if (recoveredFromFile != null) {
            // recoveredFromFile.dellThemAll();
            recoveredFromFile.getSubTaskById(3);
            recoveredFromFile.dellTaskById(3);
            recoveredFromFile.getTaskById(6);
            recoveredFromFile.dellTaskById(6);
            recoveredFromFile.getSubTaskById(4);
            recoveredFromFile.getEpicById(2);
            recoveredFromFile.getTaskById(7);
            recoveredFromFile.getTaskById(1);
            recoveredFromFile.getSubTaskById(5);
        }
    }

    @Override
    public void save() throws ManagerSaveException {             // сохранение изменений в файл
        File fileForSave = new File(getFileOut());                // OUT
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

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileForSave)) ) {  //OUT
            bw.write("id,type,name,status,description,startTime,duration,epic");
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
        if (manager.getHistory() != null) {
            for (Task task : manager.getHistory()) {
                builder.append(task.getId());
                count++;
                if (count < manager.getHistory().size()) {              // проверяем нужно ли добавлять ","
                    builder.append(",");
                }
            }
        }
        return builder.toString();
    }

    // =============================== ЗАГРАЗКА ИЗ ФАЙЛА =====================================
    public static FileBackedTasksManager loadFromFile(String fileIn, String fileOut) throws IOException {
        TaskManager loadedFromFile = new FileBackedTasksManager(fileIn, fileOut);

        List<String> tmp = new ArrayList<>();                // для хранения списка строк из файла
        String isHistory = "";

        try (FileReader reader = new FileReader(fileIn); BufferedReader br = new BufferedReader(reader)) {
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
                    if (!tmp.get(i).isBlank() && !tmp.get(i).contains("It is history:")) {
                        String[] tmpArray = tmp.get(i).split(",");  //строковый массив после деления строки по ","
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
//        for (int i = 0; i < tmpArray.length; i++) {
            if (tmpArray.length == 7) {
                Task taskFromFile = new Task(tmpArray);
                return taskFromFile;
            } else if (tmpArray.length == 8) {
                Subtask taskFromFile = new Subtask(tmpArray);
                return taskFromFile;
            } else {
                Epic taskFromFile = new Epic(Type.EPIC, tmpArray);
                return taskFromFile;
            }
//        }
//        return null;
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> listOfId = new ArrayList<>();
        if (value.length() > 2) {
            for (String s : value.split(",")) {
                listOfId.add(Integer.parseInt(s));
            }
        } else if (value.length() == 0){
            return null;
        }
        else {listOfId.add(Integer.parseInt(value));}
        return listOfId;
    }

    String toStringForFile(Task task) {
        String isStartTimeAndDuration = task.getStartTime() == null ? "" :
                task.getStartTime().format(task.getFormatter()) + "," + task.getDuration().toMinutes();
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + isStartTimeAndDuration + task.getEpicId();
        }

        // ========================================= ПЕРЕОПРЕДЕЛЁННЫЕ МЕТОДЫ =============================

    @Override
    public void dellAllSubtasks() throws IOException, ManagerSaveException {
        super.dellAllSubtasks();
    }

    @Override
    public void dellAllTasks() throws IOException, ManagerSaveException {
        super.dellAllTasks();
    }

    @Override
    public ArrayList<Epic> getListAllEpics() {
        return super.getListAllEpics();
    }

    @Override
    public List<Subtask> getListAllSubtasks() {
        return super.getListAllSubtasks();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

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
        if (super.getListSubtasksOfEpic(epic) != null) {
            return super.getListSubtasksOfEpic(epic);
        }
        return null;
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
    public boolean findIntersection(Task newTask) {
        return super.findIntersection(newTask);
    }

    @Override
    public HashMap<Integer, Epic> getEpicTasks() {
        return super.getEpicTasks();
    }

    @Override
    public HashMap<Integer, Subtask> getSubtaskTasks() {
        return super.getSubtaskTasks();
    }

    @Override
    public HashMap<Integer, Task> getTaskTasks() {
        return super.getTaskTasks();
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
    public ArrayList<Task> getListAllTasks() {
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