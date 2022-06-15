import entity.Epic;
import entity.SubTask;
import entity.Task;
import entity.TaskStatus;
import manager.InMemoryTaskManager;
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
        mng.createSubtask(subTask);
        mng.createSubtask(anotherSubTask);

        Epic anotherEpic = new Epic("Второй эпик", "Описание второго эпика");
        mng.createEpic(anotherEpic);

        SubTask thirdSubTask = new SubTask("Третья сабтаска", "Проверка третьего сабтаска", TaskStatus.IN_PROGRESS, anotherEpic);
        mng.createSubtask(thirdSubTask);

        mng.findTasksById(1);
        mng.findTasksById(2);
        mng.findEpicById(3);
        mng.findSubTasksById(4);
        mng.findSubTasksById(5);
        mng.findSubTasksById(5);
        mng.findSubTasksById(5);
        mng.findSubTasksById(5);
        mng.findSubTasksById(5);
        mng.findSubTasksById(5);
//        mng.findSubTasksById(5); //Добавил в поиск 11 экземпляр, если раскомментировать и запустить, то он добавится в конец, а самый старый экземпляр удалится
        System.out.println(mng.getHistory());
//        Есть вопрос, касаемо вывода истории просмотров. Правильно ли реализован доступ к методу?
    }
}