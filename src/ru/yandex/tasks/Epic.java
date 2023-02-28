package ru.yandex.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private ArrayList<Subtask> mySubtasks = new ArrayList<>();
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    public Epic(String nameOfTask, String descriptionOfTask, Integer id, Status status) {
        super(nameOfTask, descriptionOfTask, id, status);
        type = Type.EPIC;
        endTime = latestSubtaskEndTime();
        this.startTime = earliestSubtaskStartTime();
        this.duration = totalOfSubDuration();
    }

    public Epic(Type type, String[] fromArrayEpic) {
        super(type, fromArrayEpic);
    }

    private Duration totalOfSubDuration() {
        long storageOfDurationInMinutes = 0;
        for (Subtask mySubtask : mySubtasks) {
            storageOfDurationInMinutes += mySubtask.getDuration().toMinutes();
        }
        return storageOfDurationInMinutes == 0 ? null : Duration.ofMinutes(storageOfDurationInMinutes);
    }

    private LocalDateTime latestSubtaskEndTime() {
        Map<Integer, LocalDateTime> storageOfEndTime = new HashMap<>();
        for (Subtask mySubtask : mySubtasks) {
            storageOfEndTime.put(mySubtask.getId(), mySubtask.getEndTime());
        }
        LocalDateTime latestDataTime = LocalDateTime.MIN;
        for (LocalDateTime value : storageOfEndTime.values()) {
            if (value != null && value.isAfter(latestDataTime)) {
                latestDataTime = value;
            }
        }
        return latestDataTime == LocalDateTime.MIN ? null : latestDataTime;
    }

    public LocalDateTime earliestSubtaskStartTime() {
        Map<Integer, LocalDateTime> storageOfStartTime = new HashMap<>();
        for (Subtask mySubtask : mySubtasks) {
            storageOfStartTime.put(mySubtask.getId(), mySubtask.getStartTime());
        }
        LocalDateTime earliestDataTime = LocalDateTime.MAX;
        for (LocalDateTime value : storageOfStartTime.values()) {
            if (value != null && value.isBefore(earliestDataTime)) {
                earliestDataTime = value;
            }
        }
        return earliestDataTime == LocalDateTime.MAX ? null : earliestDataTime;
    }

    public void setMySubtask(Subtask newSubtask) {
        mySubtasks.add(newSubtask);
    }
    public ArrayList<Subtask> getMySubtasks() {
        if (!mySubtasks.isEmpty()) {
            return mySubtasks;
        }
        return null;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }
}
