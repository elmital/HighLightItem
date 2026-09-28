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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public class HighLightItem implements ModInitializer {

    @Override
    public void onInitialize() {
        HighLightItemCommon.init();

        HighLightCommands.registerArgumentTypes();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, _) -> HighLightCommands.registerClientSide(dispatcher, new HighLightCommands<>() {
            @Override
            void sendClientFeedBack(FabricClientCommandSource clientCommandSource, Component text) {
                clientCommandSource.sendFeedback(text);
            }
        }));

        Constants.LOG.info("Registering key binds and notification tracking");
        KeyMapping.Category.register(HighLightItemCommon.keyBindCategory.id());
        for (KeyMapping keyMapping : HighLightItemCommon.keyMappings) {
            KeyMappingHelper.registerKeyMapping(keyMapping);
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;
            if (Configurator.TOGGLE_BIND.consumeClick()) {
                HighLightItemCommon.configurator.updateToggle(client.player, Configurator.NotificationContext.IN_GAME);
            }

            if (Configurator.COLOR_MENU.consumeClick()) {
                client.setScreenAndShow(new ConfigurationScreen(client.options));
            }

            if (Configurator.COLOR_HOVERED_BIND.consumeClick()) {
                HighLightItemCommon.configurator.changeColorHovered(client.player, Configurator.NotificationContext.IN_GAME);
            }

            if (Configurator.COMPARATOR_BIND.consumeClick()) {
                HighLightItemCommon.configurator.changeMode(client.player, Configurator.NotificationContext.IN_GAME);
            }
        });
    }
}
