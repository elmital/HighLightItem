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
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;


public class ConfigurationScreen extends GameOptionsScreen {
    int red;
    int green;
    int blue;
    float alpha;
    boolean colorHovered, toggle;
    ItemComparator.Comparators comparator;
    final static int FOOTER_HEIGHT = 53;

    public ConfigurationScreen(GameOptions gameOptions) {
        this(gameOptions, ColorHelper.getRed(Configurator.COLOR), ColorHelper.getGreen(Configurator.COLOR), ColorHelper.getBlue(Configurator.COLOR), (ColorHelper.getAlpha(Configurator.COLOR) / 255f) * 100, Configurator.COLOR_HOVERED, Configurator.COMPARATOR, Configurator.TOGGLE);
    }

    private ConfigurationScreen(GameOptions gameOptions, int red, int green, int blue, float alpha, boolean colorHovered, ItemComparator.Comparators comparator, boolean toggle) {
        super(null, gameOptions, Text.literal("HighLightItem"));
        this.layout.setFooterHeight(FOOTER_HEIGHT);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.colorHovered = colorHovered;
        this.toggle = toggle;
        this.comparator = comparator;
    }

    private ConfigurationScreen(GameOptions gameOptions, boolean colorHovered, ItemComparator.Comparators comparator, boolean toggle) {
        this(gameOptions, ColorHelper.getRed(Configurator.COLOR), ColorHelper.getGreen(Configurator.COLOR), ColorHelper.getBlue(Configurator.COLOR), (ColorHelper.getAlpha(Configurator.COLOR) / 255f) * 100, colorHovered, comparator, toggle);
    }

    @Override
    public void close() {
        close(true);
    }

    private void close(boolean withSaving) {
        super.close();
        if (withSaving) {
            HighlightItem.configurator.updateColor(new float[]{this.red / 255.0f, this.green / 255.0f, this.blue / 255.0f, this.alpha / 100.0f}, null);
            if (this.colorHovered != Configurator.COLOR_HOVERED)
                HighlightItem.configurator.updateColorHovered(this.colorHovered, HighlightItem.CLIENT.player, Configurator.NotificationType.NONE);
            if (this.comparator != Configurator.COMPARATOR)
                HighlightItem.configurator.updateMode(this.comparator, HighlightItem.CLIENT.player, Configurator.NotificationType.NONE);
            if (this.toggle != Configurator.TOGGLE)
                HighlightItem.configurator.updateToggle(HighlightItem.CLIENT.player, Configurator.NotificationType.NONE);
        }
    }

    @Override
    protected void initFooter() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.vertical()).spacing(8);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.add(ButtonWidget.builder(Text.translatable("options.highlightitem.color.vanilla"), (button -> {
            close(false);
            HighlightItem.CLIENT.setScreen(new ConfigurationScreen(HighlightItem.CLIENT.options, (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[0] * 255), (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[1] * 255), (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[2] * 255), Colors.HighLightColor.DEFAULT.getShaderColor()[3] * 100, colorHovered, comparator, toggle));
        })).build());
        directionalLayoutWidget2.add(ButtonWidget.builder(Text.translatable("options.highlightitem.color.reset"), button -> {
            close(false);
            HighlightItem.CLIENT.setScreen(new ConfigurationScreen(HighlightItem.CLIENT.options, this.colorHovered, this.comparator, this.toggle));
        }).build());
        directionalLayoutWidget.add(ButtonWidget.builder(Text.translatable("options.highlightitem.save.close"), button -> close()).build());
    }

    @Override
    protected void addOptions() {
        this.body.addSingleOptionEntry(new SimpleOption<>("options.highlightitem.color.red", SimpleOption.emptyTooltip(), (prefix, value) -> {
            if (value < 0 || value > 255) {
                return Text.literal("error");
            } else {
                return GameOptions.getGenericValueText(prefix, value);
            }
        }, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), this.red, (value) -> this.red = value));
        this.body.addSingleOptionEntry(new SimpleOption<>("options.highlightitem.color.green", SimpleOption.emptyTooltip(), (prefix, value) -> {
            if (value < 0 || value > 255) {
                return Text.literal("error");
            } else {
                return GameOptions.getGenericValueText(prefix, value);
            }
        }, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), this.green, (value) -> this.green = value));
        this.body.addSingleOptionEntry(new SimpleOption<>("options.highlightitem.color.blue", SimpleOption.emptyTooltip(), (prefix, value) -> {
            if (value < 0 || value > 255) {
                return Text.literal("error");
            } else {
                return GameOptions.getGenericValueText(prefix, value);
            }
        }, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), this.blue, (value) -> this.blue = value));

        this.body.addSingleOptionEntry(new SimpleOption<>("options.highlightitem.color.alpha", SimpleOption.emptyTooltip(), (prefix, value) -> {
            if (value < 0 || value > 100) {
                return Text.literal("error");
            } else {
                return GameOptions.getGenericValueText(prefix, Text.of(value + "%"));
            }
        }, new SimpleOption.ValidatingIntSliderCallbacks(0, 100), (int) this.alpha, (value) -> this.alpha = (float) value));

        this.body.addSingleOptionEntry(SimpleOption.ofBoolean("options.highlightitem.color.hovered", this.colorHovered, value -> this.colorHovered = value));

        this.body.addSingleOptionEntry(new SimpleOption<>("options.highlightitem.comparator", value -> Tooltip.of(Text.translatable(value.translationKey())), SimpleOption.enumValueText()
                , new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(ItemComparator.Comparators.values()), Codec.INT.xmap(compId -> ItemComparator.Comparators.values()[compId], ItemComparator.Comparators::getId))
                , this.comparator
                , value -> this.comparator = value)
        );

        this.body.addSingleOptionEntry(SimpleOption.ofBoolean("options.highlightitem.toggle", this.toggle, value -> this.toggle = value));
    }


    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("highlight_item", "textures/empty-color.png"), 5, 36 , 0, 0, (this.width / 2) - 164 , this.height - 72 - FOOTER_HEIGHT, 256, 256);
        context.drawBorder(4, 35 , (this.width / 2) - 163 , this.height - 70 - FOOTER_HEIGHT, ColorHelper.getArgb(255, 75, 75, 75));
        context.fill(RenderPipelines.GUI, 5, 36 , (this.width / 2) - 160 , this.height - 36- FOOTER_HEIGHT, ColorHelper.getArgb((int) (this.alpha * 2.55F), this.red, this.green, this.blue));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            this.close(false);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
