package ru.yandex.tmanager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.exceptions.ManagerSaveException;
import ru.yandex.http.KVTaskClient;
import java.io.IOException;

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
    public void save() throws ManagerSaveException {
        kvTaskClient.put(key, gson.toJson(this));
    }

    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    public TaskManager loadFromKVServer() throws IOException, InterruptedException {
        HttpTaskManager loadedFromKVServer = Managers.getDefault();
        String response = kvTaskClient.load(key);
        System.out.println(response);

        // распарсить response

        return null; // loadedFromKVServer;
    }
}

   /*
     реализация аналогична файловому менеджеру, но пишем и читаем не из файла,
     а через KVTaskClient - вызываем его методы save/load
     */

    // Ответственность:
    // 1. сохранять данные на KVServer и восстанавливаться с данных с сервера
    // по аналогии с FileBacked (приватный метод save)

    // 2. Вызывать методы базовой реализации TaskManager
    // KVTaskClient - слой между HttpTaskManager и KVServer для работы с KVServer

    // Ответственность
    // 1. Создание http запросов к KVServer
    // KVServer - сервер для хранения состояния менеджеров (по аналогии с файлом)
    // Ответственность
    // 1. Регистрация клиентов
    // 2. Сохранение и выгрузка данных

// HttpTaskServer -> HttpTaskManager -> KVTaskClient -> KVServer

//    public HttpTaskManager(String pathIn, String pathOut) throws IOException, InterruptedException {
//        super(pathIn, pathOut);
//        kvTaskClient = new KVTaskClient("newToken");
//    }
//        List<String> tmp = new ArrayList<>();                // для хранения списка строк из файла
//        String isHistory = "";
//        try (FileReader reader = new FileReader(getFileIn()); BufferedReader br = new BufferedReader(reader)) {
//            while (br.ready()) {
//                String line = isHistory + br.readLine();
//                if (!line.isBlank()) {
//                    tmp.add(line);
//                } else {
//                    isHistory = "It is history:";
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("Произошла ошибка во время чтения файла.");
//        }
//
//        for (int i = 1; i < tmp.size(); i++) {
//            if (tmp != null) {
//                if (!tmp.get(i).isBlank() && !tmp.get(i).contains("It is history:"))  {
//                    String[] tmpArray = tmp.get(i).split(",");    // строковый массив после деления строки по ","
//                    switch (Type.valueOf(tmpArray[1])) {
//                        case TASK:
//                            Task tmpTask = fromString(tmp.get(i));
//                            loadedFromFile.makeNewTask(tmpTask);
//                            break;
//                        case EPIC:
//                            Epic tmpEpic = (Epic) fromString(tmp.get(i));
//                            loadedFromFile.makeNewEpic(tmpEpic);
//                            break;
//                        case SUBTASK:
//                            Subtask tmpSubtask = (Subtask) fromString(tmp.get(i));
//                            loadedFromFile.makeNewSubtask(tmpSubtask);
//                            break;
//                    }
//                } else if (tmp.get(i).isBlank()) {
//                    continue;
//                } else {
//
//                    String historyIdsFromTask = tmp.get(i).substring(isHistory.length());
//                    for (Integer id : historyFromString(historyIdsFromTask)) {
//                        loadedFromFile.getTaskById(id);
//                        loadedFromFile.getEpicById(id);
//                        loadedFromFile.getSubTaskById(id);
//                    }
//                }
//            }
//        }
//        return (FileBackedTasksManager) loadedFromFile;
//
