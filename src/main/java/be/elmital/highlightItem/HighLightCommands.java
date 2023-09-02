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
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.IOException;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class HighLightCommands {
    public static HighLightCommands inst() {
        return new HighLightCommands();
    }

    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, environment) -> dispatcher.register(literal("highlightitem")
                .then(literal("color")
                        .then(argument("color", HighLightColorArgumentType.color())
                                .executes(context -> {
                                    var color = HighLightColorArgumentType.getColor("color", context);
                                    Configurator.HIGHLIGHT_COLOR = color;
                                    try {
                                        HighlightItem.configurator.updateConfig(Configurator.Config.COLOR, color.name());
                                        context.getSource().getPlayer().sendMessage(Text.of("Color changed!"));
                                    } catch (IOException e) {
                                        context.getSource().getPlayer().sendMessage(Text.of("The config file can't be updated!"));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        ))
                .then(literal("hoverColor")
                        .then(argument("boolean", BoolArgumentType.bool())
                                .executes(context -> {
                                    Configurator.COLOR_HOVERED = BoolArgumentType.getBool(context, "boolean");
                                    try {
                                        HighlightItem.configurator.updateConfig(Configurator.Config.COLOR_HOVERED, "" + Configurator.COLOR_HOVERED);
                                        context.getSource().getPlayer().sendMessage(Text.of(Configurator.COLOR_HOVERED ? "Hovered item are now colored" : "Hovered item aren't colored"));
                                    } catch (IOException e) {
                                        context.getSource().getPlayer().sendMessage(Text.of("The config file can't be updated!"));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(literal("toggle").executes(context -> {
                    Configurator.TOGGLE = !Configurator.TOGGLE;
                    try {
                        HighlightItem.configurator.updateConfig(Configurator.Config.TOGGLE, "" + Configurator.TOGGLE);
                        context.getSource().getPlayer().sendMessage(Text.of("Highlighting is " + (Configurator.TOGGLE ? "activated!" : "deactivated!")));
                    } catch (IOException e) {
                        context.getSource().getPlayer().sendMessage(Text.of("The config file can't be updated!"));
                    }
                    return Command.SINGLE_SUCCESS;
                })).then(literal("mode")
                        .then(argument("mode", ItemComparator.ComparatorArgumentType.comparator())
                                .executes(context -> {
                                    var mode = ItemComparator.ComparatorArgumentType.getComparator("mode", context);
                                    Configurator.COMPARATOR = mode;
                                    try {
                                        HighlightItem.configurator.updateConfig(Configurator.Config.COMPARATOR, mode.name());
                                        context.getSource().getPlayer().sendMessage(Text.of("Mode changed!"));
                                    } catch (IOException e) {
                                        context.getSource().getPlayer().sendMessage(Text.of("The config file can't be updated!"));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })))
        ));
    }

    public void registerArgumentTypes() {
        ArgumentTypeRegistry.registerArgumentType(new Identifier("highlightitem:color"), HighLightColorArgumentType.class, ConstantArgumentSerializer.of(HighLightColorArgumentType::color));
        ArgumentTypeRegistry.registerArgumentType(new Identifier("highlightitem:mode"), ItemComparator.ComparatorArgumentType.class, ConstantArgumentSerializer.of(ItemComparator.ComparatorArgumentType::comparator));
    }
}
