package TaskTracker;

public class Subtask extends Task {
    private int myEpicID;    // переменная для id верхнеуровневой задачи

    public Subtask(Integer id, String nameOfTask, String descriptionOfTask, String status) {
        super(id, nameOfTask, descriptionOfTask, status);
        //myEpicID = epicId;
    }
}

    /*
Для каждой подзадачи известно, в рамках какого эпика она выполняется.
     */

