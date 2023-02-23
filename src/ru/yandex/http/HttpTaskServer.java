package ru.yandex.http;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Subtask;
import ru.yandex.tasks.Task;
import ru.yandex.tmanager.Managers;
import ru.yandex.tmanager.TaskManager;

import java.io.IOException;

public class HttpTaskServer {                      // слушать порт 8080, принимать запросы
    private static final int PORT = 8080;
    HttpServer httpServer;
    Gson gson = new Gson();
    TaskManager fileManager = Managers.getFileBackedManager();  // добавить реализацию FileBackedTaskManager

     public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        //httpServer.stop(15);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] path = requestPath.split("/");

        if (true){
            return null;
        } else if (false) {
            return null;
        } else {
            return null;
        }
    }

    private class TaskHandler implements HttpHandler {
        TaskManager httpManager = Managers.getDefault();
        int tmpId = 0;                  // временная заглушка
        Task tmpTask = null;            // временная заглушка
        Subtask tmpSubtask = null;      // временная заглушка
        Epic tmpEpic = null;            // временная заглушка

        //ответственность - распарсить запрос
        // 1. http метод
        // 2. url запрос
        // 3. сериализация / десериализация json (как вариант десериализовать в строку... и дальше методом из файла)
        // 4. вызво методов TaskManager

        // API должен работать так, чтобы все запросы по пути /task/<ресурсы> приходили в интерфейс
        // TaskManager
        // Путь для обычных задач /tasks/task
        // для подзадач /tasks/subtask
        // для эпиков /tasks/epic
        // получить все задачи сразу по пути /tasks/
        // получить историяю задач по пути /tasks/history

// Задачи передаются в теле запроса в формате JSON
// Идентификатор (id) задачи следует передавать параметром запроса (через вопросительный знак)
// Для каждого метода интерфейса TaskManager должен быть создан отдельный эндпойнт,
// который можно будет вызвать по HTTP


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_TASK: {
                    handleGetT(exchange);
                    break;
                }
                case GET_SUBTASK: {
                    handleGetT(exchange);
                    break;
                }
                case GET_EPIC: {
                    //httpManager.getEpicById(tmpId);
                    break;
                }
                case GET_HISTORY: {
                    //httpManager.getHistory();
                    break;
                }
                case POST_TASK: {
                    //httpManager.makeNewTask(tmpTask);
                    break;
                }
                case POST_SUBTASK: {
                    //httpManager.makeNewSubtask(tmpSubtask);
                    break;
                }
                case POST_EPIC: {
                    //httpManager.makeNewEpic(tmpEpic);
                    break;
                }
                case DELETE_All: {
                    //httpManager.dellThemAll();
                    break;
                }
                case DELETE_TASK: {
                    //httpManager.dellTaskById(tmpId);
                    break;
                }
                case DELETE_SUBTASK: {
                    //httpManager.dellTaskById(tmpId);
                    break;
                }
                case DELETE_EPIC: {
                    //httpManager.dellTaskById(tmpId);
                    break;
                }

                default:
                    //writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private void handleGetT(HttpExchange exchange) {
            httpManager.getTaskById(tmpId);
        }

        private void handleMakeT(HttpExchange exchange) throws IOException {
            httpManager.makeNewTask(tmpTask);
        }

        private void handleDellTask(HttpExchange exchange) throws IOException {
            httpManager.dellTaskById(tmpId);
        }
    }




}
