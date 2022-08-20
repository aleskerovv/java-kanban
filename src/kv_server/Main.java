package kv_server;

import com.google.gson.Gson;
import http_server.HttpTaskServer;
import kv_task_client.KvTaskClient;
import manager.FileBackedTasksManager;
import manager.Managers;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
//        new HttpTaskServer().start();
        KvTaskClient client = new KvTaskClient("http://localhost:8078/");

        Gson gson = new Gson();
        FileBackedTasksManager manager = Managers.loadFromFile(new File("tasks.csv"));
        manager.createEntityFromText();
        String json = gson.toJson(manager.getAllTaskList());


        client.put("debug", json);

        client.load("debug");
    }
}

