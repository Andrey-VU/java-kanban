package ru.yandex.tasks;

import ru.yandex.tasks.Task;

import java.util.ArrayList;

import static ru.yandex.tasks.Type.EPIC;

public class Epic extends Task {
    private ArrayList<Subtask> mySubtasks = new ArrayList<>();
    Type type;
    public Epic(String nameOfTask, String descriptionOfTask, Integer id, Status status) {
        super(nameOfTask, descriptionOfTask, id, status);
        type = Type.EPIC;


    }

    public void setMySubtask(Subtask newSubtask) {
        mySubtasks.add(newSubtask);
    }
    public ArrayList<Subtask> getMySubtasks() {
        return mySubtasks;
    }
}
