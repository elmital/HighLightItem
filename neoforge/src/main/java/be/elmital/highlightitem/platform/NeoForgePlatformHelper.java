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

package be.elmital.highlightitem.platform;

import be.elmital.highlightitem.IScheduler;
import be.elmital.highlightitem.platform.services.IPlatformHelper;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public KeyMapping registerKeyBind(KeyMapping keyMapping) {
        // TODO
        return null;
    }

    @Override
    public IScheduler getScheduler() {
        // TODO
        return null;
    }

    @Override
    public Path getConfigDir() {
        // TODO
        return null;
    }

    @Override
    public void registerArgumentType(Identifier identifier, Class<? extends ArgumentType<?>> argumentTypeClass, SingletonArgumentInfo<ArgumentType<?>> argumentTypeSingletonArgumentInfo) {
        // TODO
    }
}