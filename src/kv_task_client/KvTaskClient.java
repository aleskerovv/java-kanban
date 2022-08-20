package kv_task_client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KvTaskClient {
    String url;
    HttpClient client;

    public KvTaskClient(String url) {
        this.url = url;
        this.client = HttpClient.newHttpClient();
    }

    public void put(String key, String json) {
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=DEBUG");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("An error occurred while executing the request.\n" +
                    "Endpoint must be '/save'");
        }
    }

    public String load(String key) {
        String body = "";
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=DEBUG");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            body = response.body();
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("An error occurred while executing the request.\n" +
                    "Endpoint must be '/load'");
        }

        return body;
    }
}
