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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
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
    boolean toggle;
    Configurator.ColorHoveredOptions colorHovered;
    ItemComparator.Comparators comparator;
    Configurator.NotificationPreference notif;
    Configurator.ScreenContext screenContext;
    final static int FOOTER_HEIGHT = 53;
    int highlightTick = 0;

    public ConfigurationScreen(Options gameOptions) {
        this(null, gameOptions);
    }

    public ConfigurationScreen(@Nullable Screen parent, Options gameOptions) {
        this(parent, gameOptions, ARGB.red(Configurator.COLOR), ARGB.green(Configurator.COLOR), ARGB.blue(Configurator.COLOR), (ARGB.alpha(Configurator.COLOR) / 255f) * 100, Configurator.COLOR_HOVERED, Configurator.COMPARATOR, Configurator.NOTIFICATION_PREFERENCE, Configurator.SCREEN_CONTEXT, Configurator.TOGGLE);
    }

    private ConfigurationScreen(@Nullable Screen parent, Options gameOptions, int red, int green, int blue, float alpha, Configurator.ColorHoveredOptions colorHovered, ItemComparator.Comparators comparator, Configurator.NotificationPreference notif, Configurator.ScreenContext screenContext, boolean toggle) {
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

    private ConfigurationScreen(@Nullable Screen parent, Options gameOptions, Configurator.ColorHoveredOptions colorHovered, ItemComparator.Comparators comparator, Configurator.NotificationPreference notif, Configurator.ScreenContext screenContext, boolean toggle) {
        this(parent, gameOptions, ARGB.red(Configurator.COLOR), ARGB.green(Configurator.COLOR), ARGB.blue(Configurator.COLOR), (ARGB.alpha(Configurator.COLOR) / 255f) * 100, colorHovered, comparator, notif, screenContext, toggle);
    }

    @Override
    public void onClose() {
        close(true);
    }

    private void close(boolean withSaving) {
        super.onClose();
        if (withSaving) {
            int newColor = getColor(this.red, this.green, this.blue, this.alpha);
            if (Configurator.COLOR != newColor)
                HighlightItem.configurator.updateColor(new float[]{this.red / 255.0f, this.green / 255.0f, this.blue / 255.0f, this.alpha / 100.0f}, useDefaultHighLight(newColor) ? Colors.HighLightColor.DEFAULT : null, Minecraft.getInstance().player, Configurator.NotificationContext.NONE);
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

    private int getColor(int red, int green, int blue, float alpha) {
        return ARGB.color((int) ((alpha / 100.0f) * 255f), red, green, blue);
    }

    private boolean useDefaultHighLight(int color) {
        return Colors.HighLightColor.DEFAULT.colorInteger() == color;
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

        this.list.addSmall(Button.builder(Component.translatable("options.highlightitem.color.vanilla"), (_ -> {
                    close(false);
                    Minecraft.getInstance().setScreenAndShow(new ConfigurationScreen(this.lastScreen, Minecraft.getInstance().options, (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[0] * 255), (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[1] * 255), (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[2] * 255), Colors.HighLightColor.DEFAULT.getShaderColor()[3] * 100, colorHovered, comparator, notif,  this.screenContext, toggle));
                })).build()
                , Button.builder(Component.translatable("options.highlightitem.color.reset"), (_ -> {
                    close(false);
                    Minecraft.getInstance().setScreenAndShow(new ConfigurationScreen(this.lastScreen, Minecraft.getInstance().options, this.colorHovered, this.comparator, this.notif, this.screenContext, this.toggle));
                })).build()
        );
        this.list.addBig(new OptionInstance<>("options.highlightitem.color.hovered", value ->  Tooltip.create(Component.translatable(value.getKey()))
                , (_, value) -> Component.translatable(value.getKey())
                , new OptionInstance.Enum<>(Arrays.asList(Configurator.ColorHoveredOptions.values()), Codec.INT.xmap(compId -> Configurator.ColorHoveredOptions.values()[compId], Configurator.ColorHoveredOptions::getId))
                , this.colorHovered
                , value -> this.colorHovered = value)
        );

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
        super.extractRenderState(context, mouseX, mouseY, delta);// 133 53
        final int width = 38;
        final int height = 38;

        final int x = 5;
        final int y = ((this.height - this.layout.getHeaderHeight()) / 2) - (height / 2);
        final int itemOffSet = 18;

        context.blit(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("textures/gui/container/inventory.png"), x, y, 96F, 16F, width, height, 256, 256);
        context.outline(x - 1, y - 1, width + 1, height + 1, new Color(71, 71, 71).getRGB());
        context.fakeItem(new ItemStack(Blocks.WOOL.red(), 1), x + 2, y + 2); // 1
        context.fakeItem(new ItemStack(Blocks.WOOL.green(), 1), x + 2 + itemOffSet, y + 2);
        context.fakeItem(new ItemStack(Blocks.WOOL.blue(), 1), x + 2, y + 2 + itemOffSet);
        context.fakeItem(new ItemStack(Blocks.WOOL.lightGray(), 1), x + 2 + itemOffSet, y + 2 + itemOffSet);

        // Highlight
        if (this.highlightTick <= 60) {
            drawFakeHighLight(context, x + 2, y + 2);
        } else if (this.highlightTick <= 120) {
            drawFakeHighLight(context, x + 2 + itemOffSet, y + 2);
        } else if (this.highlightTick <= 180) {
            drawFakeHighLight(context, x + 2 + itemOffSet, y + 2 + itemOffSet);
        } else if (this.highlightTick <= 240) {
            drawFakeHighLight(context, x + 2, y + 2 + itemOffSet);
        } else {
            this.highlightTick = -1; // Reset
        }

        this.highlightTick++;
    }

    private void drawFakeHighLight(GuiGraphicsExtractor context, int x, int y) {
        final int color = getColor(this.red, this.green, this.blue, this.alpha);
        if (useDefaultHighLight(color)) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("container/slot_highlight_front"), x - 4, y - 4, 24, 24);
        } else {
            context.fill(x, y, x + 16, y + 16, color);
        }
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
