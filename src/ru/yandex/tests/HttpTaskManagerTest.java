package ru.yandex.tests;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import ru.yandex.http.HttpTaskServer;
import ru.yandex.tasks.Task;
import ru.yandex.tmanager.FileBackedTasksManager;
import ru.yandex.tmanager.Managers;
import ru.yandex.tmanager.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class HttpTaskManagerTest {
    private HttpTaskServer httpTaskServer = new HttpTaskServer();
    private TaskManager fileManager;
    private final URI urlForAllTasks = URI.create("http://localhost:8080/tasks/task/");
    private Gson gson;

    public HttpTaskManagerTest() throws IOException {
    }

     @BeforeEach
    public void beforeEach() throws IOException {
        httpTaskServer.start();
        gson = Managers.getGson();
        fileManager = FileBackedTasksManager.loadFromFile("storageTestIn.csv",
                "firstTestHttpOut.csv");
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop(5);
    }

    @Test
    public void shouldSendGetRequestToHttpTaskServerAndGetListOfTasks() throws IOException, InterruptedException {
        ArrayList<Task> testList = fileManager.getListAllTasks();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();             // экземпляр класса-строителя
        HttpRequest request = requestBuilder.GET().uri(urlForAllTasks).build();    // объект, описывающий HTTP-запрос
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
