package uapi.task.internal;

import java.util.Comparator;

import uapi.task.ITask;

/**
 * Used for comparing two task order
 * 
 * @author min
 */
final class TaskOrder implements Comparator<ITask> {

    @Override
    public int compare(ITask task1, ITask task2) {
        if (task1 == task2) {
            return 0;
        }
        if (task1.getPriority() == task2.getPriority()) {
            return 0;
        } else if (task1.getPriority() < task2.getPriority()) {
            return -1;
        } else {
            return 1;
        }
    }
}
