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

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = "highlightitem", dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class HighLightItem {

    public HighLightItem(IEventBus eventBus) {
        HighLightItemCommon.init();
    }

    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        Constants.LOG.info("Registering key binds and notification tracking");
        // Register category
        event.registerCategory(HighLightItemCommon.keyBindCategory);

        // Register binding with category used
        KeyMapping.Category.register(HighLightItemCommon.keyBindCategory.id());
        for (KeyMapping keyMapping : HighLightItemCommon.keyMappings) {
            event.register(keyMapping);
        }
    }

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        HighLightCommands.registerClientSide(event.getDispatcher(), new HighLightCommands<>() {
            @Override
            void sendClientFeedBack(CommandSourceStack clientCommandSource, Component text) {
                HighLightCommands.registerArgumentTypes();
                clientCommandSource.sendSystemMessage(text);
            }
        });
    }

    @SubscribeEvent // on the game event bus only on the physical client
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Configurator.TOGGLE_BIND.consumeClick()) {
            HighLightItemCommon.configurator.updateToggle(Minecraft.getInstance().player, Configurator.NotificationContext.IN_GAME);
        }

        if (Configurator.COLOR_MENU.consumeClick()) {
            Minecraft.getInstance().setScreenAndShow(new ConfigurationScreen(Minecraft.getInstance().options));
        }

        if (Configurator.COLOR_HOVERED_BIND.consumeClick()) {
            HighLightItemCommon.configurator.changeColorHovered(Minecraft.getInstance().player, Configurator.NotificationContext.IN_GAME);
        }

        if (Configurator.COMPARATOR_BIND.consumeClick()) {
            HighLightItemCommon.configurator.changeMode(Minecraft.getInstance().player, Configurator.NotificationContext.IN_GAME);
        }
    }
}