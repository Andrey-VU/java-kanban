package ru.yandex.http;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Status;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;
import ru.yandex.tmanager.FileBackedTasksManager;
import ru.yandex.tmanager.HttpTaskManager;
import ru.yandex.tmanager.Managers;
import ru.yandex.tmanager.TaskManager;
import ru.yandex.tmanager.adapter.LocalDateTimeAdapter;


import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {                      // слушать порт 8080, принимать запросы
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private TaskManager fileManager;
    private static final int PORT = 8080;
    private KVTaskClient client;
    private HttpServer httpServer;
    private TaskManager httpManager;
    private Gson gson;

    public HttpTaskServer() throws IOException {
        gson = Managers.getGson();
        fileManager = FileBackedTasksManager.loadFromFile("storageTestIn.csv",
                "firstTestHttpOut.csv");
        httpManager = Managers.getDefault("httpStorage.csv");
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod, String query) {
        String[] path = requestPath.split("/");
        switch (requestMethod) {
            case "GET":
                switch (path[2]) {                      // Index 2 out of bounds for length 2
                    case "task":
                        if (path.length == 3 && query == null) return Endpoint.GET_TASKS;
                        else return Endpoint.GET_TASK;

                    case "epic":
                        if (path.length == 3 && query == null) return Endpoint.GET_EPICS;
                        else return Endpoint.GET_EPIC;

                    case "subtask":
                        if (path.length == 3 && query == null) return Endpoint.GET_SUBTASKS;
                        else if (path.length == 3) return Endpoint.GET_SUBTASK;
                        else if (path[3].equals("epic")) return Endpoint.GET_EPIC_SUBTASK;

                    case "history":
                        return Endpoint.GET_HISTORY;
                    case "":
                        return Endpoint.GET_PRIORITIZED;
                }

            case "POST":
                switch (path[2]) {
                    case "task":
                        return Endpoint.POST_TASK;
                    case "epic":
                        return Endpoint.POST_EPIC;
                    case "subtask":
                        return Endpoint.POST_SUBTASK;
                }

            case "DELETE":
                switch (path[2]) {
                    case "task":
                        if (path.length == 3 && query == null) return Endpoint.DELETE_TASKS;
                        else return Endpoint.DELETE_TASK;
                    case "epic":
                        if (path.length == 3 && query == null) return Endpoint.DELETE_EPICS;
                        else return Endpoint.DELETE_EPIC;
                    case "subtask":
                        if (path.length == 3 && query == null) return Endpoint.DELETE_SUBTASKS;
                        else return Endpoint.DELETE_SUBTASK;
                }
        }
        return Endpoint.ERROR;
    }

    private class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String response = "это пустая форма для ответа";   // форма для ответа
                URI requestURI = exchange.getRequestURI();         //  здесь весь URI
                exchange.sendResponseHeaders(200, 0);         // отправляем код, что, мол, запрос получен
                String path = requestURI.getPath();                 // здесь путь
                String method = exchange.getRequestMethod();       // здесь метод
                String query = requestURI.getQuery();                    //  здесь "запрос": то, что после символа ?
                int id = parseQueryId(query);
                Endpoint endpoint = getEndpoint(path, method, query);           // возвращает endpoint

                switch (endpoint) {                               // формирует ответ клиенту
                    case GET_TASKS:
                        // сообщить о начале работы над запросом
                        System.out.println("Началась обработка запроса на получение списка всех задач");
                        response = gson.toJson(fileManager.getListAllTasks());    // тело ответа
                        break;
                    case GET_TASK:
                        System.out.println("Началась обработка запроса на получение Task по Id");
                        response = gson.toJson(fileManager.getTaskById(id));
                        break;
                    case GET_SUBTASK:
                        System.out.println("Началась обработка запроса на получение Subtask по Id");
                        response = gson.toJson(fileManager.getSubTaskById(id));
                        break;
                    case GET_EPIC:
                        System.out.println("Началась обработка запроса на получение Epic по Id");
                        response = gson.toJson(fileManager.getEpicById(id));
                        break;
                    case GET_EPICS:
                        System.out.println("Началась обработка запроса на получение списка всех Epic");
                        response = gson.toJson(fileManager.getListAllEpics());
                        break;
                    case GET_SUBTASKS:
                        System.out.println("Началась обработка запроса на получение списка всех Subtask");
                        response = gson.toJson(fileManager.getListAllSubtasks());
                        break;
                    case GET_EPIC_SUBTASK:
                        System.out.println("Началась обработка запроса на получение всех подзадач по Id Эпика");
                        response = gson.toJson(fileManager.getListSubtasksOfEpic(fileManager.getEpicById(id)));
                        break;
                    case GET_HISTORY:
                        System.out.println("Началась обработка запроса на получение истории");
                        response = gson.toJson(fileManager.getHistory());
                        break;
                    case GET_PRIORITIZED:
                        System.out.println("Началась обработка запроса на получение списка приоритезированных задач");
                        response = gson.toJson(fileManager.getPrioritizedTasks());
                        break;

                    case POST_TASK:
                        InputStream inputStreamForTask = exchange.getRequestBody();
                        String bodyForNewOrUpdate = new String(inputStreamForTask.readAllBytes(), DEFAULT_CHARSET);
                        if (exchange.getRequestURI().getQuery() == null) {         // создание новой задачи
                            if (bodyForNewOrUpdate != null) {
                                Task newTask = gson.fromJson(bodyForNewOrUpdate, Task.class); // получаем Task в виде строки
                                fileManager.makeNewTask(newTask);
                                response = "Новый Task " + fileManager.getTaskById(newTask.getId()).toString()
                                        + " создан!";
                            }
                        } else {                                                     // обновление задачи по id
                            if (bodyForNewOrUpdate != null) {
                                Task updateTask = gson.fromJson(bodyForNewOrUpdate, Task.class);
                                fileManager.updateTask(id, updateTask);
                                response = "Task " +  fileManager.getTaskById(id).toString()
                                        + " обновлён!";
                            }
                        }
                        break;
                    case POST_SUBTASK:                     // и для новой подзадачи и для обновления старой
                        InputStream inputStreamForSubtask = exchange.getRequestBody();
                        String bodyForNewOrUpdateSub = new String(inputStreamForSubtask.readAllBytes(), DEFAULT_CHARSET);
                        if (exchange.getRequestURI().getQuery() == null) {         // создание новой задачи
                            if (bodyForNewOrUpdateSub != null) {
                                Subtask newSubtask = gson.fromJson(bodyForNewOrUpdateSub, Subtask.class); // получаем Task в виде строки
                                fileManager.makeNewSubtask(newSubtask);
                                response = "Новый Subtask " + fileManager.getSubTaskById(newSubtask.getId()).toString()
                                        + " создан!";
                            }
                        } else {                                                     // обновление задачи по id
                            if (bodyForNewOrUpdateSub != null) {
                                Subtask updateSubtask = gson.fromJson(bodyForNewOrUpdateSub, Subtask.class);
                                fileManager.updateTask(id, updateSubtask);
                                response = "Subtask " +  fileManager.getSubTaskById(id).toString()
                                        + " обновлён!";
                            }
                        }
                        break;
                    case POST_EPIC:
                        InputStream inputStreamForEpic = exchange.getRequestBody();
                        String bodyForNewOrUpdateEpic = new String(inputStreamForEpic.readAllBytes(), DEFAULT_CHARSET);
                        if (exchange.getRequestURI().getQuery() == null) {         // создание новой задачи
                            if (bodyForNewOrUpdateEpic != null) {
                                Epic newEpic = gson.fromJson(bodyForNewOrUpdateEpic, Epic.class); // получаем Task в виде строки
                                fileManager.makeNewEpic(newEpic);
                                response = "Новый Epic " + fileManager.getEpicById(newEpic.getId()).toString()
                                        + " создан!";
                            }
                        } else {
                            if (bodyForNewOrUpdateEpic != null) {
                                Epic updateEpic = gson.fromJson(bodyForNewOrUpdateEpic, Epic.class);
                                fileManager.updateEpic(id, updateEpic);
                                response = "Epic " + fileManager.getEpicById(id).toString()
                                        + " обновлён!";
                            }
                        }
                        break;

                    case DELETE_All:
                        System.out.println("Началась обработка запроса на удаление всех задач");
                        fileManager.dellThemAll();
                        break;
                    case DELETE_TASK:
                    case DELETE_SUBTASK:
                    case DELETE_EPIC:
                        System.out.println("Началась обработка запроса на удаление объекта по Id");
                         fileManager.dellTaskById(id);
                        break;
                    case DELETE_TASKS:
                        System.out.println("Началась обработка запроса на удаление всех задач Task");
                        fileManager.dellAllTasks();
                        break;
                    case DELETE_SUBTASKS:
                        System.out.println("Началась обработка запроса на удаление всех задач Subtask");
                        fileManager.dellAllSubtasks();
                        break;
                    case DELETE_EPICS:
                        System.out.println("Началась обработка запроса на удаление всех задач Epic");
                        fileManager.dellAllEpic();
                        break;
                    case ERROR:
                        System.out.println("Получен некорректный запрос метода: " + method);
                        exchange.sendResponseHeaders(405, 0);
                        break;
                }

                // отправить ответ клиенту
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

            } catch (Exception exception) {exception.printStackTrace();
            } finally { exchange.close();}
        }

        private int parseQueryId(String query) {
            try {
                return Integer.parseInt(query);
            } catch (NumberFormatException exception) {
                return -1;
            }
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Доступен в браузере http://localhost:" + PORT + "/tasks/");
        //System.out.println("API_TOKEN: " + apiToken);
        httpServer.start();
    }

    public void stop(int i) {
        httpServer.stop(i);
        System.out.println("Сервер на порту " + PORT + " остановлен");
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");   //"application/json; charset=utf-8"
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}