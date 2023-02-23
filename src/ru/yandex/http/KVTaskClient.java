package ru.yandex.http;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class KVTaskClient {

    HttpClient client = HttpClient.newHttpClient();
    URI url = URI.create("http://localhost:8080/tasks/task/");
    HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
}

    /*
    KVTaskClient - создает http запросы по примеру из задания к KVServer - 3 типа запросов - register/save/load
KVServer - дописываем 1 метод - load - по аналогии с другими метода, достаем из мапы data.get(key)
Парсинг LocalDateTime - можно настроить через TypeAdapter, JsonSerializer/JsonDeserializer
     */


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
Для работы с хранилищем вам потребуется HTTP-клиент, который будет делегировать вызовы методов в HTTP-запросы.
 Создайте класс KVTaskClient. Его будет использовать класс HttpTaskManager, который мы скоро напишем.
При создании KVTaskClient учтите следующее:
Конструктор принимает URL к серверу хранилища и регистрируется. При регистрации выдаётся токен (API_TOKEN),
который нужен при работе с сервером.
Метод void put(String key, String json) должен сохранять состояние менеджера задач через
запрос POST /save/<ключ>?API_TOKEN=.
Метод String load(String key) должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=.
Далее проверьте код клиента в main. Для этого запустите KVServer, создайте экземпляр KVTaskClient. Затем сохраните
значение под разными ключами и проверьте, что при запросе возвращаются нужные данные. Удостоверьтесь, что если
изменить значение, то при повторном вызове вернётся уже не старое, а новое.
 */


