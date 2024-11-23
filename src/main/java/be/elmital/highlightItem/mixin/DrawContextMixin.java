package be.elmital.highlightItem.mixin;

import be.elmital.highlightItem.Colors;
import be.elmital.highlightItem.Configurator;
import be.elmital.highlightItem.HighlightItem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Shadow public abstract void fillGradient(RenderLayer layer, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z);

    @Inject(method = "drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIIII)V"), cancellable = true)
    private void modifyMethodIfModCall(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height, CallbackInfo ci) {
        if (HighlightItem.toDrawFromMod != null) {
            if (Configurator.COLOR == Colors.HighLightColor.DEFAULT.colorInteger())
                return;
            this.fillGradient(RenderLayer.getGuiOverlay(), x, y, width, height, Configurator.COLOR, Configurator.COLOR, 0);
            ci.cancel();
        }
    }
}
