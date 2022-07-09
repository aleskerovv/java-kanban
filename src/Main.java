import entity.Epic;
import entity.SubTask;
import entity.Task;
import entity.TaskStatus;
import manager.Managers;
import manager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager mng = Managers.getDefault();
        Task task = new Task("Проверка тасок", "Проверка функционала по таскам", TaskStatus.NEW);
        mng.createTask(task);

        Task anotherTask = new Task("Вторая таска", "Проверка второй таски", TaskStatus.IN_PROGRESS);
        mng.createTask(anotherTask);

        Epic epic = new Epic("Первый эпик", "Описание первого эпика");
        mng.createEpic(epic);

        SubTask subTask = new SubTask("Первая сабтаска", "Проверяем функционал", TaskStatus.NEW, epic);
        SubTask anotherSubTask = new SubTask("Вторая сабтаска", "Снова Проверяем функционал", TaskStatus.NEW, epic);
        SubTask thirdSubTask = new SubTask("Третья сабтаска", "Проверка третьего сабтаска", TaskStatus.IN_PROGRESS, epic);
        mng.createSubtask(thirdSubTask);
        mng.createSubtask(subTask);
        mng.createSubtask(anotherSubTask);

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