import entity.Epic;
import entity.SubTask;
import entity.Task;
import manager.Manager;

public class Main {
    public static void main(String[] args) {
        Manager mng = new Manager();
        Task task = new Task("Проверка тасок", "Проверка функционала по таскам", "NEW");
        mng.createTask(task);

        Task anotherTask = new Task("Вторая таска", "Проверка второй таски", "IN_PROGRESS");
        mng.createTask(anotherTask);

        Epic epic = new Epic("Первый эпик", "Описание первого эпика");
        mng.createEpic(epic);

        SubTask subTask = new SubTask("Первая сабтаска", "Проверяем функционал", "NEW", epic);
        SubTask anotherSubTask = new SubTask("Вторая сабтаска", "Снова Проверяем функционал", "NEW", epic);
        mng.createSubtask(subTask);
        mng.createSubtask(anotherSubTask);

        Epic anotherEpic = new Epic("Второй эпик", "Описание второго эпика");
        mng.createEpic(anotherEpic);

        SubTask thirdSubTask = new SubTask("Третья сабтаска", "Проверка третьего сабтаска", "IN_PROGRESS", anotherEpic);
        mng.createSubtask(thirdSubTask);

        //Проверка обновления статуса у эпика, если убрать у него все задачи
/*        System.out.println(mng.findEpicById(5));
        mng.deleteSubTaskById(6);
        System.out.println(mng.findEpicById(5));*/

        //Проверка обновления статуса у эпика, если у задач разные статусы, а после обновление при апдейте сабтаски
/*        System.out.println(mng.findEpicById(2));
        subTask.setStatus("DONE"); //Меняем статус с NEW на DONE
        mng.updateSubTask(subTask);
        System.out.println(mng.findEpicById(2));*/

        //Проверка на удаление всех эпиков(вместе с ними и сабтасок) и тасок
/*        mng.clearTasks();
        mng.clearEpicsList();
        System.out.println(mng.getAllTaskList());
        System.out.println(mng.getTasks());
        System.out.println(mng.getEpics());
        System.out.println(mng.getSubtasks());*/

        //Проверяем вывод всех список тасок
/*        System.out.println(mng.getAllTaskList());
        System.out.println(mng.getTasks());
        System.out.println(mng.getEpics());
        System.out.println(mng.getSubtasks());*/

        //Проверяем удаление эпика и его сабтасок
/*        System.out.println(mng.getEpics());
        System.out.println(mng.getSubtasks());
        mng.deleteEpicById(2);
        System.out.println(mng.getEpics());
        System.out.println(mng.getSubtasks());*/

        //Проверка на изменение статуса таски  и очистка списка тасок
/*        System.out.println(mng.getTasks());
        anotherTask.setStatus("DONE");
        mng.updateTask(anotherTask);
        System.out.println(mng.getTasks());
        mng.clearTasks();
        System.out.println(mng.getTasks());*/
    }
}