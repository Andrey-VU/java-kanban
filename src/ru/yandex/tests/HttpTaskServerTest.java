package ru.yandex.tests;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.yandex.http.HttpTaskServer;
import ru.yandex.tasks.Epic;
import ru.yandex.tasks.Status;
import ru.yandex.tasks.Task;
import ru.yandex.tasks.Type;
import ru.yandex.tmanager.FileBackedTasksManager;
import ru.yandex.tmanager.Managers;
import ru.yandex.tmanager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer = new HttpTaskServer();
    private TaskManager fileManager;
    private final URI urlWithoutId = URI.create("http://localhost:8080/tasks/task/");
    private final URI urlWithId = URI.create("http://localhost:8080/tasks/task/?id=");
    private final URI urlForHistory = URI.create("http://localhost:8080/tasks/history");
    private Gson gson;

    public HttpTaskServerTest() throws IOException, InterruptedException {
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        //httpTaskServer.start();
        gson = Managers.getGson();
        fileManager = FileBackedTasksManager.loadFromFile("storageTestIn.csv",
                "firstTestHttpOut.csv");
    }
    @AfterEach
    public void afterEach() {
        httpTaskServer.stop(1);
    }

    @Test   // пока не работает для эпика
    public void shouldMakeJsonFromTaskAndThenMakeTaskFromIt() throws IOException {
        Task taskForJson = new Task("Task to Json", "Make newTask and make Json From It",
                0, Status.NEW,"01.01.1917--12:00",0);
        fileManager.makeNewTask(taskForJson);
        String taskSerialized = gson.toJson(fileManager.getTaskById(taskForJson.getId()));
        Task taskFromJson = gson.fromJson(taskSerialized,Task.class);
        Assertions.assertEquals(taskSerialized, gson.toJson(taskFromJson),
                "трансформация в json или обратно не работает");
    }

    @Test
    public void shouldMakeJsonFromEpicAndThenMakeEpicFromIt () throws IOException {
        Epic epicForJson = new Epic("Epic to Json",
                "Make newEpic and make Json From It",0, Status.NEW);
        fileManager.makeNewEpic(epicForJson);
        String epicSerialized = gson.toJson(fileManager.getTaskById(epicForJson.getId()));
        Epic epicFromJson = gson.fromJson(epicSerialized,Epic.class);
        Assertions.assertEquals(epicSerialized, gson.toJson(epicFromJson),
                "трансформация в json или обратно не работает");
    }

    @Test
    public void shouldGetHistoryFromServer() throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest requestHistory = requestBuilder.uri(urlForHistory).GET().build();

// обеспечиваем принятие ответа
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(requestHistory, handler);

        System.out.println("Вот какое тело нам вернули по запросу создания History: " + response.body());
        Assertions.assertEquals("Новый Task создан!",  response.body(),
                "Новая задача Task не создана, либо не доставлена обратно клиенту" );
    }

    @Test
    public void shouldMakeNewTask() throws IOException, InterruptedException {
        Task testTaskHttpNew = new Task("newForHTTP","ServerMade", 0, Status.NEW,
                "01.01.1917--06:00", 3600);
        TaskManager fileManager2 = new FileBackedTasksManager();
        fileManager2.makeNewTask(testTaskHttpNew);
        Task testTaskBeforeSending = fileManager2.getTaskById(testTaskHttpNew.getId());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        final HttpRequest.BodyPublisher bodyNewTask =
                HttpRequest.BodyPublishers.ofString(gson.toJson(testTaskHttpNew));
        HttpRequest requestNewTask = requestBuilder.uri(urlWithoutId).POST(bodyNewTask).build();

        // обеспечиваем принятие ответа
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(requestNewTask, handler);

        //System.out.println("Вот какое тело нам вернули по запросу создания новой Task: " + response.body());
        Assertions.assertEquals("Новый Task " + testTaskBeforeSending.toString()
                        + " создан!",  response.body(),
                "Новая задача Task не создана, либо не доставлена обратно клиенту" );
    }

    @Test
    public void shouldGetResponseBodyFromServer() throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request2 = requestBuilder.GET().uri(urlWithoutId).build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request2, handler);
        Assertions.assertEquals(!response.body().isEmpty(), true, "тело не доставлено" );
    }

    @Test
    public void shouldGetResponseHeaderFromServer() throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        final HttpRequest.BodyPublisher bodyUpdate = HttpRequest.BodyPublishers.ofString("1");
        HttpRequest request1 = requestBuilder.uri(urlWithId).POST(bodyUpdate).build();

        HttpRequest request2 = requestBuilder.GET().uri(urlWithoutId).build();
        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> responseUpdateTaskById = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request2, handler);

        Assertions.assertEquals(200, response.statusCode(),
                "Запрос ресурса tasks/task/ сервером не получен" );
        Assertions.assertEquals(200, responseUpdateTaskById.statusCode(),
                "Запрос ресурса tasks/task/?id= сервером не получен" );
        }

    @Test
    public void shouldSendGetRequestToHttpTaskServerAndGetListOfTasks() throws IOException, InterruptedException {
        ArrayList<Task> testList = fileManager.getListAllTasks();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();             // экземпляр класса-строителя
        HttpRequest request = requestBuilder.GET().uri(urlWithoutId).build();    // объект, описывающий HTTP-запрос
        HttpClient client = HttpClient.newHttpClient();                       // HTTP-клиент с настройками по умолчанию
        // стандартный обработчик тела запроса с конвертацией содержимого в строку
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);     // отправляем запрос, получаем ответ сервера

        // выводим код состояния и тело ответа
        System.out.println("Код ответа: " + response.statusCode());
        System.out.println("Тело ответа: " + response.body());

        Assertions.assertEquals(testList.toString(), response.body().toString(),
                "Список всех Task не получен с сервера");
    }
}

//        HttpRequest requestAllTasks = HttpRequest.newBuilder().uri(url).GET().build();
//        HttpResponse<String> response = client.send(requestAllTasks, HttpResponse.BodyHandlers.ofString());


//    HttpRequest requestAllTasks = HttpRequest.newBuilder().uri(urlForTasks).GET().build();
//
//    HttpResponse<String> response = client.send(requestAllTasks, HttpResponse.BodyHandlers.ofString());
//
//    // 1. Получить все задачи Task
//    HttpRequest requestAllTasks = HttpRequest.newBuilder().uri(urlForTasks).GET().build();
//    HttpResponse<String> response = client.send(requestAllTasks, HttpResponse.BodyHandlers.ofString());
//
//    // 2. Удаление всех Task
//    HttpRequest requestDelAllTasks = HttpRequest.newBuilder().uri(urlForTasks).DELETE().build();
//    HttpResponse<String> responseDelAllTasks = client.send(requestDelAllTasks, HttpResponse.BodyHandlers.ofString());
//
//    // 3. Получение одной Task
//    HttpRequest requestGetTaskById = HttpRequest.newBuilder().uri(urlForOneTask).GET().build();
//    HttpResponse<String> responseGetTaskById = client.send(requestGetTaskById, HttpResponse.BodyHandlers.ofString());
//
//    // 4. Удаление одной Task
//    HttpRequest requestDelTaskById = HttpRequest.newBuilder().uri(urlForOneTask).DELETE().build();
//    HttpResponse<String> responseDelTaskById = client.send(requestDelTaskById, HttpResponse.BodyHandlers.ofString());
//
//    // 5. обновление одной Task
//    final HttpRequest.BodyPublisher bodyUpdate = HttpRequest.BodyPublishers.ofString("json на HttpSервер");
//    HttpRequest requestUpdateTaskById = HttpRequest.newBuilder().uri(urlForOneTask).POST(bodyUpdate).build();
//    HttpResponse<String> responseUpdateTaskById = client.send(requestUpdateTaskById, HttpResponse.BodyHandlers.ofString());
//
//    // 6. создание одной новой Task
//    final HttpRequest.BodyPublisher bodyNew = HttpRequest.BodyPublishers.ofString("json на HttpSервер");
//    HttpRequest requestPostTaskById = HttpRequest.newBuilder().uri(urlForTasks).POST(bodyNew).build();
//
//
//    HttpResponse<String> response = client.send(requestAllTasks, HttpResponse.BodyHandlers.ofString());



//    private final URI urlForTasks = URI.create("http://localhost:8080/tasks/task/");// GET, DELETE всех задач + new POST
//    private final URI urlForOneTask = URI.create("http://localhost:8080/tasks/task/?id="); // GET, DELETE, POST по id



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



//    @Test
//    public void shouldUpdateTask() {
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//        final HttpRequest.BodyPublisher bodyUpdate = HttpRequest.BodyPublishers.ofString("1");
//        HttpRequest requestUpdate = HttpRequest.newBuilder().uri(urlWithId).POST(bodyUpdate).build();
//    }
