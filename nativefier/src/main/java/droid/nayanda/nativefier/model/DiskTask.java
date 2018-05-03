package droid.nayanda.nativefier.model;

public class DiskTask<TPairObj> {

    private DiskTask.TaskType type;
    private TaskPair<TPairObj> task;

    public DiskTask(DiskTask.TaskType type, TaskPair<TPairObj> task) {
        this.type = type;
        this.task = task;
    }

    public DiskTask.TaskType getType() {
        return type;
    }

    public TaskPair<TPairObj> getTask() {
        return task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiskTask)) return false;

        DiskTask<?> task1 = (DiskTask<?>) o;

        if (type != task1.type) return false;
        return task != null ? task.equals(task1.task) : task1.task == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (task != null ? task.hashCode() : 0);
        return result;
    }

    public enum TaskType {
        WRITE, DELETE
    }
}
