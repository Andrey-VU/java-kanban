package ru.yandex.tasks;

import java.util.Objects;

public class Task {
    private String name;                   // название
    private String description;            // описание, в котором раскрываются детали
    private int id;                        // Уникальный идентификационный номер
    private Status status;                 // Статус прогресса работы над задачей

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
