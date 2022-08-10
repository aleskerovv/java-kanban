import manager.Managers;
import manager.TaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {
    public InMemoryTaskManagerTest() {
        super.manager = Managers.getDefault();
    }
}
