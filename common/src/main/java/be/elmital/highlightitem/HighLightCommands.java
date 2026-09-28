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
import be.elmital.highlightitem.utils.ConfigUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.function.TriConsumer;


public abstract class HighLightCommands<S extends SharedSuggestionProvider> {
    static void registerArgumentTypes() {
        Constants.LOG.info("Registering commands argument types...");
        Services.PLATFORM.registerArgumentType(Identifier.parse("highlightitem:color"), Colors.HighLightColorArgumentType.class, SingletonArgumentInfo.contextFree(Colors.HighLightColorArgumentType::color));
        Services.PLATFORM.registerArgumentType(Identifier.parse("highlightitem:mode"), ItemComparator.ComparatorArgumentType.class, SingletonArgumentInfo.contextFree(ItemComparator.ComparatorArgumentType::comparator));
        Constants.LOG.info("Commands argument types registered");

    }

    static <S extends SharedSuggestionProvider> void registerClientSide(CommandDispatcher<S> commandDispatcher, HighLightCommands<S> commands) {
        Constants.LOG.info("Registering commands client side...");
        commandDispatcher.register(HighLightCommands.commandNodeBuilder((source, text, aBoolean) -> commands.sendClientFeedBack(source, text)));
        Constants.LOG.info("Client commands registered!");
    }

    abstract void sendClientFeedBack(S clientCommandSource, Component text);

    public static <S extends SharedSuggestionProvider> LiteralArgumentBuilder<S> commandNodeBuilder(TriConsumer<S, Component, Boolean> sourceNotification) {
        return LiteralArgumentBuilder.<S>literal("highlightitem")
                .then(LiteralArgumentBuilder.<S>literal("menu").executes(context -> {
                    Services.PLATFORM.getScheduler().queue(new IScheduler.Task(() -> Minecraft.getInstance().setScreenAndShow(new ConfigurationScreen(Minecraft.getInstance().options)), 1L));
                    return Command.SINGLE_SUCCESS;
                }))
                .then(LiteralArgumentBuilder.<S>literal("color")
                        .then(LiteralArgumentBuilder.<S>literal("custom")
                                .then(RequiredArgumentBuilder.<S,Integer>argument("red", IntegerArgumentType.integer(0, 255)).then(RequiredArgumentBuilder.<S,Integer>argument("green", IntegerArgumentType.integer(0, 255)).then(RequiredArgumentBuilder.<S,Integer>argument("blue", IntegerArgumentType.integer(0, 255)).then(RequiredArgumentBuilder.<S,Float>argument("alpha", FloatArgumentType.floatArg(0.0f, 1.0f))
                                        .executes(context -> {
                                            HighLightItemCommon.configurator.updateColor(new float[]{context.getArgument("red", int.class) / 255.0f,
                                                    context.getArgument("green", int.class) / 255.0f,
                                                    context.getArgument("blue", int.class) / 255.0f,
                                                    context.getArgument("alpha", float.class)}, null, Minecraft.getInstance().player, Configurator.NotificationContext.SENDING_COMMAND);
                                            return Command.SINGLE_SUCCESS;
                                        }))))
                                )
                        )
                        .then(RequiredArgumentBuilder.<S, Colors.HighLightColor>argument("color", Colors.HighLightColorArgumentType.color())
                                .executes(context -> {
                                    var color = Colors.HighLightColorArgumentType.getColor("color", context);
                                    HighLightItemCommon.configurator.updateColor(color.getShaderColor(), color, Minecraft.getInstance().player, Configurator.NotificationContext.SENDING_COMMAND);
                                    return Command.SINGLE_SUCCESS;
                                })
                        ))
                .then(LiteralArgumentBuilder.<S>literal("hoverColor")
                        .then(RequiredArgumentBuilder.<S, Configurator.ColorHoveredOptions>argument("hovered", Configurator.ColorHoveredOptions.Argument.COLOR_HOVERED_ARGUMENT)
                                .executes(context -> {
                                    HighLightItemCommon.configurator.updateColorHovered(ConfigUtils.EnumArgumentType.getArguments("hovered", Configurator.ColorHoveredOptions.class, context), Minecraft.getInstance().player, Configurator.NotificationContext.SENDING_COMMAND);
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(LiteralArgumentBuilder.<S>literal("toggle").executes(context -> {
                    HighLightItemCommon.configurator.updateToggle(Minecraft.getInstance().player, Configurator.NotificationContext.SENDING_COMMAND);
                    return Command.SINGLE_SUCCESS;
                })).then(LiteralArgumentBuilder.<S>literal("mode")
                        .then(RequiredArgumentBuilder.<S, ItemComparator.Comparators>argument("mode", ItemComparator.ComparatorArgumentType.comparator())
                                .executes(context -> {
                                    HighLightItemCommon.configurator.updateMode(ItemComparator.ComparatorArgumentType.getComparator("mode", context), Minecraft.getInstance().player, Configurator.NotificationContext.SENDING_COMMAND);
                                    return Command.SINGLE_SUCCESS;
                                })));
    }
}
