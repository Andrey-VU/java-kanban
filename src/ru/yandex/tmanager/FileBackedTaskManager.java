package ru.yandex.tmanager;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    static void main(String[] args) {
            /* реализуйте небольшой сценарий: Заведите несколько разных задач, эпиков и подзадач.
Запросите некоторые из них, чтобы заполнилась история просмотра.
Создайте новый FileBackedTasksManager менеджер из этого же файла.
Проверьте, что история просмотра восстановилась верно и все задачи, эпики, подзадачи,
которые были в старом, есть в новом менеджере.
     */

    }

    static FileBackedTaskManager loadFromFile(File file) {   // восстанавливать данные менеджера из файла при запуске
        return null;
    }



    Task fromString(String value) {         // создание задачи из строки
        return null;
    }
        //  =========  для сохранения и восстановления менеджера истории из CSV  =======
    static String historyToString(HistoryManager manager) {
        return null;
    }
    static List<Integer> historyFromString(String value) {
        return null;
    }

    // === Напишите метод сохранения задачи в строку  или переопределите базовый.
    //
    String toString(Task task) {
        return null;
    }

    private void save() throws IOException {                        // сохранение изменений в файл
        try (FileWriter fileWriter = new FileWriter("/path")) {
            fileWriter.write("id,type,name,status,description,epic" + "\n");
            int cntr = 0;
            for (Integer id : taskManager.getTaskTasks().keySet()) {
                Task tmpTask = taskManager.getTaskTasks().get(id);
                fileWriter.write(++cntr + "," + tmpTask.getType() + "," + tmpTask.getName() + ","
                        + tmpTask.getStatus() + "," + tmpTask.getDescription() + "\n");
                //1,TASK,Task1,NEW,Description task1,
            }

        } catch (FileNotFoundException e) {
            System.out.println("Произошла ошибка во время записи файла.");
        }
    }
/*
Проверка работы нового менеджера
Исключения вида IOException нужно отлавливать внутри метода save и кидать собственное непроверяемое исключение
ManagerSaveException. Благодаря этому можно не менять сигнатуру методов интерфейса менеджера.

id,type,name,status,description,epic
1,TASK,Task1,NEW,Description task1,
2,EPIC,Epic2,DONE,Description epic2,
3,SUBTASK,Sub Task2,DONE,Description sub task3,2

2,3

Сначала через запятую перечисляются все поля задач. Ниже находится список задач, каждая из них записана с новой строки.
Дальше — пустая строка, которая отделяет задачи от истории просмотров. И заключительная строка — это идентификаторы
задач из истории просмотров.
Файл из нашего примера можно прочитать так: в трекер добавлены задача, эпик и подзадача. Эпик и подзадача просмотрены
и выполнены. Задача осталась в состоянии новой и не была просмотрена.
*/


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
    public void updateEpic(int idForUpdate, Epic epic) {
        super.updateEpic(idForUpdate, epic);
        save();
    }

    @Override
    public ArrayList<Subtask> getListSubtasksOfEpic(Epic epic) {
        return super.getListSubtasksOfEpic(epic);
    }

    @Override
    public void dellAllEpic() {
        super.dellAllEpic();
        save();
    }

    @Override
    public void makeNewSubtask(Subtask subtask) {
        super.makeNewSubtask(subtask);
        save();
    }

    @Override
    public Subtask getSubTaskById(int idForSearch) {
        return super.getSubTaskById(idForSearch);
    }

    @Override
    public void updateSubtask(int idForUpdate, Subtask subtask) {
        super.updateSubtask(idForUpdate, subtask);
        save();
    }

    @Override
    public void statusChecker(Epic newEpic) {
        super.statusChecker(newEpic);
    }

    @Override
    public void makeNewTask(Task task) {
        super.makeNewTask(task);
        save();
    }

    @Override
    public Task getTaskById(int idForSearch) {
        return super.getTaskById(idForSearch);
    }

    @Override
    public void updateTask(int idForUpdate, Task newTask) {
        super.updateTask(idForUpdate, newTask);
        save();
    }

    @Override
    public void clearTask() {
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
    public void printHistory() {
        super.printHistory();
        save();
    }

    @Override
    public ArrayList<Object> getListAllTasks() {
        return super.getListAllTasks();
    }

    @Override
    public void dellThemAll() {
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}




