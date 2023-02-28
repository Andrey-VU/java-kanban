package ru.yandex.http;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Status;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;
import ru.yandex.tmanager.HttpTaskManager;
import ru.yandex.tmanager.Managers;
import ru.yandex.tmanager.TaskManager;
import ru.yandex.tmanager.adapter.LocalDateTimeAdapter;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {                      // слушать порт 8080, принимать запросы
//    //private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final int PORT = 8080;
    private KVTaskClient client;
    private HttpServer httpServer;
    private TaskManager httpManager;
    private Gson gson;

    public HttpTaskServer() throws IOException {
        gson = Managers.getGson();
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
                switch (path[2]) {
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
        private TaskManager fileManager
                = Managers.getFileBackedManager("storageTestIn.csv", "fromHttpTaskServer.csv");
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                URI requestURI = exchange.getRequestURI();         //  здесь весь URI
                exchange.sendResponseHeaders(200, 0);         // отправляем код, что, мол, запрос получен
                String path = requestURI.getPath();                 // здесь путь
                String method = exchange.getRequestMethod();       // здесь метод
                String query = requestURI.getQuery();             //  здесь "запрос": то, что после символа ?
                Endpoint endpoint = getEndpoint(path, method, query);
                int id = parseQueryId(query);

                switch (endpoint) {
                    case GET_TASK:
                        handleGetTask(exchange, id);
                        break;
                    case GET_SUBTASK:
                        handleGetSubtask(exchange, id);
                        break;
                    case GET_EPIC:
                        handleGetEpic(exchange, id);
                        break;
                    case GET_TASKS:
                        handleGetTasks(exchange);
                        break;
                    case GET_EPICS:
                        handleGetEpics(exchange);
                        break;
                    case GET_SUBTASKS:
                        handleGetSubtasks(exchange);
                        break;
                    case GET_EPIC_SUBTASK:
                        handleGetEpicSubtask(exchange, id);
                        break;
                    case GET_HISTORY:
                        handleGetHistory(exchange);
                        break;
                    case GET_PRIORITIZED:
                        handleGetPrioritized(exchange);
                        break;

//                    case POST_TASK:                     // и для новой задачи и для обновления старой
//                        handlePostTask(exchange);
//                        break;
//                    case POST_SUBTASK:                  // и для новой подзадачи и для обновления старой
//                        handlePostSubtask(exchange);
//                        break;
//                    case POST_EPIC:
//                        handlePostEpic(exchange);
//                        break;

                    case DELETE_All:
                        handleDeleteAll(exchange);
                        break;
                    case DELETE_TASK:
                    case DELETE_SUBTASK:
                    case DELETE_EPIC:
                        handleDeleteTask(id, exchange);
                        break;
                    case DELETE_TASKS:
                        handleDeleteTasks(exchange);
                        break;
                    case DELETE_SUBTASKS:
                        handleDellSubtasks(exchange);
                        break;
                    case DELETE_EPICS:
                        handleDellEpics(exchange);
                        break;
                    case ERROR:
                        break;
                    default:
                        System.out.println("Получен некорректный запрос метода: " + method);
                        exchange.sendResponseHeaders(405, 0);

                }
            } catch (Exception exception) {exception.printStackTrace();
            } finally { exchange.close();}
        }

//        private void handlePostTask(HttpExchange exchange) throws IOException {       // необходимо обрабатывать тело запроса
//            if (exchange.getRequestURI().getQuery() == null) {     // создание новой задачи
//                String body = String.valueOf(exchange.getRequestBody());   // получение тела запроса в виде строки
//                if (body != null) {
//                    Task task = gson.fromJson(body, Task.class);           // получаем Task в виде строки
//                    // или здесь нужно закинуть эту строку в KVServer, чтобы оттуда уже загрузить в fileManager
//                    fileManager.makeNewTask(task);
//                    String response = gson.toJson();
//                    sendText(exchange, response);
//                }
//
//
//            } else {                                              // обновление уже существующей задачи
//
//            }
//        }

        private void handleGetEpicSubtask(HttpExchange exchange, int id) throws IOException {
            String response = gson.toJson(fileManager.getListSubtasksOfEpic(fileManager.getEpicById(id)));
            sendText(exchange, response);
        }

        private void handleGetSubtasks(HttpExchange exchange) throws IOException {
            String response = gson.toJson(fileManager.getListAllSubtasks());
            sendText(exchange, response);
        }

        private void handleGetEpics(HttpExchange exchange) throws IOException {
            String response = gson.toJson(fileManager.getListAllEpics());
            sendText(exchange, response);
        }

        private void handleDellSubtasks(HttpExchange exchange) throws IOException {
            fileManager.dellAllSubtasks();
            exchange.sendResponseHeaders(200, 0);
        }

        private void handleDeleteTasks(HttpExchange exchange) throws IOException {
            fileManager.dellAllTasks();
            exchange.sendResponseHeaders(200, 0);
        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка запроса от клиента на получения списка всех задач.");
            if (fileManager != null) {
                String response = gson.toJson(fileManager.getListAllTasks());
                sendText(exchange, response);
            }
        }

        private void handleGetTask(HttpExchange exchange, int Id) throws IOException {
            String response = gson.toJson(fileManager.getTaskById(Id));
            sendText(exchange, response);
        }
        private void handleGetEpic(HttpExchange exchange, int Id) throws IOException {
            String response = gson.toJson(fileManager.getEpicById(Id));
            sendText(exchange, response);
        }
        private void handleGetSubtask(HttpExchange exchange, int Id) throws IOException {
            String response = gson.toJson(fileManager.getSubTaskById(Id));
            sendText(exchange, response);
        }
        private void handleGetHistory(HttpExchange exchange) throws IOException {
            String response = gson.toJson(fileManager.getHistory());
            sendText(exchange, response);
        }
        private void handleGetPrioritized(HttpExchange exchange) throws IOException {
            String response = gson.toJson(fileManager.getPrioritizedTasks());
            sendText(exchange, response);
        }
        private void handleDeleteTask(int Id, HttpExchange exchange) throws IOException {
            fileManager.dellTaskById(Id);
            System.out.println("Задача с ИД " + Id + " удалена");
            exchange.sendResponseHeaders(200, 0);
        }
        private void handleDellEpics(HttpExchange exchange) throws IOException {
            fileManager.dellAllEpic();
            exchange.sendResponseHeaders(200, 0);
        }
        private void handleDeleteAll(HttpExchange exchange) throws IOException {
            fileManager.dellThemAll();
            exchange.sendResponseHeaders(200, 0);
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
        System.out.println("Открой в браузере http://localhost:" + PORT + "/tasks/");
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


/*
public String taskToJson(Task task) {                                   // пока не работает для эпика

        String taskSerialized = gson.toJson(task);
        System.out.println(taskSerialized);
        return taskSerialized;
    }

    public Task jsonToTask(String taskSerialized){                          // пока не работает для эпика

        Task taskFromJson = gson.fromJson(taskSerialized,Task.class);
        System.out.println(taskFromJson.toString());
        return taskFromJson;
    }

public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        //httpTaskServer.stop(25);

//        TaskManager fileManager = Managers.getFileBackedManager();
//        TaskManager inMemoryManager = Managers.getDefault();
//
//        Task taskTest = new Task("Test name", "Test description", 0, Status.NEW,
//                "01.01.2000--12:00", 3600);
//        inMemoryManager.makeNewTask(taskTest);
//        Epic epicTest = new Epic("Epic name", "Epic description", 0, Status.NEW);
//        inMemoryManager.makeNewEpic(epicTest);
//        Subtask subtaskTest = new Subtask("Subtask name", "Subtask description",
//                0, Status.NEW, epicTest.getId(), "01.05.2000--12:00", 3600);
//        inMemoryManager.makeNewSubtask(subtaskTest);
//
//
//        String taskSerialized = httpTaskServer.taskToJson(taskTest);
//        Task taskFromJson = httpTaskServer.jsonToTask(taskSerialized);
//        if (taskFromJson.equals(taskSerialized)) {
//            System.out.println("Десериализовано успешно");
//        } else System.out.println("Десериализация не прошла");
//        String taskSerialized2 = httpTaskServer.taskToJson(taskTest);
//
//        String tmpRequestPath1 = "/tasks/task/";
//        String tmpRequestPath2 = "/tasks/task/?id=";
//
//        String[] path1 = tmpRequestPath1.split("/");
//        String[] path2 = tmpRequestPath2.split("/");
//
//        System.out.println("path1" + " " + path1[0] + " " + path1[1] + " " + path1[2] + " " + path1.length + "\n"
//                + "path2" + " " + path2[0] + " " + path2[1] + " " + path2[2] + " " + path2[3] + " " + path2.length );


    }  // временно для тестирования


 */
