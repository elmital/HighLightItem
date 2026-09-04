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

package be.elmital.highlightItem.mixin;

import be.elmital.highlightItem.Colors;
import be.elmital.highlightItem.Configurator;
import be.elmital.highlightItem.HighlightItem;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {
    @Shadow public abstract void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd);

    @Inject(method = "blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V"), cancellable = true)
    private void modifyMethodIfModCall(RenderPipeline renderPipeline, Identifier sprite, int x, int y, int width, int height, CallbackInfo ci) {
        if (HighlightItem.toDrawFromMod != null) {
            if (Configurator.COLOR == Colors.HighLightColor.DEFAULT.colorInteger())
                return;
            this.fillGradient(x, y, width, height, Configurator.COLOR, Configurator.COLOR);
            ci.cancel();
        }
    }
}
