package ru.yandex.http;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;
import ru.yandex.tmanager.Managers;
import ru.yandex.tmanager.TaskManager;


import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {                      // слушать порт 8080, принимать запросы
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private TaskManager httpManager;
    private static final int PORT = 8080;
    private KVTaskClient client;
    private HttpServer httpServer;
    private Gson gson;

        public HttpTaskServer() throws IOException, InterruptedException {    // сюда может приходить пользовательский,
                                                // менеджер со своими данными
        gson = Managers.getGson();
        httpManager = Managers.getDefault();   // этот менеджер будет равен тому, что будет передаваться в конструкторе

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
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
                        response = gson.toJson(httpManager.getListAllTasks());    // тело ответа
                        break;
                    case GET_TASK:
                        System.out.println("Началась обработка запроса на получение Task по Id");
                        response = gson.toJson(httpManager.getTaskById(id));
                        break;
                    case GET_SUBTASK:
                        System.out.println("Началась обработка запроса на получение Subtask по Id");
                        response = gson.toJson(httpManager.getSubTaskById(id));
                        break;
                    case GET_EPIC:
                        System.out.println("Началась обработка запроса на получение Epic по Id");
                        response = gson.toJson(httpManager.getEpicById(id));
                        break;
                    case GET_EPICS:
                        System.out.println("Началась обработка запроса на получение списка всех Epic");
                        response = gson.toJson(httpManager.getListAllEpics());
                        break;
                    case GET_SUBTASKS:
                        System.out.println("Началась обработка запроса на получение списка всех Subtask");
                        response = gson.toJson(httpManager.getListAllSubtasks());
                        break;
                    case GET_EPIC_SUBTASK:
                        System.out.println("Началась обработка запроса на получение всех подзадач по Id Эпика");
                        response = gson.toJson(httpManager.getListSubtasksOfEpic(httpManager.getEpicById(id)));
                        break;
                    case GET_HISTORY:
                        System.out.println("Началась обработка запроса на получение истории");
                        response = gson.toJson(httpManager.getHistory());
                        break;
                    case GET_PRIORITIZED:
                        System.out.println("Началась обработка запроса на получение списка приоритезированных задач");
                        response = gson.toJson(httpManager.getPrioritizedTasks());
                        break;

                    case POST_TASK:
                        InputStream inputStreamForTask = exchange.getRequestBody();
                        String bodyForNewOrUpdate = new String(inputStreamForTask.readAllBytes(), DEFAULT_CHARSET);
                        if (exchange.getRequestURI().getQuery() == null) {         // создание новой задачи
                            if (bodyForNewOrUpdate != null) {
                                Task newTask = gson.fromJson(bodyForNewOrUpdate, Task.class); // получаем Task в виде строки
                                httpManager.makeNewTask(newTask);
                                response = "Новый Task " + httpManager.getTaskById(newTask.getId()).toString()
                                        + " создан!";
                            }
                        } else {                                                     // обновление задачи по id
                            if (bodyForNewOrUpdate != null) {
                                Task updateTask = gson.fromJson(bodyForNewOrUpdate, Task.class);
                                httpManager.updateTask(id, updateTask);
                                response = "Task " +  httpManager.getTaskById(id).toString()
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
                                httpManager.makeNewSubtask(newSubtask);
                                response = "Новый Subtask " + httpManager.getSubTaskById(newSubtask.getId()).toString()
                                        + " создан!";
                            }
                        } else {                                                     // обновление задачи по id
                            if (bodyForNewOrUpdateSub != null) {
                                Subtask updateSubtask = gson.fromJson(bodyForNewOrUpdateSub, Subtask.class);
                                httpManager.updateTask(id, updateSubtask);
                                response = "Subtask " +  httpManager.getSubTaskById(id).toString()
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
                                httpManager.makeNewEpic(newEpic);
                                response = "Новый Epic " + httpManager.getEpicById(newEpic.getId()).toString()
                                        + " создан!";
                            }
                        } else {
                            if (bodyForNewOrUpdateEpic != null) {
                                Epic updateEpic = gson.fromJson(bodyForNewOrUpdateEpic, Epic.class);
                                httpManager.updateEpic(id, updateEpic);
                                response = "Epic " + httpManager.getEpicById(id).toString()
                                        + " обновлён!";
                            }
                        }
                        break;

                    case DELETE_All:
                        System.out.println("Началась обработка запроса на удаление всех задач");
                        httpManager.dellThemAll();
                        break;
                    case DELETE_TASK:
                    case DELETE_SUBTASK:
                    case DELETE_EPIC:
                        System.out.println("Началась обработка запроса на удаление объекта по Id");
                         httpManager.dellTaskById(id);
                        break;
                    case DELETE_TASKS:
                        System.out.println("Началась обработка запроса на удаление всех задач Task");
                        httpManager.dellAllTasks();
                        break;
                    case DELETE_SUBTASKS:
                        System.out.println("Началась обработка запроса на удаление всех задач Subtask");
                        httpManager.dellAllSubtasks();
                        break;
                    case DELETE_EPICS:
                        System.out.println("Началась обработка запроса на удаление всех задач Epic");
                        httpManager.dellAllEpic();
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

//        OutputStream getResponseBody()
//        Через этот метод определяется, что вернётся клиенту в теле ответа.
//        Метод getResponseBody() возвращает объект OutputStream, в который нужно записать массив байтов.
//        После того как байты записаны, у объекта OutputStream нужно вызвать метод close().
//        OutputStream os = httpExchange.getResponseBody();
//os.write("Тело ответа в виде простого текста".getBytes(StandardCharsets.UTF_8));
//os.close();
//        Или воспользоваться try-with-resources, как мы делали это выше.
//try (OutputStream os = httpExchange.getResponseBody()) {
//            os.write("Тело ответа в виде простого текста".getBytes());
//        }


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