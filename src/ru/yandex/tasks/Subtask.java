public class Subtask extends Task {
    private int epicId;    // переменная для id верхнеуровневой задачи

    public Subtask(String nameOfTask, String descriptionOfTask, Integer id, String status, Integer epicId) {
        super(nameOfTask, descriptionOfTask, id, status);
        this.epicId = epicId;
    }
    public int getEpicID() {
        return epicId;
    }

}
