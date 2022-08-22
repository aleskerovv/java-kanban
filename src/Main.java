import entity.Epic;
import entity.SubTask;
import entity.Task;
import http_server.HttpTaskServer;
import kv_server.KVServer;
import manager.FileBackedTasksManager;
import manager.HttpTaskManager;
import manager.Managers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static entity.TaskStatus.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        //Здесь можно проверить работу HttpTaskServer
//        new HttpTaskServer(Managers.loadFromFile(new File("httpManager.csv"))).start();
        //Здесь полный функционал KVServer + HttpTaskManager
//        new KVServer().start();
//        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078");
//        Task task1 = new Task("Первая таска", "описание", NEW, 50, LocalDateTime.parse("2022-08-01T11:10:00"));
//        Task task2 = new Task("Вторая таска", "описание", DONE, 50);
//        Task task3 = new Task("Третья таска", "описание", IN_PROGRESS, 60, LocalDateTime.parse("2022-08-09T12:30:00"));
//        Task task4 = new Task("Четвертая", "описание", IN_PROGRESS, 60, LocalDateTime.parse("2022-08-09T13:30:00"));
//        Epic epic1 = new Epic("Эпик", "описание");
//        SubTask subTask1 = new SubTask("Первая сабтаска", "описание", NEW, 40, LocalDateTime.parse("2022-08-05T13:50:00"), 5);
//        SubTask subTask2 = new SubTask("Вторая сабтаска", "описание", NEW, 40, LocalDateTime.parse("2022-08-06T14:30:00"), 5);
//        manager.createTask(task1);
//        manager.createTask(task2);
//        manager.createTask(task3);
//        manager.createTask(task4);
//        manager.createEpic(epic1);
//        manager.createSubtask(subTask1);
//        manager.createSubtask(subTask2);
//        manager.findEpicById(5);
//        manager.findTasksById(1);
//        manager.findTasksById(4);
//        manager.findEpicById(5);
//        Создаем новый HttpManager через метод loadFromServer и проверяем состояние
//        HttpTaskManager newManager = manager.loadFromServer("taskManager");
//        System.out.println(newManager.getHistory());
//        System.out.println(newManager.getPrioritizedTasks());

        FileBackedTasksManager manager = new FileBackedTasksManager(new File("tasks.csv"));
        manager.createEntityFromText();
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }
}