package be.elmital.highlightItem.mixin;

import be.elmital.highlightItem.Colors;
import be.elmital.highlightItem.Configurator;
import be.elmital.highlightItem.HighlightItem;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GuiGraphics.class)
public abstract class DrawContextMixin {
    @Shadow public abstract void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd);

    @Inject(method = "blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIIII)V"), cancellable = true)
    private void modifyMethodIfModCall(RenderPipeline renderPipeline, ResourceLocation sprite, int x, int y, int width, int height, CallbackInfo ci) {
        if (HighlightItem.toDrawFromMod != null) {
            if (Configurator.COLOR == Colors.HighLightColor.DEFAULT.colorInteger())
                return;
            this.fillGradient(x, y, width, height, Configurator.COLOR, Configurator.COLOR);
            ci.cancel();
        }
    }
}
