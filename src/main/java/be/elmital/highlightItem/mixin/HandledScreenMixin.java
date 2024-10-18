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

import be.elmital.highlightItem.Configurator;
import be.elmital.highlightItem.HighlightItem;
import be.elmital.highlightItem.ItemComparator;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;


@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
	@Shadow protected abstract void drawSlotHighlightFront(DrawContext context);

	@Shadow @Nullable protected Slot focusedSlot;

	@Unique
	private static Slot toDrawFromMod = null;

	@Inject(method = "drawSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlot(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/screen/slot/Slot;)V", shift = At.Shift.AFTER))
	private void drawSlot(DrawContext context, CallbackInfo ci, @Local Slot slot) {
		if (Configurator.TOGGLE) {
			if (Configurator.notificationTicks > 0 && Configurator.notification != null)
				context.drawCenteredTextWithShadow(HighlightItem.CLIENT.textRenderer, Configurator.notification, ((HandledScreenAccessor) this).getBackgroundWidth() / 2, ((HandledScreenAccessor) this).getBackgroundHeight() + 2, 0);

			if (focusedSlot == null)
				return;

			if (focusedSlot.equals(slot) && !Configurator.COLOR_HOVERED)
				return;

			if (slot.isEnabled() && !slot.getStack().isEmpty() && ItemComparator.test(Configurator.COMPARATOR, focusedSlot.getStack(), slot.getStack())) {
				toDrawFromMod = slot;
				drawSlotHighlightFront(context);
			}
		}
	}

	@Redirect(method = "drawSlotHighlightFront", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"))
	private void colorizeIfMod(DrawContext context, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height) {
		// TODO if using vanilla use texture
		if (toDrawFromMod != null) {
			context.fillGradient(RenderLayer.getGuiOverlay(), toDrawFromMod.x, toDrawFromMod.y, toDrawFromMod.x + 16, toDrawFromMod.y + 16, Configurator.COLOR, Configurator.COLOR, 0);
			toDrawFromMod = null;
		} else {

		}
	}

	@Inject(method = "keyPressed(III)Z", at = @At("RETURN"))
	private boolean keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
		assert HighlightItem.CLIENT.player != null;
		if (Configurator.TOGGLE_BIND.matchesKey(keyCode, scanCode)) {
            HighlightItem.configurator.updateToggle(HighlightItem.CLIENT.player, Configurator.NotificationType.ON_SCREEN);
			return true;
		}

		if (!Configurator.TOGGLE)
			return info.getReturnValue();

		if (Configurator.COLOR_HOVERED_BIND.matchesKey(keyCode, scanCode)) {
			HighlightItem.configurator.updateColorHovered(!Configurator.COLOR_HOVERED, HighlightItem.CLIENT.player, Configurator.NotificationType.ON_SCREEN);
			return true;
		} else if (Configurator.COMPARATOR_BIND.matchesKey(keyCode, scanCode)) {
			HighlightItem.configurator.changeMode(HighlightItem.CLIENT.player, Configurator.NotificationType.ON_SCREEN);
			return true;
		} else {
			return info.getReturnValue();
		}
	}
}
