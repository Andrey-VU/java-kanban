package ru.yandex.tasks;
import ru.yandex.tmanager.adapter.LocalDateTimeAdapter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    private String name;                   // название
    private String description;            // описание, в котором раскрываются детали
    private int id;                        // Уникальный идентификационный номер
    private Status status;                 // Статус прогресса работы над задачей
    protected Type type;                      // Тип задачи
    private LocalDateTime startTime;       // время начала задачи (дата с точностью до дня, часов, минут, секунд)
    private Duration duration;             // прогнозная длительность выполнения (в минутах)

    public Task(String name, String description, Integer id, Status status,
                String startTime, long minutes) {

        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        type = Type.TASK;
        this.startTime = LocalDateTime.parse(startTime, LocalDateTimeAdapter.formatter);
        this.duration = Duration.ofMinutes(minutes);
    }

    public Task(String[] fromArray) {
        this.id =  Integer.parseInt(fromArray[0]);
        this.type = Type.valueOf(fromArray[1]);
        this.name = fromArray[2];
        this.status = Status.valueOf(fromArray[3]);
        this.description = fromArray[4];
        this.startTime = LocalDateTime.parse(fromArray[5], LocalDateTimeAdapter.formatter);
        this.duration = Duration.ofMinutes(Long.parseLong(fromArray[6]));
    }

    public Task(Type type, String[] fromArrayEpic) {   // для эпика из массива
        this.id =  Integer.parseInt(fromArrayEpic[0]);
        this.type = type;
        this.name = fromArrayEpic[2];
        this.status = Status.valueOf(fromArrayEpic[3]);
        this.description = fromArrayEpic[4];
    }

    // специализированный конструктор для Эпика и для задач без времени старта
    public Task(String name, String description, Integer id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        type = Type.TASK;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null || duration != null) {
            LocalDateTime endTime = startTime.plusMinutes(duration.toMinutes());
            return endTime;
        } else {
            return null;
        }
    }

    public LocalDateTime getStartTime() {
        if (startTime != null) {
            return startTime;
        } else {
            return null;
        }
    }

    public DateTimeFormatter getFormatter() {
        return LocalDateTimeAdapter.formatter;
    }

    public Duration getDuration() {
        if (duration != null) {
            return duration;
        } else {
            return null;
        }
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
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
                '}';
    }

    public String getEpicId() {
        return "";
    }
}
