package TaskTracker;

public class Task {
    String nameOfTask;                   // название
    String descriptionOfTask;            // описание, в котором раскрываются детали
    private Integer id;                  // Уникальный идентификационный номер
    private String status;                       // Статус прогресса работы над задачей

    public Task(String nameOfTask, String descriptionOfTask, Integer id, String status) {
        this.nameOfTask = nameOfTask;
        this.descriptionOfTask = descriptionOfTask;
        this.id = id;
        this.status = status;
    }
    public void setTaskId(int uniqueId) {
        id = uniqueId;
    }
}
