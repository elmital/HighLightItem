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

    public static void init() {
        // TODO init all common stuff
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

            var com = HighLightCommands.inst();
            Constants.LOG.info("Registering commands...");
            com.register();
            Constants.LOG.info("Commands registered!");
            Constants.LOG.info("Registering command arguments...");
            com.registerArgumentTypes();
            Constants.LOG.info("Command arguments registered!");
            Constants.LOG.info("Mod init!");
        } catch (IOException | URISyntaxException e) {
            Constants.LOG.error("Can't setup mod properly !", e);
        } finally {
            Constants.LOG.info("First initialization phase ended!");
        }

        Constants.LOG.info("Client side initialization start");
        Constants.LOG.info("Registering key binds");
        // TODO keybinds
        /*KeyMapping.Category cat = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(HighlightItem.MOD_ID, "global"));
        Configurator.TOGGLE_BIND = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.highlightitem.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, cat));
        Configurator.COLOR_MENU = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.highlightitem.color_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, cat));
        Configurator.COLOR_HOVERED_BIND = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.highlightitem.color_hover", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, cat));
        Configurator.COMPARATOR_BIND = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.highlightitem.comparator", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, cat));
        */Constants.LOG.info("Key binds registered!");
        Constants.LOG.info("Registering client scheduler...");
        // TODO
        // Scheduler.register();
        Constants.LOG.info("Scheduler client registered!");

        Constants.LOG.info("Registering key bind and notification tracking");
        // TODO keybinds
        /*
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;
            if (Configurator.TOGGLE_BIND.consumeClick()) {
                HighlightItem.configurator.updateToggle(client.player, Configurator.NotificationContext.IN_GAME);
            }

            if (Configurator.COLOR_MENU.consumeClick()) {
                client.setScreenAndShow(new ConfigurationScreen(client.options));
            }

            if (Configurator.COLOR_HOVERED_BIND.consumeClick()) {
                HighlightItem.configurator.changeColorHovered(client.player, Configurator.NotificationContext.IN_GAME);
            }

            if (Configurator.COMPARATOR_BIND.consumeClick()) {
                HighlightItem.configurator.changeMode(client.player, Configurator.NotificationContext.IN_GAME);
            }
        });*/
        Constants.LOG.info("Client side initialization done!");
    }
}