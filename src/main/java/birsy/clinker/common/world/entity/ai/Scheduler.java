package birsy.clinker.common.world.entity.ai;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    final List<ScheduledTask> tasks = new ArrayList<>();

    public Scheduler() {}

    public void tick() {
        tasks.removeIf(ScheduledTask::tick);
    }

    public ScheduledTask schedule(int delay, Runnable callback) {
        ScheduledTask scheduledTask = new ScheduledTask(delay, callback);
        this.tasks.add(scheduledTask);
        return scheduledTask;
    }

    public static class ScheduledTask {
        final Runnable task;
        int timeRemaining;
        int duration;
        boolean canceled = false;

        private ScheduledTask(int duration, Runnable task) {
            this.duration = duration;
            this.timeRemaining = duration;
            this.task = task;
        }

        boolean tick() {
            timeRemaining--;
            if (timeRemaining <= 0) task.run();
            return timeRemaining <= 0 || canceled;
        }

        public void cancel() {
            canceled = true;
        }

        public int getDuration() {
            return duration;
        }

        public int getTimeRemaining() {
            return timeRemaining;
        }
    }
}
