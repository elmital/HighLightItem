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

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static be.elmital.highlightItem.Configurator.Config.COLOR;

public class Configurator {
    public static boolean TOGGLE;
    private final Path currentDirectory;
    public static HighlightItem.HighLightColor HIGHLIGHT_COLOR;
    public static boolean COLOR_HOVERED;
    public static ItemComparator.Comparators COMPARATOR;
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
        COLOR("highlight-color", HighlightItem.HighLightColor.DEFAULT.name()),
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
        if(Files.exists(getConfigPath())) {
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
        HIGHLIGHT_COLOR = HighlightItem.HighLightColor.valueOf(properties.getProperty(COLOR.getKey(), COLOR.getDefault()));
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
}
