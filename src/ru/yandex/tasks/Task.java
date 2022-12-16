package ru.yandex.tasks;

public class Task {
    private String name;                   // название
    private String description;            // описание, в котором раскрываются детали
    private int id;                      // Уникальный идентификационный номер
    private Status status;               // Статус прогресса работы над задачей

    public Task(String name, String description, Integer id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public int getId() {
        return id;
    }
    public Status getStatus() {
        return status;
    }
}
