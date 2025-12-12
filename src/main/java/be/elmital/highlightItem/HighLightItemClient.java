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

package be.elmital.highlightItem;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class HighLightItemClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        HighlightItem.LOGGER.info("Client side initialization start");
        HighlightItem.LOGGER.info("Registering key binds");
        KeyMapping.Category cat = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(HighlightItem.MOD_ID, "global"));
        Configurator.TOGGLE_BIND = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.highlightitem.toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, cat));
        Configurator.COLOR_MENU = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.highlightitem.color_menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, cat));
        Configurator.COLOR_HOVERED_BIND = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.highlightitem.color_hover", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, cat));
        Configurator.COMPARATOR_BIND = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.highlightitem.comparator", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, cat));
        HighlightItem.LOGGER.info("Key binds registered!");
        HighlightItem.LOGGER.info("Registering client scheduler...");
        Scheduler.register();
        HighlightItem.LOGGER.info("Scheduler client registered!");

        HighlightItem.LOGGER.info("Registering key bind and notification tracking");
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;
            if (Configurator.TOGGLE_BIND.consumeClick()) {
                HighlightItem.configurator.updateToggle(client.player, Configurator.NotificationContext.IN_GAME);
            }

            if (Configurator.COLOR_MENU.consumeClick()) {
                client.setScreen(new ConfigurationScreen(client.options));
            }

            if (Configurator.COLOR_HOVERED_BIND.consumeClick()) {
                HighlightItem.configurator.updateColorHovered(!Configurator.COLOR_HOVERED, client.player, Configurator.NotificationContext.IN_GAME);
            }

            if (Configurator.COMPARATOR_BIND.consumeClick()) {
                HighlightItem.configurator.changeMode(client.player, Configurator.NotificationContext.IN_GAME);
            }
        });
        HighlightItem.LOGGER.info("Client side initialization done!");
    }
}
