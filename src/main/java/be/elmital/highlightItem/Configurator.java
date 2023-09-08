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

import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


public class Configurator {
    public static boolean TOGGLE;
    public static KeyBinding TOGGLE_BIND;
    private final Path currentDirectory;
    public static float[] COLOR;
    public static boolean COLOR_HOVERED;
    public static KeyBinding COLOR_HOVERED_BIND;
    public static ItemComparator.Comparators COMPARATOR;
    public static KeyBinding COMPARATOR_BIND;
    private final String CONFIG = "HighLightItemConfig";
    private final Properties properties = new Properties();

    public static Configurator getInstance() throws IOException, URISyntaxException {
        return new Configurator();
    }

    public Configurator() throws IOException {
        currentDirectory = FabricLoader.getInstance().getConfigDir();
        loadOrGenerateConfig();
    }

    public enum Config {
        COLOR("color", Colors.HighLightColor.DEFAULT.json().toString()),
        COLOR_HOVERED("color-hovered", "false"),
        TOGGLE("toggle", "true"),
        COMPARATOR("comparator", ItemComparator.Comparators.ITEM_ONLY.name());

        private final String key;
        private final String def;
        Config(String key, String def) {
            this.key = key;
            this.def = def;
        }

        public String getKey() {
            return key;
        }

        public String getDefault() {
            return def;
        }
    }

    public void loadOrGenerateConfig() throws IOException, IllegalArgumentException {
        if (Files.exists(getConfigPath())) {
            InputStream input = new FileInputStream(getConfigPath().toString());
            properties.load(input);
        } else {
            var stream = new FileOutputStream(getConfigPath().toString());

            for (Config value : Config.values()) {
                properties.setProperty(value.getKey(), value.getDefault());
            }
            properties.store(stream, null);
        }

        TOGGLE = Boolean.parseBoolean(properties.getProperty(Config.TOGGLE.getKey(), Config.TOGGLE.getDefault()));

        if (properties.containsKey("color")) {
            var jsonColor = JsonParser.parseString(properties.getProperty(Config.COLOR.getKey())).getAsJsonObject();
            if (jsonColor.has("default"))
                COLOR = Colors.HighLightColor.fromJson(jsonColor).getShaderColor();
            else
                COLOR = Colors.customFromJson(jsonColor);
        } else {
            var highlightColor = Colors.HighLightColor.valueOf(properties.getProperty("highlight-color", Colors.HighLightColor.DEFAULT.name()));
            COLOR = highlightColor.getShaderColor();
            removeFromConfig("highlight-color"); // Color system is changed
            updateConfig(Config.COLOR, highlightColor.json().toString());
        }

        COLOR_HOVERED = Boolean.parseBoolean(properties.getProperty(Config.COLOR_HOVERED.getKey(), Config.COLOR_HOVERED.getDefault()));
        COMPARATOR = ItemComparator.Comparators.valueOf(properties.getProperty(Config.COMPARATOR.getKey(), Config.COMPARATOR.getDefault()));
    }

    public Path getConfigDirectoryPath() {
        return currentDirectory;
    }

    public Path getConfigPath() {
        return getConfigDirectoryPath().resolve(CONFIG);
    }

    public void updateConfig(Config config, String value) throws IOException {
        var stream = new FileOutputStream(getConfigPath().toString());
        properties.setProperty(config.getKey(), value);
        properties.store(stream, null);
    }

    public void removeFromConfig(String key) throws IOException {
        var stream = new FileOutputStream(getConfigPath().toString());
        properties.remove(key);
        properties.store(stream, null);
    }

    public void updateToggle(ClientPlayerEntity player) {
        Configurator.TOGGLE = !Configurator.TOGGLE;
        try {
            HighlightItem.configurator.updateConfig(Configurator.Config.TOGGLE, "" + Configurator.TOGGLE);
            player.sendMessage(Configurator.TOGGLE ? Text.translatable( "notification.highlightitem.highlighting.update").append(Text.literal(" ")).append(Text.translatable("notification.highlightitem.activate")).formatted(Formatting.GRAY) : Text.translatable( "notification.highlightitem.highlighting.update").append(Text.literal(" ")).append(Text.translatable("notification.highlightitem.deactivate")).formatted(Formatting.DARK_GRAY));
        } catch (IOException e) {
            player.sendMessage(Text.translatable("notification.highlightitem.config.update.fail").formatted(Formatting.RED));
        }
    }

    public void updateColorHovered(boolean hovered, ClientPlayerEntity player) {
        Configurator.COLOR_HOVERED = hovered;
        try {
            HighlightItem.configurator.updateConfig(Configurator.Config.COLOR_HOVERED, "" + Configurator.COLOR_HOVERED);
            player.sendMessage(Configurator.COLOR_HOVERED ? Text.translatable( "notification.highlightitem.color_hovered_activated").formatted(Formatting.GRAY) : Text.translatable("notification.highlightitem.color_hovered_deactivated").formatted(Formatting.DARK_GRAY));
        } catch (IOException e) {
            player.sendMessage(Text.translatable("notification.highlightitem.config.update.fail").formatted(Formatting.RED));
        }
    }

    public void updateMode(ItemComparator.Comparators mode, ClientPlayerEntity player) {
        Configurator.COMPARATOR = mode;
        try {
            HighlightItem.configurator.updateConfig(Configurator.Config.COMPARATOR, mode.name());
            player.sendMessage(Text.translatable("notification.highlightitem.toggle", mode.name()).formatted(Formatting.GRAY));
        } catch (IOException e) {
            player.sendMessage(Text.translatable("notification.highlightitem.config.update.fail").formatted(Formatting.RED));
        }
    }
}
