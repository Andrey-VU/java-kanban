package ru.yandex.http;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/*
1. HttpTaskManager отправляет запрос на получение/сохранение/удаление/обновление задачи HttpTaskServer'у;
2. HttpTaskServer обрабатывает запрос:
   - вызывает экземпляр FileManager, чтобы правильным образом обработать запрос HttpTaskManager;
   - передаёт поручение KVTaskClient'у, (который нужен для того, чтобы взаимодействовать с KVServerom);
   - отчитывается перед менеджером о том, что запрос получен/отправлен или произошла неувязка;
3. KVTaskClient обработывает полученное поручение и обращается в хранилище KVServer, чтобы достать/изменить/удалить
    - отчитывается перед KVTaskServer о ходе выполнения;
4. KVServer - меняет состояние хранилища (файла) на сервери в соответствии с запросом; отчитывается об этом;


У KVTaskClient 3 метода для методов register/save/load из KVServer. Каждый метод создает http запрос, отправляет его,
и сообщает статус.
В случаи GET-запросов получает ответ от KVсервера и передаёт его менеджеру. Менеджер парсит JSON в нужный тип.
При POST-запросах используем при создании метод POST(HttpRequest.BodyPublishers.ofString(“Указываем json, который
 хотим передать на сервер”)).
Ключ передаем через параметр пути запроса.

В KVTaskClient в URI нужно подставить значение токена (поле apiToken из KVServer). Как его лучше достать?
Создать дублирующее поле apiToken и инициализировать его, возвращая значение из метода register?
Сохранять токен в клиенте
//    При создании KVTaskClient учтите следующее: Конструктор принимает URL к серверу хранилища и
//    регистрируется. При регистрации выдаётся токен (API_TOKEN), который нужен при работе с сервером.

 */

public class KVTaskClient {
    private final String apiToken;
    private HttpClient client = HttpClient.newHttpClient();

    //В KVTaskClient в URI нужно подставить значение токена (поле apiToken из KVServer)
    public KVTaskClient(String apiToken) throws IOException, InterruptedException {
        this.apiToken = apiToken;      }

    public HttpResponse<String> send(HttpRequest requestAllTasks, HttpResponse.BodyHandler<String> ofString) {
        return null;
    }

    // 1. Получить все задачи Task


//    Метод void put(String key, String json) должен сохранять состояние менеджера задач через
//    запрос POST /save/<ключ>?API_TOKEN=.
//    Метод String load(String key) должен возвращать состояние менеджера задач через запрос
//    GET /load/<ключ>?API_TOKEN=.
//    Далее проверьте код клиента в main.
//    Для этого запустите KVServer, создайте экземпляр KVTaskClient. Затем сохраните
//    значение под разными ключами и проверьте, что при запросе возвращаются нужные данные.
//            Удостоверьтесь, что если
//    изменить значение, то при повторном вызове вернётся уже не старое, а новое.

//  KVTaskClient - создает http запросы по примеру из задания
//  к KVServer - 3 типа запросов - register/save/load
    // запросы register

    // запросы save


    // запросы load


// к HttpTaskServer запросы типа GET, POST, DELETE




    // получаем экземпляр класса-строителя
//    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//
//    // создаём объект, описывающий HTTP-запрос
//    HttpRequest request = requestBuilder
//            .GET()    // указываем HTTP-метод запроса
//            .uri(uri) // указываем адрес ресурса
//            .version(HttpClient.Version.HTTP_1_1) // указываем версию протокола HTTP
//            .header("Accept", "text/html") // указываем заголовок Accept
//            .build(); // заканчиваем настройку и создаём ("строим") HTTP-запрос
//



}

//    KVTaskClient - слой между HttpTaskManager и KVServer - для работы с KVServer
//    Ответстывенность
//
//   1. создание http запросов к KVServer
//
//    Ответственность
//   1.Регистрация клиентов
//   2.Сохранение и выгрузка данных

/*
Пишем HTTP-клиент
Для работы с хранилищем вам потребуется HTTP-клиент, который будет делегировать вызовы методов
в HTTP-запросы.
 Создайте класс KVTaskClient. Его будет использовать класс HttpTaskManager,
 который мы скоро напишем.

 */


