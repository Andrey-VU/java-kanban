package TaskTracker;
import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Subtask> mySubtasks = new ArrayList<>();
    private Integer myEpicId;
    private String myEpicStatus;

    public Epic(String nameOfTask, String descriptionOfTask, Integer id, String status,
                ArrayList<Subtask> subtasks) {
        super(nameOfTask, descriptionOfTask, id, status);
        this.mySubtasks = subtasks;
        this.myEpicId = id;
        this.myEpicStatus = status;
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
    public String getStatusOfMySubtask(int idOfSubtask){
        return mySubtasks.get(idOfSubtask).getMyStatus();
    }
    }
