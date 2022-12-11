package TaskTracker;

public class Subtask extends Task {
    private int myEpicId;    // переменная для id верхнеуровневой задачи
    private int myId;   // уникальный id сабтаска
    private String myStatus;                // статус подзадачи

    public Subtask(String nameOfTask, String descriptionOfTask, Integer id, String status, Integer myEpicId) {
        super(nameOfTask, descriptionOfTask, id, status);
        this.myEpicId = myEpicId;
        this.myStatus = status;
    }
    public int getIdOfMyEpic() {
        return myEpicId;
    }

    public void setSubtaskId(int subtaskId) {
        myId = subtaskId;
    }

    public int getIdOfSubtask() {
        return myId;
    }

    public String getMyStatus() {
        return myStatus;
    }

}
