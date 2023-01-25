package ru.yandex.tmanager;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Status;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static FileBackedTasksManager loadedFromFile;
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    public static void main(String[] args) throws IOException {
        File file = new File("storage.csv");
        loadedFromFile = loadFromFile(file);
    }

/* реализуйте небольшой сценарий: Заведите несколько разных задач, эпиков и подзадач.
Запросите некоторые из них, чтобы заполнилась история просмотра.
Создайте новый FileBackedTasksManager менеджер из этого же файла.
Проверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи,
которые были в старом, есть в новом менеджере.
     */


     static FileBackedTasksManager loadFromFile(File file) throws IOException {   // восстанавливать
        // данные менеджера из файла при запуске
        List<String> tmp = new ArrayList<>();
        FileReader reader = new FileReader(file);
        try (BufferedReader br = new BufferedReader(reader)) {
            while (br.ready()) {
                String line = br.readLine();
                tmp.add(line);
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }
        for (int i = 1; i < tmp.size(); i++) {
            if ((loadedFromFile != null)) {
                if (tmp.get(i).split(",")[1].equals("TASK")) {
                    loadedFromFile.makeNewTask(fromString(tmp.get(i)));
                } else if (tmp.get(i).split(",")[1].equals("EPIC")) {
                    loadedFromFile.makeNewEpic((Epic) fromString(tmp.get(i)));
                } else if (tmp.get(i).split(",")[1].equals("SUBTASK")) {
                    loadedFromFile.makeNewSubtask((Subtask) fromString(tmp.get(i)));
                } else if (!tmp.get(i).isBlank()) {
                    for (Integer idForHistory : historyFromString(tmp.get(i))) {
                        loadedFromFile.historyManager.linkLast(loadedFromFile.getTaskById(idForHistory));
                    }
                }
            }
        }
        return loadedFromFile;
    }

    //  =========  для сохранения и восстановления менеджера истории из CSV  =======
    static String historyToString(HistoryManager manager) {
        StringBuilder builder = new StringBuilder();
        for (Task task : manager.getHistory()) {
            builder.append(task.getId() + ",");
        }
        builder.deleteCharAt(builder.lastIndexOf(","));
        String stringHistory = builder.toString();
        return stringHistory;
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


    static Task fromString(String s) {         // создание задачи из строки
        Task taskFromFile = null;
        for (int i = 0; i < s.split(",").length; i++) {
            if (s.split(",").length == 5) {
                int id = Integer.parseInt(s.split(",")[0]);
                String type = s.split(",")[1];
                String name = s.split(",")[2];
                Status status = (s.split(",")[3].equals("NEW") ? Status.NEW
                        : (s.split(",")[3].equals("DONE") ? Status.DONE : Status.IN_PROGRESS));
                String description = s.split(",")[4];
                taskFromFile = new Task(name, description, id, status);

            } else if (s.split(",").length == 6) {
                int id = Integer.parseInt(s.split(",")[0]);
                String type = s.split(",")[1];
                String name = s.split(",")[2];
                Status status = (s.split(",")[3].equals("NEW") ? Status.NEW
                        : (s.split(",")[3].equals("DONE") ? Status.DONE : Status.IN_PROGRESS));
                String description = s.split(",")[4];
                int myEpicId = Integer.parseInt(s.split(",")[5]);
                taskFromFile = new Subtask(name, description, id, status, myEpicId);
                return taskFromFile;
            }
        }
        return taskFromFile;
    }

    private void save() throws IOException {                        // сохранение изменений в файл
        List<Task> tmpStorage = new ArrayList<>();
        for (Task value : taskManager.getTaskTasks().values()) {
            tmpStorage.add(value);
        }
        for (Epic value : taskManager.getEpicTasks().values()) {
            tmpStorage.add(value);
        }
        for (Subtask value : taskManager.getSubtaskTasks().values()) {
            tmpStorage.add(value);
        }

        try (FileWriter fileWriter = new FileWriter("storage.txt")) {

            fileWriter.write("id,type,name,status,description,epic" + "\n");
            int counter = 0;
            for (Task taskFromStorage : tmpStorage) {
                switch (taskFromStorage.getType()){
                    case TASK:
                    case SUBTASK:
                        fileWriter.write(++counter + "," + taskFromStorage.getType() + ","
                                + taskFromStorage.getName() + "," + taskFromStorage.getStatus() + ","
                                + taskFromStorage.getDescription() + "\n");
                        break;
                    case EPIC:
                        fileWriter.write(++counter + "," + taskFromStorage.getType() + ","
                                + taskFromStorage.getName() + "," + taskFromStorage.getStatus() + ","
                                + taskFromStorage.getDescription()
                                + taskManager.getEpicTasks().get(taskFromStorage.getId()).getMySubtasks() + "\n");
                        break;
                }
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(historyManager));

        } catch (FileNotFoundException e) {
            System.out.println("Произошла ошибка во время записи файла.");
        }
    }

/* Исключения вида IOException нужно отлавливать внутри метода save и кидать собственное непроверяемое исключение
ManagerSaveException. Благодаря этому можно не менять сигнатуру методов интерфейса менеджера.

try(IOException ex){ throw new ManagerSaveException("custom message", ex);}

ManagerSaveException extends RuntimeException */

    @Override
    public void makeNewEpic(Epic epic) throws IOException {
        super.makeNewEpic(epic);
        save();
    }

    @Override
    public Epic getEpicById(int idForSearch) {
        return super.getEpicById(idForSearch);
    }

    @Override
    public void updateEpic(int idForUpdate, Epic epic) throws IOException {
        super.updateEpic(idForUpdate, epic);
        save();
    }

    @Override
    public ArrayList<Subtask> getListSubtasksOfEpic(Epic epic) {
        return super.getListSubtasksOfEpic(epic);
    }

    @Override
    public void dellAllEpic() throws IOException {
        super.dellAllEpic();
        save();
    }

    @Override
    public void makeNewSubtask(Subtask subtask) throws IOException {
        super.makeNewSubtask(subtask);
        save();
    }

    @Override
    public Subtask getSubTaskById(int idForSearch) {
        return super.getSubTaskById(idForSearch);
    }

    @Override
    public void updateSubtask(int idForUpdate, Subtask subtask) throws IOException {
        super.updateSubtask(idForUpdate, subtask);
        save();
    }

    @Override
    public void statusChecker(Epic newEpic) {
        super.statusChecker(newEpic);
    }

    @Override
    public void makeNewTask(Task task) throws IOException {
        super.makeNewTask(task);
        save();
    }

    @Override
    public Task getTaskById(int idForSearch) {
        return super.getTaskById(idForSearch);
    }

    @Override
    public void updateTask(int idForUpdate, Task newTask) throws IOException {
        super.updateTask(idForUpdate, newTask);
        save();
    }

    @Override
    public void clearTask() throws IOException {
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
    public void printHistory() throws IOException {
        super.printHistory();
        save();
    }

    @Override
    public ArrayList<Object> getListAllTasks() {
        return super.getListAllTasks();
    }

    @Override
    public void dellThemAll() throws IOException {
        super.dellThemAll();
        save();
    }

    @Override
    public void dellTaskById(int idForDell) throws IOException {
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }
    // === Напишите метод сохранения задачи в строку, или переопределите базовый.
    //   String toString(Task task) {
    //        return null;
    //    }  // или переопределите базовый!?
}




