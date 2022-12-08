package TaskTracker;
import java.util.ArrayList;

public class Task {
    String nameOfTask;                   // название
    String descriptionOfTask;            // описание, в котором раскрываются детали
    Integer id;                      // Уникальный идентификационный номер
    String status;                       // Статус прогресса работы над задачей

    public Task(Integer id, String nameOfTask, String descriptionOfTask, String status) {
        this.nameOfTask = nameOfTask;
        this.descriptionOfTask = descriptionOfTask;
        this.id = id;
        this.status = status;
    }

    public void aboutTask() {    // служебная информация
        System.out.println("Я класс TaskTracker.Task. Что я делаю? " + "\n"
                + " Я задаю: структуру хранения информации о любых здачах;" + "\n"
                + " Я храню информацию о простых задачах (без подзадач, и без верхнеуровневых задач)" );
    }
}
