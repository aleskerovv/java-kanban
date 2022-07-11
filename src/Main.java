import entity.Epic;
import entity.SubTask;
import entity.Task;
import manager.Managers;
import manager.TaskManager;

import static entity.TaskStatus.*;

public class Main {
    public static void main(String[] args) {
        TaskManager mng = Managers.getDefault();
        Task task = new Task("Проверка тасок", "Проверка функционала по таскам", NEW);
        mng.createTask(task);

        Task anotherTask = new Task("Вторая таска", "Проверка второй таски", IN_PROGRESS);
        mng.createTask(anotherTask);

        Epic epic = new Epic("Первый эпик", "Описание первого эпика");
        mng.createEpic(epic);

        SubTask subTask = new SubTask("Первая сабтаска", "Проверяем функционал", NEW, 3);
        mng.createSubtask(subTask);

        SubTask anotherSubTask = new SubTask("Вторая сабтаска", "Снова Проверяем функционал", DONE, 3);
        mng.createSubtask(anotherSubTask);

        SubTask thirdSubTask = new SubTask("Третья сабтаска", "Проверка третьего сабтаска", DONE, 3);
        mng.createSubtask(thirdSubTask);

        Epic anotherEpic = new Epic("Второй эпик", "Описание второго эпика");
        mng.createEpic(anotherEpic);

        //Тесты с Epic и SubTask
/*        mng.findSubTasksById(4);
        System.out.println(mng.getHistory());
        mng.findSubTasksById(5);
        System.out.println(mng.getHistory());
        mng.findSubTasksById(6);
        System.out.println(mng.getHistory());
        mng.findSubTasksById(5);
        System.out.println(mng.getHistory());
        mng.findEpicById(3);
        System.out.println(mng.getHistory());
        mng.findSubTasksById(6);
        System.out.println(mng.getHistory());
        mng.findSubTasksById(6);
        System.out.println(mng.getHistory());
        mng.findSubTasksById(4);
        System.out.println(mng.getHistory());

        //Проверяем, что после очистки всего листа SubTask статус Epic == NEW
        mng.clearSubtasksList();
        System.out.println(mng.getHistory());

        mng.deleteEpicById(3);
        System.out.println(mng.getHistory());*/

    //Тесты с Task
/*        mng.findTasksById(1);
        System.out.println(mng.getHistory());
        mng.findTasksById(2);
        System.out.println(mng.getHistory());
        mng.findTasksById(1);
        System.out.println(mng.getHistory());

        mng.deleteTaskById(2);
        System.out.println(mng.getHistory());*/
    }
}