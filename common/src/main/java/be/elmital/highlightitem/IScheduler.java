/*
 *  This file is part of the HighLightItem distribution (https://github.com/elmital/HighLightItem).
 *
 *  HighLightItem minecraft mod
 *  Copyright (C) 2022  elmital
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package be.elmital.highlightitem;

import java.util.ArrayList;

public interface IScheduler {
    ArrayList<Task> getTasks();

    void register();

    default void queue(Task task) {
        synchronized (getTasks()) {
            getTasks().add(task);
        }
    }

    default void remove(Task task) {
        synchronized (getTasks()) {
            getTasks().remove(task);
        }
    }

    class Task {
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
