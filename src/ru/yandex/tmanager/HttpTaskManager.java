package ru.yandex.tmanager;
import com.google.gson.*;
import ru.yandex.exceptions.ManagerSaveException;
import ru.yandex.http.KVTaskClient;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;
import ru.yandex.tasks.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private String key;
    private final Gson gson;


    public HttpTaskManager(String urlServer) throws IOException, InterruptedException {
        super();
        kvTaskClient = new KVTaskClient(urlServer);
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }

    @Override
    public void save() throws ManagerSaveException {   // сохранять состояние менеджера в строку или в json
                                                  // и отправлять на сервер.
        Writer writer = new StringWriter();
        Map<Integer, Task> tmpStorage = new HashMap<>();
        for (Task value :  getTaskTasks().values()) tmpStorage.put(value.getId(), value);
        for (Epic value : getEpicTasks().values()) tmpStorage.put(value.getId(), value);
        for (Subtask value : getSubtaskTasks().values()) tmpStorage.put(value.getId(), value);

        try (BufferedWriter bw = new BufferedWriter(writer) ) {  //OUT
            bw.write("id,type,name,status,description,startTime,duration,epic");
            bw.newLine();
            for (Task value : tmpStorage.values()) {
                bw.write(toStringForFile(value));
                bw.newLine();
            }
            bw.newLine();
            bw.write(historyToString(getHistoryManager()));

        } catch (IOException exception) {
            throw new ManagerSaveException("Во время записи данных менеджера в строку произошла ошибка!", exception);
        }
        String stringForSave = writer.toString();
        kvTaskClient.put(key, gson.toJson(stringForSave));
    }

    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public TaskManager loadFromKVServer() throws IOException, InterruptedException {
        HttpTaskManager loadedFromKVServer = Managers.getDefault();
        String response = kvTaskClient.load(key);

        String stringFromServer = gson.fromJson(response, String.class);
        System.out.println("ЗДЕСЬ ТО, ЧТО Я ВЫГРУЖАЮ С СЕРВЕРА: " + stringFromServer);

        List<String> tmp = new ArrayList<>();     // для хранения списка строк из строки
        String isHistory = "";

        try (Reader reader = new StringReader(stringFromServer); BufferedReader br = new BufferedReader(reader)) {
            String line = "";
            while (br.ready() && !line.equals("It is history:" + null) ) {
                line = isHistory + br.readLine();
                if (!line.isBlank() && !line.equals("It is history:" + null)) {
                    tmp.add(line);
                } else {
                    isHistory = "It is history:";
                }

            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время распаковки данных с сервера");
        }

        for (int i = 1; i < tmp.size(); i++) {
            if (tmp != null) {
                if (!tmp.get(i).isBlank() && !tmp.get(i).contains("It is history:")) {
                    String[] tmpArray = tmp.get(i).split(",");  //строковый массив после деления строки по ","
                    switch (Type.valueOf(tmpArray[1])) {
                        case TASK:
                            Task tmpTask = fromString(tmp.get(i));
                            loadedFromKVServer.makeNewTask(tmpTask);
                            break;
                        case EPIC:
                            Epic tmpEpic = (Epic) fromString(tmp.get(i));
                            loadedFromKVServer.makeNewEpic(tmpEpic);
                            break;
                        case SUBTASK:
                            Subtask tmpSubtask = (Subtask) fromString(tmp.get(i));
                            loadedFromKVServer.makeNewSubtask(tmpSubtask);
                            break;
                    }
                } else if (tmp.get(i).isBlank()) {
                    continue;
                } else {

                    String historyIdsFromTask = tmp.get(i).substring(isHistory.length());
                    for (Integer id : historyFromString(historyIdsFromTask)) {
                        loadedFromKVServer.getTaskById(id);
                        loadedFromKVServer.getEpicById(id);
                        loadedFromKVServer.getSubTaskById(id);
                    }
                }
            }
        }
        return loadedFromKVServer;
    }
}
