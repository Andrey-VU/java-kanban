package ru.yandex.tasks;

import java.time.Duration;

public class Subtask extends Task {
    private int epicId;    // переменная для id верхнеуровневой задачи

    public Subtask(String nameOfTask, String descriptionOfTask, Integer id, Status status, Integer epicId,
                   String startTime, long minutes) {
        super(nameOfTask, descriptionOfTask, id, status, startTime, minutes);
        this.epicId = epicId;
        type = Type.SUBTASK;
    }

    public Subtask(String[] fromArray) {
        super(fromArray);
        this.epicId = Integer.parseInt(fromArray[7]);
    }



    public int getEpicID() {
        return epicId;
    }

//    @Override
//    public String getDescription() {
//        return super.getDescription() + "," + epicId;
//    }



    @Override
    public String getEpicId() {
        return "," + getEpicID();
    }
}
