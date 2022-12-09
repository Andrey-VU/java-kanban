package TaskTracker;

public class Subtask extends Task {
    private int myEpicId;    // переменная для id верхнеуровневой задачи
    private int myUniqSubtaskId;   // уникальный id сабтаска
    String myStatus;                // статус подзадачи

    public Subtask(String nameOfTask, String descriptionOfTask, Integer id, String status, int myEpicId) {
        super(nameOfTask, descriptionOfTask, id, status);
        this.myEpicId = myEpicId;
        this.myStatus = status;
    }
    public int getIdOfMyEpic() {
        return myEpicId;
    }

    public void setSubtaskId(int uniqSubtaskId) {
        myUniqSubtaskId = uniqSubtaskId;
    }

    public String getMyStatus() {
        return myStatus;
    }
}
