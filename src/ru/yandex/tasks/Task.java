public class Task {
    String name;                   // название
    String description;            // описание, в котором раскрываются детали
    private int id;                      // Уникальный идентификационный номер
    private String status;               // Статус прогресса работы над задачей

    public Task(String name, String description, Integer id, String status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getId() {
        return id;
    }
    public String getStatus() {
        return status;
    }
}
