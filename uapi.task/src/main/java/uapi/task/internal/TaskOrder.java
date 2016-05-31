/**
 * Copyright (C) 2010 The UAPI Authors
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the LICENSE file.
 *
 * You must gained the permission from the authors if you want to
 * use the project into a commercial product
 */

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
