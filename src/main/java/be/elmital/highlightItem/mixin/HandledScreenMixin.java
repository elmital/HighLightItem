package be.elmital.highlightItem.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.screen.ingame.HandledScreen.drawSlotHighlight;


@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public class HandledScreenMixin {

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V"
			)
	)
	private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
		ScreenHandler handler = ((HandledScreenAccessor) this).getHandler();
		Slot focusedSlot = ((HandledScreenAccessor) this).getFocusedSlot();

		int zOffset = ((HandledScreen) (Object)this).getZOffset();

		for(int k = 0; k < handler.slots.size(); ++k) {
			Slot slot = handler.slots.get(k);
			if(focusedSlot == null)
				break;

			if(slot.isEnabled() && focusedSlot.getStack() != null && !slot.getStack().isEmpty() && slot.getStack().getItem().equals(focusedSlot.getStack().getItem())) {
				drawSlotHighlight(matrices, slot.x, slot.y, zOffset);
			}
		}
	}

}
