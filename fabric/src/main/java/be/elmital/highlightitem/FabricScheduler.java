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

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class FabricScheduler implements IScheduler, ClientTickEvents.EndTick {
    public static final FabricScheduler INSTANCE = new FabricScheduler();
    private final ArrayList<Task> tasks = new ArrayList<>();

    @Override
    public ArrayList<IScheduler.Task> getTasks() {
        return this.tasks;
    }

    @Override
    public void register() {
        ClientTickEvents.END_CLIENT_TICK.register(INSTANCE);
    }

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
}
