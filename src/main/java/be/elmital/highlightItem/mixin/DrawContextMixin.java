package be.elmital.highlightItem.mixin;

import be.elmital.highlightItem.Colors;
import be.elmital.highlightItem.Configurator;
import be.elmital.highlightItem.HighlightItem;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow public abstract void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd);

    @Inject(method = "drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIII)V"), cancellable = true)
    private void modifyMethodIfModCall(RenderPipeline renderPipeline, Identifier sprite, int x, int y, int width, int height, CallbackInfo ci) {
        if (HighlightItem.toDrawFromMod != null) {
            if (Configurator.COLOR == Colors.HighLightColor.DEFAULT.colorInteger())
                return;
            this.fillGradient(x, y, width, height, Configurator.COLOR, Configurator.COLOR);
            ci.cancel();
        }
    }
}
