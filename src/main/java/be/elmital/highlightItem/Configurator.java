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

import be.elmital.highlightItem.mixin.SystemToastAccessor;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


public class Configurator {
    public static boolean TOGGLE;
    public static KeyMapping TOGGLE_BIND;
    private final Path currentDirectory;
    public static int COLOR;

    public static KeyMapping COLOR_MENU;
    public static boolean COLOR_HOVERED;
    public static KeyMapping COLOR_HOVERED_BIND;
    public static ItemComparator.Comparators COMPARATOR;
    public static KeyMapping COMPARATOR_BIND;
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

    public enum NotificationPreference implements OptionEnum {
        NONE,
        TOAST,
        CHAT,
        OVERLAY;

        @Override
        public int getId() {
            return ordinal();
        }

        @Override
        public String getKey() {
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

        COLOR = ARGB.color((int) (colors[3] * 255), (int) (colors[0] * 255), (int) (colors[1] * 255), (int) (colors[2] * 255));
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

    public void updateToggle(LocalPlayer player, NotificationContext notification) {
        Configurator.TOGGLE = !Configurator.TOGGLE;
        try {
            HighlightItem.configurator.updateConfig(Configurator.Config.TOGGLE, "" + Configurator.TOGGLE);
            notify(notification, Configurator.TOGGLE ? Component.translatable( "notification.highlightitem.highlighting.update").append(Component.literal(" ")).append(Component.translatable("notification.highlightitem.activate")).withStyle(ChatFormatting.GRAY) : Component.translatable( "notification.highlightitem.highlighting.update").append(Component.literal(" ")).append(Component.translatable("notification.highlightitem.deactivate")).withStyle(ChatFormatting.DARK_GRAY), player);
        } catch (IOException e) {
            notify(notification, Component.translatable("notification.highlightitem.config.update.fail").withStyle(ChatFormatting.RED), player);
        }
    }

    public void updateColorHovered(boolean hovered, LocalPlayer player, NotificationContext notification) {
        Configurator.COLOR_HOVERED = hovered;
        try {
            HighlightItem.configurator.updateConfig(Configurator.Config.COLOR_HOVERED, "" + Configurator.COLOR_HOVERED);
            notify(notification, Configurator.COLOR_HOVERED ? Component.translatable( "notification.highlightitem.color_hovered_activated").withStyle(ChatFormatting.GRAY) : Component.translatable("notification.highlightitem.color_hovered_deactivated").withStyle(ChatFormatting.DARK_GRAY), player);
        } catch (IOException e) {
            notify(notification, Component.translatable("notification.highlightitem.config.update.fail").withStyle(ChatFormatting.RED), player);
        }
    }

    public void changeMode(LocalPlayer player, NotificationContext notification) {
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

    public void updateMode(ItemComparator.Comparators mode, LocalPlayer player, NotificationContext notification) {
        Configurator.COMPARATOR = mode;
        try {
            HighlightItem.configurator.updateConfig(Configurator.Config.COMPARATOR, mode.name());
            notify(notification, Component.translatable("notification.highlightitem.comparator.change",  Component.translatable(mode.translationKey()).append(" (").append(mode.name()).append(")")).withStyle(ChatFormatting.GRAY), player);
        } catch (IOException e) {
            notify(notification, Component.translatable("notification.highlightitem.config.update.fail").withStyle(ChatFormatting.RED), player);
        }
    }

    public void updateColor(float[] rgba, @Nullable LocalPlayer player) {
        Configurator.COLOR = ARGB.color((int) (rgba[3] * 255f), (int) (rgba[0] * 255f), (int) (rgba[1] * 255f), (int) (rgba[2] * 255f));
        try {
            HighlightItem.configurator.updateConfig(Configurator.Config.COLOR, Colors.customToJson(rgba).toString());
            if (player != null) player.displayClientMessage(Component.translatable("notification.highlightitem.color").withStyle(ChatFormatting.GRAY), false);
        } catch (IOException e) {
            if (player != null) player.displayClientMessage(Component.translatable("notification.highlightitem.config.update.fail").withStyle(ChatFormatting.RED), false);
        }

    }

    public void updateNotificationPreference(NotificationPreference notificationPreference) {
        Configurator.NOTIFICATION_PREFERENCE = notificationPreference;
        try {
            HighlightItem.configurator.updateConfig(Config.NOTIFICATION_PREFERENCE, notificationPreference.name());
        } catch (IOException e) {
            notifyToast(Component.translatable("notification.highlightitem.config.update.fail").withStyle(ChatFormatting.RED));
        }
    }

    private void notify(NotificationContext type, Component text, @Nullable LocalPlayer player) {
        if (type.equals(NotificationContext.ON_SCREEN) || NOTIFICATION_PREFERENCE.equals(NotificationPreference.TOAST)) {
            notifyToast(text);
            return;
        }

        if (player == null)
            return;

        if (NOTIFICATION_PREFERENCE.equals(NotificationPreference.CHAT)) {
            player.displayClientMessage(text, false);
            return;
        } else if (NOTIFICATION_PREFERENCE.equals(NotificationPreference.OVERLAY)) {
            player.displayClientMessage(text, true);
            return;
        }
        switch (type) {
            case SENDING_COMMAND -> player.displayClientMessage(text, false);
            case IN_GAME -> player.displayClientMessage(text, true);
        }
    }

    private void notifyToast(Component text) {
        notifyToast(Component.literal("HighLightItem"), text);
    }

    private void notifyToast(Component text, Component desc) {
        if (activeToastNotification == null || activeToastNotification.getWantedVisibility().equals(Toast.Visibility.HIDE)) {
            Minecraft.getInstance().getToastManager().addToast(activeToastNotification = new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION, text, desc));
        } else {
            activeToastNotification.reset(text, desc);
            // We need to recalculate the width manually following the way it's done in the SystemToast class
            ((SystemToastAccessor) activeToastNotification).setWidth(Math.max(200, Minecraft.getInstance().font.split(desc, 200)
                    .stream().mapToInt(value -> Minecraft.getInstance().font.width(desc)).max().orElse(200)) + 30);
            activeToastNotification.update(Minecraft.getInstance().getToastManager(), 5000L); // System toast is 5000L
        }
    }
}
