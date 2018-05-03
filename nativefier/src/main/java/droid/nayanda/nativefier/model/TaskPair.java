package droid.nayanda.nativefier.model;

public class TaskPair<TObj> {
    private String fileName;
    private TObj object;

    public TaskPair(String fileName, TObj object) {
        this.fileName = fileName;
        this.object = object;
    }

    public String getFileName() {
        return fileName;
    }

    public TObj getObject() {
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskPair)) return false;

        TaskPair<?> taskPair = (TaskPair<?>) o;

        if (fileName != null ? !fileName.equals(taskPair.fileName) : taskPair.fileName != null)
            return false;
        return object != null ? object.equals(taskPair.object) : taskPair.object == null;
    }

    @Override
    public int hashCode() {
        int result = fileName != null ? fileName.hashCode() : 0;
        result = 31 * result + (object != null ? object.hashCode() : 0);
        return result;
    }
}
