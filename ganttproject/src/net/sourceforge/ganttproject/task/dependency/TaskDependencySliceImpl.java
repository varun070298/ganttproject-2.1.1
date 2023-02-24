package net.sourceforge.ganttproject.task.dependency;

import java.util.List;
import net.sourceforge.ganttproject.task.Task;


public class TaskDependencySliceImpl implements TaskDependencySlice {
    private final Task myTask;

    private final TaskDependencyCollection myDependencyCollection;

    public TaskDependencySliceImpl(Task task,
            TaskDependencyCollection dependencyCollection) {
        myTask = task;
        myDependencyCollection = dependencyCollection;
    }

    public TaskDependency[] toArray() {
        return myDependencyCollection.getDependencies(myTask);
    }
    
    public void clear() {
        TaskDependency[] deps = toArray();
        for (int i = 0; i < deps.length; i++) {
            deps[i].delete();
        }
    }
    // This function unlinks only tasks that are selected and leaves links to not selected tasks.
    public void clear(List selection) {
        TaskDependency[] deps = toArray();
        for (int i = 0; i < deps.length; i++) {
            if (selection.contains(deps[i].getDependant()) && selection.contains(deps[i].getDependee())) {
                deps[i].delete();
            }
        }
    }

    protected Task getTask() {
        return myTask;
    }

    protected TaskDependencyCollection getDependencyCollection() {
        return myDependencyCollection;
    }
}
