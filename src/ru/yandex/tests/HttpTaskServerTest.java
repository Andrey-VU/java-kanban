package ru.yandex.tests;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.yandex.http.HttpTaskServer;
import ru.yandex.tasks.*;
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
    HttpTaskServer httpTaskServer;
    private TaskManager fileManager;
    private Gson gson;
    private final URI urlWithoutId = URI.create("http://localhost:8080/tasks/task/");// GET, DELETE всех задач + new POST
    private final URI urlWithId = URI.create("http://localhost:8080/tasks/task/?id="); // GET, DELETE, POST по id

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        gson = Managers.getGson();
        fileManager = FileBackedTasksManager.loadFromFile("storageTestIn.csv",
                "firstTestHttpOut.csv");
    }

    @AfterEach
    public void afterEach() throws IOException {
        fileManager.dellThemAll();
        fileManager.getHistory().clear();
        fileManager.getPrioritizedTasks().clear();
        httpTaskServer.stop(0);
    }

    @Test
    public void shouldMakeJsonFromTaskAndThenMakeTaskFromIt() throws IOException {
        Task taskForJson = new Task("Task to Json", "Make newTask and make Json From It",
                0, Status.NEW, "01.01.1917--12:00", 0);
        fileManager.makeNewTask(taskForJson);
        String taskSerialized = gson.toJson(fileManager.getTaskById(taskForJson.getId()));
        Task taskFromJson = gson.fromJson(taskSerialized, Task.class);
        Assertions.assertEquals(taskSerialized, gson.toJson(taskFromJson),
                "трансформация в json или обратно не работает");
    }

    @Test   // пока не работает для эпика
    public void shouldMakeJsonFromSabtaskAndThenMakeSabtaskFromIt() throws IOException {
        Epic epicForJson = new Epic("Epic to Json",
                "Make newEpic and make Json From It", 0, Status.NEW);
        fileManager.makeNewEpic(epicForJson);
        Subtask subtaskForJson = new Subtask("Subtask to Json",
                "Make newSubtask and make Json From It",
                0, Status.NEW, epicForJson.getId(), "01.01.1917--12:00", 0);
        fileManager.makeNewSubtask(subtaskForJson);

        String subtaskSerialized = gson.toJson(fileManager.getSubTaskById(subtaskForJson.getId()));
        Task subtaskFromJson = gson.fromJson(subtaskSerialized, Subtask.class);
        Assertions.assertEquals(subtaskSerialized, gson.toJson(subtaskFromJson),
                "трансформация в json или обратно не работает");
    }

    @Test
    public void shouldMakeJsonFromEpicAndThenMakeEpicFromIt() throws IOException {
        Epic epicForJson = new Epic("Epic to Json",
                "Make newEpic and make Json From It", 0, Status.NEW);
        fileManager.makeNewEpic(epicForJson);
        String epicSerialized = gson.toJson(fileManager.getTaskById(epicForJson.getId()));
        Epic epicFromJson = gson.fromJson(epicSerialized, Epic.class);
        Assertions.assertEquals(epicSerialized, gson.toJson(epicFromJson),
                "трансформация в json или обратно не работает");
    }

    @Test
    public void shouldGetHistoryFromServer() throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest requestHistory = requestBuilder.uri(urlWithoutId).GET().build();

// обеспечиваем принятие ответа
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(requestHistory, handler);

        String expectedResponse = gson.toJson(fileManager.getHistory());
        Assertions.assertEquals(expectedResponse,  response.body(),
                "История не возвращается с сервера" );
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
    public void shouldGetResponseNotEmptyBodyFromServer() throws IOException, InterruptedException {
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
