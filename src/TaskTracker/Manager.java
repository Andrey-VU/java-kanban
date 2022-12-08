package TaskTracker;
import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    String[] status = {"NEW", "IN_PROGRESS", "DONE"};   // список статусов работы над задачей
    Integer lastID; // здесь хранитися последний, сгенерированный id всех задач

    private HashMap<Integer, Epic> epicTasks = new HashMap<>(); // для хранения всех TaskTracker.Epic задач
    private HashMap<Integer, Subtask> subtaskTasks = new HashMap<>(); // для хранения всех TaskTracker.Subtask задач
    private HashMap<Integer, Task> taskTasks = new HashMap<>(); // для хранения всех TaskTracker.Task задач



    public void updateTask(int id, Task newTask){     // обновляем задачу
        taskTasks.put(id, newTask);
    }

    public void makeTask(String nameOfTask, String descriptionOfTask, String status) {   // новая задача
        int unidueId = makeID();
        Task task = new Task(unidueId, nameOfTask, descriptionOfTask, status);
        taskTasks.put(unidueId, task);           // сохранили объект, содержащий полное описание задачи
    }

    Integer makeID() {  // метод который генерирует id для всех классов, при обращении к нему
        int id = lastID + 1;
        lastID = id;
        return id;
    }


}
   /*
    Для генерации идентификаторов можно использовать числовое поле класса менеджер, увеличивая его на 1,
    когда нужно получить новое значение.

    Менеджер
    Кроме классов для описания задач, вам нужно реализовать класс для объекта-менеджера.
    Он будет запускаться на старте программы и управлять всеми задачами.
    В нём должны быть реализованы следующие функции:
        Возможность хранить задачи всех типов.   Для этого вам нужно выбрать подходящую коллекцию.
        Методы для каждого из типа задач(Задача/Эпик/Подзадача):
        Получение списка всех задач.
        Удаление всех задач.
        Получение по идентификатору.
        Создание. Сам объект должен передаваться в качестве параметра.
        Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        Удаление по идентификатору.

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




