package ru.yandex.tasks;

import java.util.TreeSet;

public class tmp {
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>((b,o) -> {
        if (b.getStartTime() == null || o.getStartTime() == null) {
            return 1;
        }
        if (b.getStartTime().isBefore(o.getStartTime())) {
            return -1;
        } else if (b.getStartTime().isBefore(o.getStartTime())) {
            return 1;
        } else return b.getId() - o.getId();
    });


}
