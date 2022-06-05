import entity.Epic;
import entity.SubTask;
import manager.Manager;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Manager mng = new Manager();
        Epic epic = new Epic();
        SubTask subTask = new SubTask();
        List<SubTask> list = new ArrayList<>();

        subTask.setStatus("IN_PROGRESS");
        subTask.setId(1);
        subTask.setTitle("subtask");
        subTask.setDescription("test");
        mng.createSubtask(subTask);
        list.add(subTask);

        SubTask subTask1 = new SubTask();

        subTask1.setTitle("test2");
        subTask1.setDescription("test 2");
        subTask1.setId(2);
        subTask1.setStatus("DONE");
        mng.createSubtask(subTask1);
        list.add(subTask1);

        epic.setSubtasks(list);
        epic.setStatus();
        epic.setId(1);
        epic.setDescription("test");
        epic.setTitle("test epic");
        mng.createEpic(epic);

        System.out.println(mng.getEpics());
    }
}
