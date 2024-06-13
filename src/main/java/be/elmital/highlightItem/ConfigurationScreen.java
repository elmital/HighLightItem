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


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;


public class ConfigurationScreen extends GameOptionsScreen {
    int red;
    int green;
    int blue;
    float alpha;
    private OptionListWidget list;

    public ConfigurationScreen(GameOptions gameOptions) {
        super(null, gameOptions, Text.literal("HighLightItem"));
        this.red = (int) (Configurator.COLOR[0] * 255);
        this.green = (int) (Configurator.COLOR[1] * 255);
        this.blue = (int) (Configurator.COLOR[2] * 255);
        this.alpha = Configurator.COLOR[3] * 100;
    }

    @Override
    public void close() {
        super.close();
        HighlightItem.configurator.updateColor(new float[]{this.red / 255.0f, this.green / 255.0f, this.blue / 255.0f, this.alpha / 100.0f}, null);
    }


    @Override
    protected void init() {
        this.list = new OptionListWidget(this.client, this.width, this);

        this.list.addSingleOptionEntry(new SimpleOption<>("options.highlightitem.color.red", SimpleOption.emptyTooltip(), (prefix, value) -> {
            if (value < 0 || value > 255) {
                return Text.literal("error");
            } else {
                return GameOptions.getGenericValueText(prefix, value);
            }
        }, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), this.red, (value) -> this.red = value));
        this.list.addSingleOptionEntry(new SimpleOption<>("options.highlightitem.color.green", SimpleOption.emptyTooltip(), (prefix, value) -> {
            if (value < 0 || value > 255) {
                return Text.literal("error");
            } else {
                return GameOptions.getGenericValueText(prefix, value);
            }
        }, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), this.green, (value) -> this.green = value));
        this.list.addSingleOptionEntry(new SimpleOption<>("options.highlightitem.color.blue", SimpleOption.emptyTooltip(), (prefix, value) -> {
            if (value < 0 || value > 255) {
                return Text.literal("error");
            } else {
                return GameOptions.getGenericValueText(prefix, value);
            }
        }, new SimpleOption.ValidatingIntSliderCallbacks(0, 255), this.blue, (value) -> this.blue = value));

        this.list.addSingleOptionEntry(new SimpleOption<>("options.highlightitem.color.alpha", SimpleOption.emptyTooltip(), (prefix, value) -> {
            if (value < 0 || value > 100) {
                return Text.literal("error");
            } else {
                return GameOptions.getGenericValueText(prefix, Text.of(value + "%"));
            }
        }, new SimpleOption.ValidatingIntSliderCallbacks(0, 100), (int) this.alpha, (value) -> this.alpha = (float) value));

        this.addSelectableChild(this.list);
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("options.highlightitem.color.vanilla"), (button -> {
            this.red = (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[0] * 255);
            this.green = (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[1] * 255);
            this.blue = (int) (Colors.HighLightColor.DEFAULT.getShaderColor()[2] * 255);
            this.alpha = Colors.HighLightColor.DEFAULT.getShaderColor()[3] * 100;
            this.clearAndInit();
        })).dimensions(this.width / 2 - 200, this.height - 27, 175, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> close()).dimensions(this.width / 2 + 25, this.height - 27, 175, 20).build());
    }

    @Override
    protected void addOptions() {
        // NOTHING TO DO
    }


    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        this.list.render(context, mouseX, mouseY, delta);
        context.drawTexture(Identifier.of("highlight_item", "textures/empty-color.png"), 5, 36 , 0, 0, (this.width / 2) - 164 , this.height - 72);
        context.drawBorder(4, 35 , (this.width / 2) - 163 , this.height - 70, ColorHelper.Argb.getArgb(255, 75, 75, 75));
        context.fill(RenderLayer.getGuiOverlay(), 5, 36 , (this.width / 2) - 160 , this.height - 36, ColorHelper.Argb.getArgb((int) (this.alpha * 2.55F), this.red, this.green, this.blue));
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        renderInGameBackground(context);
    }
}
