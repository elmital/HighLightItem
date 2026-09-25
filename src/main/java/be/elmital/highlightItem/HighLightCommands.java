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

import be.elmital.highlightItem.utils.ConfigUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.Identifier;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;


public class HighLightCommands {
    public static HighLightCommands inst() {
        return new HighLightCommands();
    }

    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, environment) -> dispatcher.register(literal("highlightitem")
                .then(literal("menu").executes(context -> {
                    Scheduler.queue(new Scheduler.Task(() -> Minecraft.getInstance().setScreenAndShow(new ConfigurationScreen(Minecraft.getInstance().options)), 1L));
                    return Command.SINGLE_SUCCESS;
                }))
                .then(literal("color")
                        .then(literal("custom")
                                .then(argument("red", IntegerArgumentType.integer(0, 255)).then(argument("green", IntegerArgumentType.integer(0, 255)).then(argument("blue", IntegerArgumentType.integer(0, 255)).then(argument("alpha", FloatArgumentType.floatArg(0.0f, 1.0f))
                                        .executes(context -> {
                                            HighlightItem.configurator.updateColor(new float[]{context.getArgument("red", int.class) / 255.0f,
                                                    context.getArgument("green", int.class) / 255.0f,
                                                    context.getArgument("blue", int.class) / 255.0f,
                                                    context.getArgument("alpha", float.class)}, null, context.getSource().getPlayer(), Configurator.NotificationContext.SENDING_COMMAND);
                                            return Command.SINGLE_SUCCESS;
                                        }))))
                                )
                        )
                        .then(argument("color", Colors.HighLightColorArgumentType.color())
                                .executes(context -> {
                                    var color = Colors.HighLightColorArgumentType.getColor("color", context);
                                    HighlightItem.configurator.updateColor(color.getShaderColor(), color, context.getSource().getPlayer(), Configurator.NotificationContext.SENDING_COMMAND);
                                    return Command.SINGLE_SUCCESS;
                                })
                        ))
                .then(literal("hoverColor")
                        .then(argument("hovered", Configurator.ColorHoveredOptions.Argument.COLOR_HOVERED_ARGUMENT)
                                .executes(context -> {
                                    HighlightItem.configurator.updateColorHovered(ConfigUtils.EnumArgumentType.getArguments("hovered", Configurator.ColorHoveredOptions.class, context), context.getSource().getPlayer(), Configurator.NotificationContext.SENDING_COMMAND);
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(literal("toggle").executes(context -> {
                    HighlightItem.configurator.updateToggle(context.getSource().getPlayer(), Configurator.NotificationContext.SENDING_COMMAND);
                    return Command.SINGLE_SUCCESS;
                })).then(literal("mode")
                        .then(argument("mode", ItemComparator.ComparatorArgumentType.comparator())
                                .executes(context -> {
                                    HighlightItem.configurator.updateMode(ItemComparator.ComparatorArgumentType.getComparator("mode", context), context.getSource().getPlayer(), Configurator.NotificationContext.SENDING_COMMAND);
                                    return Command.SINGLE_SUCCESS;
                                })))
        ));
    }

    public void registerArgumentTypes() {
        ArgumentTypeRegistry.registerArgumentType(Identifier.parse("highlightitem:color"), Colors.HighLightColorArgumentType.class, SingletonArgumentInfo.contextFree(Colors.HighLightColorArgumentType::color));
        ArgumentTypeRegistry.registerArgumentType(Identifier.parse("highlightitem:mode"), ItemComparator.ComparatorArgumentType.class, SingletonArgumentInfo.contextFree(ItemComparator.ComparatorArgumentType::comparator));
    }
}
