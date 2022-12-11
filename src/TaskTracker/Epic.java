package TaskTracker;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> mySubtasks = new ArrayList<>();
    private ArrayList<Integer> mySubtasksId = new ArrayList<>();
    private int myEpicId;
    private String myEpicStatus;

    public Epic(String nameOfTask, String descriptionOfTask, Integer id, String status) {
        super(nameOfTask, descriptionOfTask, id, status);
        this.myEpicId = id;
        this.myEpicStatus = status;
    }

    public int getMyEpicID() {
        return myEpicId;
    }
    public void setMyEpicId(int uniqueEpicId) {
        myEpicId = uniqueEpicId;
    }
    public void setMyStatus(String status) {
        myEpicStatus = status;
    }
    /*public String getStatusOfMySubtask(int idOfSubtask){
        return mySubtasks.get(idOfSubtask).getMyStatus();
    }*/
    public String getMyEpicStatus() {
        return myEpicStatus;
    }
    public void setMySubtasksId(int idOfSubTask) {
        mySubtasksId.add(idOfSubTask);
    }
    /*public ArrayList<Integer> getMySubtasksId() {
        return mySubtasksId;
    }*/
    public void setMySubtask(Subtask newSubtask) {
        mySubtasks.add(newSubtask);
    }

    public ArrayList<Subtask> getMySubtasks() {
        return mySubtasks;
    }
}
