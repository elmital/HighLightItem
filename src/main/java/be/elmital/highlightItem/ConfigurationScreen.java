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


import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;


public class ConfigurationScreen extends OptionsSubScreen {
    int red;
    int green;
    int blue;
    float alpha;
    boolean colorHovered, toggle;
    ItemComparator.Comparators comparator;
    Configurator.NotificationPreference notif;
    Configurator.ScreenContext screenContext;
    final static int FOOTER_HEIGHT = 53;

    public ConfigurationScreen(Options gameOptions) {
        this(null, gameOptions);
    }

    public ConfigurationScreen(@Nullable Screen parent, Options gameOptions) {
        this(parent, gameOptions, ARGB.red(Configurator.COLOR), ARGB.green(Configurator.COLOR), ARGB.blue(Configurator.COLOR), (ARGB.alpha(Configurator.COLOR) / 255f) * 100, Configurator.COLOR_HOVERED, Configurator.COMPARATOR, Configurator.NOTIFICATION_PREFERENCE, Configurator.SCREEN_CONTEXT, Configurator.TOGGLE);
    }

    private ConfigurationScreen(@Nullable Screen parent, Options gameOptions, int red, int green, int blue, float alpha, boolean colorHovered, ItemComparator.Comparators comparator, Configurator.NotificationPreference notif, Configurator.ScreenContext screenContext, boolean toggle) {
        super(parent, gameOptions, Component.literal("HighLightItem"));
        this.layout.setFooterHeight(FOOTER_HEIGHT);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.colorHovered = colorHovered;
        this.toggle = toggle;
        this.comparator = comparator;
        this.notif = notif;
        this.screenContext = screenContext;
    }

    private ConfigurationScreen(@Nullable Screen parent, Options gameOptions, boolean colorHovered, ItemComparator.Comparators comparator, Configurator.NotificationPreference notif, Configurator.ScreenContext screenContext, boolean toggle) {
        this(parent, gameOptions, ARGB.red(Configurator.COLOR), ARGB.green(Configurator.COLOR), ARGB.blue(Configurator.COLOR), (ARGB.alpha(Configurator.COLOR) / 255f) * 100, colorHovered, comparator, notif, screenContext, toggle);
    }

    @Override
    public void onClose() {
        close(true);
    }

    private void close(boolean withSaving) {
        super.onClose();
        if (withSaving) {
            int newColor = ARGB.color((int) ((this.alpha / 100.0f) * 255f), this.red, this.green, this.blue);
            if (Configurator.COLOR != newColor)
                HighlightItem.configurator.updateColor(new float[]{this.red / 255.0f, this.green / 255.0f, this.blue / 255.0f, this.alpha / 100.0f}, Colors.HighLightColor.DEFAULT.colorInteger() == newColor ? Colors.HighLightColor.DEFAULT : null, Minecraft.getInstance().player, Configurator.NotificationContext.NONE);
            if (this.colorHovered != Configurator.COLOR_HOVERED)
                HighlightItem.configurator.updateColorHovered(this.colorHovered, Minecraft.getInstance().player, Configurator.NotificationContext.NONE);
            if (this.comparator != Configurator.COMPARATOR)
                HighlightItem.configurator.updateMode(this.comparator, Minecraft.getInstance().player, Configurator.NotificationContext.NONE);
            if (this.notif != Configurator.NOTIFICATION_PREFERENCE)
                HighlightItem.configurator.updateNotificationPreference(this.notif, Minecraft.getInstance().player, Configurator.NotificationContext.NONE);
            if (this.screenContext != Configurator.SCREEN_CONTEXT)
                HighlightItem.configurator.updateScreenContext(this.screenContext, Minecraft.getInstance().player, Configurator.NotificationContext.NONE);
            if (this.toggle != Configurator.TOGGLE)
                HighlightItem.configurator.updateToggle(Minecraft.getInstance().player, Configurator.NotificationContext.NONE);
        }
    }

    @Override
    protected void addFooter() {
        // Toggle
        LinearLayout directionalLayoutWidget = this.layout.addToFooter(LinearLayout.vertical()).spacing(8);
        directionalLayoutWidget.defaultCellSetting().alignHorizontallyCenter();
        directionalLayoutWidget.addChild(Button.builder(Component.translatable(this.toggle ? "options.highlightitem.toggle.deactivation" : "options.highlightitem.toggle.activation"), button -> {
            this.toggle = !this.toggle;
            close(true);
            HighlightItem.configurator.notify(Configurator.NotificationContext.ON_SCREEN, Component.translatable(!this.toggle ? "notification.highlightitem.deactivate" : "notification.highlightitem.activate"), Minecraft.getInstance().player);
        }).build());

        // Reset | Apply
        LinearLayout directionalLayoutWidget2 = directionalLayoutWidget.addChild(LinearLayout.horizontal().spacing(8));
        directionalLayoutWidget2.addChild(Button.builder(Component.translatable("options.highlightitem.reset"), button -> {
            close(false);
            Minecraft.getInstance().setScreenAndShow(new ConfigurationScreen(this.lastScreen, Minecraft.getInstance().options));
        }).build());
        directionalLayoutWidget2.addChild(Button.builder(Component.translatable("options.highlightitem.save.close"), button -> onClose()).build());
    }

    @Override
    protected void addOptions() {
        // Colors
        this.list.addHeader(Component.translatable("notification.highlightitem.title"));
        this.list.addSmall(
                new OptionInstance<>("options.highlightitem.color.red", OptionInstance.noTooltip(), (prefix, value) -> {
                    if (value < 0 || value > 255) {
                        return Component.literal("error");
                    } else {
                        return Options.genericValueLabel(prefix, value);
                    }
                }, new OptionInstance.IntRange(0, 255), this.red, (value) -> this.red = value),
                new OptionInstance<>("options.highlightitem.color.green", OptionInstance.noTooltip(), (prefix, value) -> {
                    if (value < 0 || value > 255) {
                        return Component.literal("error");
                    } else {
                        return Options.genericValueLabel(prefix, value);
                    }
                }, new OptionInstance.IntRange(0, 255), this.green, (value) -> this.green = value)
                , new OptionInstance<>("options.highlightitem.color.blue", OptionInstance.noTooltip(), (prefix, value) -> {
                    if (value < 0 || value > 255) {
                        return Component.literal("error");
                    } else {
                        return Options.genericValueLabel(prefix, value);
                    }
                }, new OptionInstance.IntRange(0, 255), this.blue, (value) -> this.blue = value)
                , new OptionInstance<>("options.highlightitem.color.alpha", OptionInstance.noTooltip(), (prefix, value) -> {
                    if (value < 0 || value > 100) {
                        return Component.literal("error");
                    } else {
                        return Options.genericValueLabel(prefix, Component.nullToEmpty(value + "%"));
                    }
                }, new OptionInstance.IntRange(0, 100), (int) this.alpha, (value) -> this.alpha = (float) value)
        );

        this.list.addBig(Button.builder(Component.translatable("options.highlightitem.color.vanilla"), (_ -> {
            close(false);
            Minecraft.getInstance().setScreenAndShow(new ConfigurationScreen(this.lastScreen, Minecraft.getInstance().options, (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[0] * 255), (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[1] * 255), (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[2] * 255), Colors.HighLightColor.DEFAULT.getShaderColor()[3] * 100, colorHovered, comparator, notif,  this.screenContext, toggle));
        })).build());
        this.list.addBig(OptionInstance.createBoolean("options.highlightitem.color.hovered", this.colorHovered, value -> this.colorHovered = value));


        // Modes
        this.list.addHeader(Component.translatable("options.highlightitem.logical.application"));
        this.list.addBig(new OptionInstance<>("options.highlightitem.comparator", value -> Tooltip.create(Component.translatable(value.translationKey()))
                , (_, value) -> Component.translatable(value.getKey())
                , new OptionInstance.Enum<>(Arrays.asList(ItemComparator.Comparators.values()), Codec.INT.xmap(compId -> ItemComparator.Comparators.values()[compId], ItemComparator.Comparators::getId))
                , this.comparator
                , value -> this.comparator = value)
        );
        this.list.addBig(new OptionInstance<>("options.highlightitem.screen.context", value -> Tooltip.create(Component.translatable(value.getKey()))
                , (_, value) -> Component.translatable(value.getKey())
                , new OptionInstance.Enum<>(Arrays.asList(Configurator.ScreenContext.values()), Codec.INT.xmap(id -> Configurator.ScreenContext.values()[id], Configurator.ScreenContext::getId))
                , this.screenContext
                , value -> this.screenContext = value)
        );

        // Others
        this.list.addHeader(Component.translatable("options.highlightitem.others"));
        this.list.addBig(new OptionInstance<>("options.highlightitem.notif", value -> Tooltip.create(Component.translatable(value.getKey()))
                , (_, value) -> Component.translatable(value.getKey())
                , new OptionInstance.Enum<>(Arrays.asList(Configurator.NotificationPreference.values()), Codec.INT.xmap(id -> Configurator.NotificationPreference.values()[id], Configurator.NotificationPreference::getId))
                , this.notif
                , value -> this.notif = value)
        );
    }


    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath("highlight_item", "textures/empty-color.png"), 5, 36 , 0, 0, (this.width / 2) - 164 , this.height - 72 - FOOTER_HEIGHT, 256, 256);
        context.outline(4, 35 , (this.width / 2) - 163 , this.height - 70 - FOOTER_HEIGHT, ARGB.color(255, 75, 75, 75));
        context.fill(RenderPipelines.GUI, 5, 36 , (this.width / 2) - 160 , this.height - 36- FOOTER_HEIGHT, ARGB.color((int) (this.alpha * 2.55F), this.red, this.green, this.blue));
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.input() == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            this.close(false);
            return true;
        }
        return super.keyPressed(input);
    }
}
