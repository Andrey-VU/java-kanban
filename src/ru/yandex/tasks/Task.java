package ru.yandex.tasks;
import java.util.Objects;

import static ru.yandex.tasks.Type.TASK;

public class Task {

    private String name;                   // название
    private String description;            // описание, в котором раскрываются детали
    private int id;                        // Уникальный идентификационный номер
    private Status status;                 // Статус прогресса работы над задачей
    public Type type;                      // Тип задачи

    public Task(String name, String description, Integer id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        type = Type.TASK;
    }

    public Task(String[] fromArray) {
        this.id =  Integer.parseInt(fromArray[0]);
        this.type = Type.valueOf(fromArray[1]);
        this.name = fromArray[2];
        this.status = Status.valueOf(fromArray[3]);
        this.description = fromArray[4];
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public void setType(Type type) {
        this.type = type;
    }
    public Type getType() {
        return type;
    }
    public int getId() {
        return id;
    }
    public Status getStatus() {
        return status;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {

        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
