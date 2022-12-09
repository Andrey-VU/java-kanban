package TaskTracker;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> mySubtasksId = new ArrayList<>();  // нужен ли здесь? если создать список сабтасков?
    ArrayList<Subtask> mySubtasks = new ArrayList<>();
    private Integer myEpicId;
    private String myEpicStatus;

    public Epic(String nameOfTask, String descriptionOfTask, Integer id, String status,
                ArrayList<Integer> listForSubtasksId) {
        super(nameOfTask, descriptionOfTask, id, status);
        this.mySubtasksId = listForSubtasksId;
        this.myEpicId = id;
        this.myEpicStatus = status;
    }
    public void setMySubtasks(Subtask newSubtask) {
        this.mySubtasks.add(newSubtask);
    }
    public void setMySubtasksID(int idOfSubtask) {
        this.mySubtasksId.add(idOfSubtask);
    }
    public Integer getMyEpicID() {
        return myEpicId;
    }
    public void setMyEpicId(int uniqueEpicId) {
        myEpicId = uniqueEpicId;
    }
    public void setMyStatus(String status) {
        myEpicStatus = status;
    }
}
