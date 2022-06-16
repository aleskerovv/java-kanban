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
        mng.createSubtask(subTask);
        mng.createSubtask(anotherSubTask);

        Epic anotherEpic = new Epic("Второй эпик", "Описание второго эпика");
        mng.createEpic(anotherEpic);

        SubTask thirdSubTask = new SubTask("Третья сабтаска", "Проверка третьего сабтаска", TaskStatus.IN_PROGRESS, anotherEpic);
        mng.createSubtask(thirdSubTask);

        //Проверка апдейта subTask в epic
        //Проверяем через создание нового объекта
/*        SubTask testSub = new SubTask("Новая сабтаска", "Проверка апдейта сабтаски", TaskStatus.IN_PROGRESS, epic);
        testSub.setId(4); //Генерируем для testSub поле id существующей subTask вручную
        System.out.println(mng.findEpicById(3));
        mng.updateSubTask(testSub);
        System.out.println(mng.findEpicById(3));*/

        //Проверка через изменения статуса уже существующей таски
/*        subTask.setStatus(TaskStatus.IN_PROGRESS);
        System.out.println(mng.findEpicById(3));
        mng.updateSubTask(subTask);
        System.out.println(mng.findEpicById(3));*/

        //Проверка новой реализации удаления subTask
/*        System.out.println(mng.findEpicById(3));
        mng.deleteSubTaskById(4);
        System.out.println(mng.findEpicById(3));*/

/*        mng.findTasksById(1);
        mng.findTasksById(2);
        mng.findEpicById(3);
        mng.findSubTasksById(4);
        mng.findSubTasksById(5);
        mng.findSubTasksById(5);
        mng.findSubTasksById(5);
        mng.findSubTasksById(5);
        mng.findSubTasksById(5);
        mng.findSubTasksById(5);
        mng.findSubTasksById(5); //Добавил в поиск 11 экземпляр, если раскомментировать и запустить, то он добавится в конец, а самый старый экземпляр удалится
        System.out.println(mng.getHistory());*/
    }
}