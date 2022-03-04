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

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

import java.io.IOException;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import be.elmital.highlightItem.HighlightItem.HighLightColor;


public class HighLightCommands {
    public static HighLightCommands inst() {
        return new HighLightCommands();
    }

    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("highlightitem")
                    .then(literal("color")
                            .then(argument("color", HighLightColorArgumentType.color())
                                    .executes(context -> {
                                        var color = HighLightColorArgumentType.getColor("color", context);
                                        Configurator.HIGHLIGHT_COLOR = color;
                                        try {
                                            HighlightItem.configurator.updateConfig(Configurator.Config.COLOR, color.name());
                                            context.getSource().getServer().getPlayerManager().broadcast(Text.of("Color changed!"), MessageType.SYSTEM, context.getSource().getPlayer().getUuid());
                                        } catch (IOException e) {
                                            context.getSource().getServer().getPlayerManager().broadcast(Text.of("The config file can't be updated!"), MessageType.SYSTEM, context.getSource().getPlayer().getUuid());
                                        }
                                        return Command.SINGLE_SUCCESS;
                                    })
                            ))
                    .then(literal("hoverColor")
                            .then(argument("boolean", BoolArgumentType.bool())
                                    .executes(context -> {
                                        boolean bool =  BoolArgumentType.getBool(context, "boolean");
                                        Configurator.COLOR_HOVERED = bool;
                                        context.getSource().getServer().getPlayerManager().broadcast(Text.of(bool ? "Hovered item are now colored" : "Hovered item aren't colored"), MessageType.SYSTEM, context.getSource().getPlayer().getUuid());
                                        return Command.SINGLE_SUCCESS;
                                    }))
                    )
            );
        });
    }

    public void registerArgumentTypes() {
        ArgumentTypes.register("highlightitem:color", HighLightColorArgumentType.class, new ConstantArgumentSerializer<>(HighLightColorArgumentType::color));
    }
}
