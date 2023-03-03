package ru.yandex.http;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String url;
    private String apiToken;                                         // токен из KVServer'а
    private HttpClient client;                                       // экземпляр для отправки запросов на серв
    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
    int codeForTestFunctionSaveToKVServer;

    public KVTaskClient(String url) {    // "http://localhost:8078/     приходит из менеджера
        this.url = url;
        this.apiToken = null;
        client = HttpClient.newHttpClient();

        URI registerUrl = URI.create(url + "register");
        HttpRequest apiRequest = HttpRequest
                .newBuilder()
                .uri(registerUrl)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                this.apiToken = jsonElement.getAsString();
                System.out.println("Регистрация ключа прошла успешно!");
            } else {
                System.out.println("Соединение с сервером не установлено. Код состояния: " + response.statusCode());
               }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Во время регистрации возникла ошибка.\n"
                    + "Проверьте, пожалуйста, адрес и повторите попытку");
        }
    }

    public void put(String key, String json) {
        URI urlPut = URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest requestPut = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(urlPut)
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(requestPut, handler);
            setCodeForTestFunctionSaveToKVServer(response.statusCode());
            if (response.statusCode() == 200) {
                System.out.println("Всё хорошо :) Информация сохранена на сервере");
            } else {
                System.out.println("Сервер не дал ответ. Вернулся код: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Во время запроса данных произошла ошибка ");
        }
    }

    public String load(String key)  {
//  Метод должен возвращать состояние менеджера задач через запрос GET /load/<ключ>?API_TOKEN=.
////    Далее проверьте код клиента в main.
        URI urlLoad = URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken);

        HttpRequest requestLoad = requestBuilder
                .uri(urlLoad)
                .header("Accept", "application/json")     // может быть, не нужен, а, может, надо стереть
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        try {
            HttpResponse<String> response = client.send(requestLoad, handler);
            System.out.println("Код состояния: " + response.statusCode());

            if (response.statusCode() == 200) {
                // пробуем вытащить jsonElement
                JsonElement jsonElement = JsonParser.parseString(response.body());  // узнать в каком виде попадает в мапу
                // JsonObject jObj = jsonElement.getAsJsonObject();

            return jsonElement.toString();   //  jObj.getAsString();
            }
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + requestLoad.uri()
                    + "', возникла ошибка.\n" + "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return null;
    }

    public void setCodeForTestFunctionSaveToKVServer(int codeForTestFunctionSaveToKVServer) {
        this.codeForTestFunctionSaveToKVServer = codeForTestFunctionSaveToKVServer;
    }

    public int getCodeForTestFunctionSaveToKVServer() {
        return codeForTestFunctionSaveToKVServer;
    }
}

// return jsonElement.toString();

