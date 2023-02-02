package ru.yandex.tasks;

import static ru.yandex.tasks.Type.SUBTASK;

public class Subtask extends Task {
    private int epicId;    // переменная для id верхнеуровневой задачи


    public Subtask(String nameOfTask, String descriptionOfTask, Integer id, Status status, Integer epicId) {
        super(nameOfTask, descriptionOfTask, id, status);
        this.epicId = epicId;
        type = Type.SUBTASK;
    }

    public Subtask(String[] fromArray) {
        super(fromArray);
        this.epicId = Integer.parseInt(fromArray[5]);
    }

    public int getEpicID() {
        return epicId;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "," + epicId;
    }

}
