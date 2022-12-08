package TaskTracker;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> mySubtasksID = new ArrayList<>();

    public Epic(Integer id, String nameOfTask, String descriptionOfTask, String status) {
        super(id, nameOfTask, descriptionOfTask, status);
        //mySubtasksID.add(subtaskId);

    }
}

/*
Каждый эпик знает, какие подзадачи в него входят.
Завершение всех подзадач эпика считается завершением эпика.

 */