package be.elmital.highlightItem;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;

public class Scheduler implements ClientTickEvents.EndTick {
    public static final Scheduler INSTANCE = new Scheduler();
    private final ArrayList<Task> tasks = new ArrayList<>();

    @Override
    public void onEndTick(Minecraft client) {
        ArrayList<Task> toRemove = new ArrayList<>();
        synchronized (this.tasks) {
            for (Task task : tasks) {
                if (--task.ticksUntilSomething == 0L) {
                    task.runnable.run();
                    if (task.period != null)
                        task.ticksUntilSomething = task.period;
                    else
                        toRemove.add(task);
                }
            }

            tasks.removeAll(toRemove);
        }
    }

    static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(INSTANCE);
    }

    public static void queue(Task task) {
        synchronized (INSTANCE.tasks) {
            INSTANCE.tasks.add(task);
        }
    }

    public static void remove(Task task) {
        synchronized (INSTANCE.tasks) {
            INSTANCE.tasks.add(task);
        }
    }

    public static class Task {
        final Runnable runnable;
        final Long period;
        long ticksUntilSomething;

        public Task(Runnable runnable) {
            this(runnable, null, null);
        }

        public Task(Runnable runnable, Long delay) {
            this(runnable, delay, null);
        }

        public Task(Runnable runnable, Long delay, Long period) {
            this.runnable = runnable;
            this.ticksUntilSomething = delay == null ? 0L : delay;
            this.period = period;
        }
    }
}
