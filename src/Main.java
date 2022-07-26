import entity.Epic;
import entity.SubTask;
import entity.Task;
import entity.TaskType;
import manager.Managers;
import manager.TaskManager;

import static entity.TaskStatus.*;
import static entity.TaskType.*;

public class Main {
    public static void main(String[] args) {
        TaskManager mng = Managers.getDefault();
        Task task = new Task("Проверка тасок", "Проверка функционала по таскам", TASK, NEW);
        mng.createTask(task);

        Task anotherTask = new Task("Вторая таска", "Проверка второй таски", TASK, IN_PROGRESS);
        mng.createTask(anotherTask);

        Epic epic = new Epic("Первый эпик", "Описание первого эпика", EPIC);
        mng.createEpic(epic);

        SubTask subTask = new SubTask("Первая сабтаска", "Проверяем функционал", SUBTASK, NEW, 3);
        mng.createSubtask(subTask);

        SubTask anotherSubTask = new SubTask("Вторая сабтаска", "Снова Проверяем функционал", SUBTASK, DONE, 3);
        mng.createSubtask(anotherSubTask);

        SubTask thirdSubTask = new SubTask("Третья сабтаска", "Проверка третьего сабтаска", SUBTASK, DONE, 3);
        mng.createSubtask(thirdSubTask);

        Epic anotherEpic = new Epic("Второй эпик", "Описание второго эпика", EPIC);
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

        //Проверка инкремент id
        SubTask subTaskForTest = new SubTask("Третья сабтаска", "Проверка третьего сабтаска", DONE, 3);
        mng.createSubtask(subTaskForTest);

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