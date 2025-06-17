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
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


public class Configurator {
    public static boolean TOGGLE;
    public static KeyBinding TOGGLE_BIND;
    private final Path currentDirectory;
    public static int COLOR;

    public static KeyBinding COLOR_MENU;
    public static boolean COLOR_HOVERED;
    public static KeyBinding COLOR_HOVERED_BIND;
    public static ItemComparator.Comparators COMPARATOR;
    public static KeyBinding COMPARATOR_BIND;
    public static NotificationPreference NOTIFICATION_PREFERENCE;
    private final String CONFIG = "HighLightItemConfig";
    private final Properties properties = new Properties();
    public static SystemToast activeToastNotification = null;

    public static Configurator getInstance() throws IOException, URISyntaxException {
        return new Configurator();
    }

    public Configurator() throws IOException {
        currentDirectory = FabricLoader.getInstance().getConfigDir();
        loadOrGenerateConfig();
    }

    public enum NotificationContext {
        NONE,
        ON_SCREEN,
        SENDING_COMMAND,
        IN_GAME
    }

    public enum NotificationPreference implements TranslatableOption {
        NONE,
        TOAST,
        CHAT,
        OVERLAY;

        @Override
        public int getId() {
            return ordinal();
        }

        @Override
        public String getTranslationKey() {
            return "options.highlightitem.notif." + name().toLowerCase();
        }
    }

    public enum Config {
        COLOR("color", Colors.HighLightColor.DEFAULT.json().toString()),
        COLOR_HOVERED("color-hovered", "false"),
        TOGGLE("toggle", "true"),
        COMPARATOR("comparator", ItemComparator.Comparators.ITEM_ONLY.name()),
        NOTIFICATION_PREFERENCE("notif-preference", NotificationPreference.NONE.name());

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

        float[] colors;
        if (properties.containsKey("color")) {
            var jsonColor = JsonParser.parseString(properties.getProperty(Config.COLOR.getKey())).getAsJsonObject();
            if (jsonColor.has("default"))
                colors = Colors.HighLightColor.fromJson(jsonColor).getShaderColor();
            else
                colors = Colors.customFromJson(jsonColor);
        } else {
            var highlightColor = Colors.HighLightColor.valueOf(properties.getProperty("highlight-color", Colors.HighLightColor.DEFAULT.name()));
            colors = highlightColor.getShaderColor();
            removeFromConfig("highlight-color"); // Color system is changed
            updateConfig(Config.COLOR, highlightColor.json().toString());
        }

        COLOR = ColorHelper.getArgb((int) (colors[3] * 255), (int) (colors[0] * 255), (int) (colors[1] * 255), (int) (colors[2] * 255));
        COLOR_HOVERED = Boolean.parseBoolean(properties.getProperty(Config.COLOR_HOVERED.getKey(), Config.COLOR_HOVERED.getDefault()));
        COMPARATOR = ItemComparator.Comparators.valueOf(properties.getProperty(Config.COMPARATOR.getKey(), Config.COMPARATOR.getDefault()));
        NOTIFICATION_PREFERENCE = NotificationPreference.valueOf(properties.getProperty(Config.NOTIFICATION_PREFERENCE.getKey(), Config.NOTIFICATION_PREFERENCE.getDefault()));
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

    public void updateToggle(ClientPlayerEntity player, NotificationContext notification) {
        Configurator.TOGGLE = !Configurator.TOGGLE;
        try {
            HighlightItem.configurator.updateConfig(Configurator.Config.TOGGLE, "" + Configurator.TOGGLE);
            notify(notification, Configurator.TOGGLE ? Text.translatable( "notification.highlightitem.highlighting.update").append(Text.literal(" ")).append(Text.translatable("notification.highlightitem.activate")).formatted(Formatting.GRAY) : Text.translatable( "notification.highlightitem.highlighting.update").append(Text.literal(" ")).append(Text.translatable("notification.highlightitem.deactivate")).formatted(Formatting.DARK_GRAY), player);
        } catch (IOException e) {
            notify(notification, Text.translatable("notification.highlightitem.config.update.fail").formatted(Formatting.RED), player);
        }
    }

    public void updateColorHovered(boolean hovered, ClientPlayerEntity player, NotificationContext notification) {
        Configurator.COLOR_HOVERED = hovered;
        try {
            HighlightItem.configurator.updateConfig(Configurator.Config.COLOR_HOVERED, "" + Configurator.COLOR_HOVERED);
            notify(notification, Configurator.COLOR_HOVERED ? Text.translatable( "notification.highlightitem.color_hovered_activated").formatted(Formatting.GRAY) : Text.translatable("notification.highlightitem.color_hovered_deactivated").formatted(Formatting.DARK_GRAY), player);
        } catch (IOException e) {
            notify(notification, Text.translatable("notification.highlightitem.config.update.fail").formatted(Formatting.RED), player);
        }
    }

    public void changeMode(ClientPlayerEntity player, NotificationContext notification) {
        if (Configurator.COMPARATOR.ordinal() == ItemComparator.Comparators.values().length - 1) {
            HighlightItem.configurator.updateMode(ItemComparator.Comparators.ITEM_ONLY, player, notification);
        } else {
            for (ItemComparator.Comparators mode : ItemComparator.Comparators.values()) {
                if (mode.ordinal() == Math.min(Configurator.COMPARATOR.ordinal() + 1, ItemComparator.Comparators.values().length - 1)) {
                    HighlightItem.configurator.updateMode(mode, player, notification);
                    break;
                }
            }
        }
    }

    public void updateMode(ItemComparator.Comparators mode, ClientPlayerEntity player, NotificationContext notification) {
        Configurator.COMPARATOR = mode;
        try {
            HighlightItem.configurator.updateConfig(Configurator.Config.COMPARATOR, mode.name());
            notify(notification, Text.translatable("notification.highlightitem.comparator.change",  Text.translatable(mode.translationKey()).append(" (").append(mode.name()).append(")")).formatted(Formatting.GRAY), player);
        } catch (IOException e) {
            notify(notification, Text.translatable("notification.highlightitem.config.update.fail").formatted(Formatting.RED), player);
        }
    }

    public void updateColor(float[] rgba, @Nullable ClientPlayerEntity player) {
        Configurator.COLOR = ColorHelper.getArgb((int) (rgba[3] * 255f), (int) (rgba[0] * 255f), (int) (rgba[1] * 255f), (int) (rgba[2] * 255f));
        try {
            HighlightItem.configurator.updateConfig(Configurator.Config.COLOR, Colors.customToJson(rgba).toString());
            if (player != null) player.sendMessage(Text.translatable("notification.highlightitem.color").formatted(Formatting.GRAY), false);
        } catch (IOException e) {
            if (player != null) player.sendMessage(Text.translatable("notification.highlightitem.config.update.fail").formatted(Formatting.RED), false);
        }

    }

    public void updateNotificationPreference(NotificationPreference notificationPreference) {
        Configurator.NOTIFICATION_PREFERENCE = notificationPreference;
        try {
            HighlightItem.configurator.updateConfig(Config.NOTIFICATION_PREFERENCE, notificationPreference.name());
        } catch (IOException e) {
            notifyToast(Text.translatable("notification.highlightitem.config.update.fail").formatted(Formatting.RED));
        }
    }

    private void notify(NotificationContext type, Text text, ClientPlayerEntity player) {
        if (type.equals(NotificationContext.ON_SCREEN) || NOTIFICATION_PREFERENCE.equals(NotificationPreference.TOAST)) {
            notifyToast(text);
            return;
        }

        if (NOTIFICATION_PREFERENCE.equals(NotificationPreference.CHAT)) {
            player.sendMessage(text, false);
            return;
        } else if (NOTIFICATION_PREFERENCE.equals(NotificationPreference.OVERLAY)) {
            player.sendMessage(text, true);
            return;
        }
        switch (type) {
            case SENDING_COMMAND -> player.sendMessage(text, false);
            case IN_GAME -> player.sendMessage(text, true);
        }
    }

    private void notifyToast(Text text) {
        notifyToast(Text.literal("HighLightItem"), text);
    }

    private void notifyToast(Text text, Text desc) {
        if (activeToastNotification == null || activeToastNotification.getVisibility().equals(Toast.Visibility.HIDE)) {
            HighlightItem.CLIENT.getToastManager().add(activeToastNotification = new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, text, desc));
        } else {
            activeToastNotification.setContent(text, desc);
            activeToastNotification.update(HighlightItem.CLIENT.getToastManager(), 5000L); // System toast is 5000L
        }
    }
}
