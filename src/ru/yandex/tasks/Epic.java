import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> mySubtasks = new ArrayList<>();
    public Epic(String nameOfTask, String descriptionOfTask, Integer id, String status) {
        super(nameOfTask, descriptionOfTask, id, status);
    }

    public void setMySubtask(Subtask newSubtask) {
        mySubtasks.add(newSubtask);
    }
    public ArrayList<Subtask> getMySubtasks() {
        return mySubtasks;
    }
}
