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


import be.elmital.highlightitem.platform.Services;
import be.elmital.highlightitem.utils.ReturningHashSet;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.net.URISyntaxException;

public class HighLightItemCommon {
    public static Slot toDrawFromMod = null;
    public static Configurator configurator;
    public static KeyMapping.Category keyBindCategory;
    public static ReturningHashSet<KeyMapping> keyMappings;

    public static void init() {
        Constants.LOG.info("""
    
				-------------
				 HighLightItem
				 Copyright (C) 2022  elmital
				 This program comes with ABSOLUTELY NO WARRANTY.
				 This is free software, and you are welcome to redistribute it
				 under certain conditions.
				 See the GNU General Public License for more details. <https://www.gnu.org/licenses/>
				-------------
				""");
        try {
            Constants.LOG.info("Checking for configuration file");
            configurator = Configurator.getInstance();
            Constants.LOG.info("Config file loaded!");
            Constants.LOG.info("Mod init!");
        } catch (IOException | URISyntaxException e) {
            Constants.LOG.error("Can't setup mod properly !", e);
        } finally {
            Constants.LOG.info("First initialization phase ended!");
        }

        keyMappings = new ReturningHashSet<>();
        Constants.LOG.info("Client side initialization start");
        Constants.LOG.info("Generate key binds");
        keyBindCategory = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "global"));
        Configurator.TOGGLE_BIND = keyMappings.addAndReturn(new KeyMapping("key.highlightitem.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, keyBindCategory));
        Configurator.COLOR_MENU = keyMappings.addAndReturn(new KeyMapping("key.highlightitem.color_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, keyBindCategory));
        Configurator.COLOR_HOVERED_BIND = keyMappings.addAndReturn(new KeyMapping("key.highlightitem.color_hover", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, keyBindCategory));
        Configurator.COMPARATOR_BIND = keyMappings.addAndReturn(new KeyMapping("key.highlightitem.comparator", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, keyBindCategory));
        Constants.LOG.info("Key binds generated!");
        Constants.LOG.info("Registering client scheduler...");
        Services.PLATFORM.getScheduler().register();
        Constants.LOG.info("Scheduler client registered!");
        Constants.LOG.info("Client side initialization done!");
    }
}