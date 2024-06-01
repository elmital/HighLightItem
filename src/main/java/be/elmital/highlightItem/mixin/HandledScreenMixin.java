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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public class HandledScreenMixin {

	@Inject(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V"
			)
	)
	private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
		if (Configurator.TOGGLE) {
			Slot focusedSlot = ((HandledScreenAccessor) this).getFocusedSlot();

			if (focusedSlot != null && focusedSlot.getStack() != null) {
				ScreenHandler handler = ((HandledScreenAccessor) this).getHandler();

				for (int k = 0; k < handler.slots.size(); ++k) {
					Slot slot = handler.slots.get(k);

					if (focusedSlot.equals(slot) && !Configurator.COLOR_HOVERED)
						continue;

					if (slot.isEnabled() && !slot.getStack().isEmpty() && ItemComparator.test(Configurator.COMPARATOR, focusedSlot.getStack(), slot.getStack())) {
						Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
						VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGuiOverlay());
						vertexConsumer.vertex(matrix4f, (float)slot.x, (float)slot.y, (float)0).color(Configurator.COLOR[0], Configurator.COLOR[1], Configurator.COLOR[2], Configurator.COLOR[3]);
						vertexConsumer.vertex(matrix4f, (float)slot.x, (float)slot.y + 16, (float)0).color(Configurator.COLOR[0], Configurator.COLOR[1], Configurator.COLOR[2], Configurator.COLOR[3]);
						vertexConsumer.vertex(matrix4f, (float)slot.x + 16, (float)slot.y + 16, (float)0).color(Configurator.COLOR[0], Configurator.COLOR[1], Configurator.COLOR[2], Configurator.COLOR[3]);
						vertexConsumer.vertex(matrix4f, (float)slot.x + 16, (float)slot.y, (float)0).color(Configurator.COLOR[0], Configurator.COLOR[1], Configurator.COLOR[2], Configurator.COLOR[3]);
						context.draw();
					}
				}
			}
		}
		if (Configurator.notificationTicks > 0 && Configurator.notification != null)
			context.drawCenteredTextWithShadow(HighlightItem.CLIENT.textRenderer, Configurator.notification, ((HandledScreenAccessor) this).getBackgroundWidth() / 2, ((HandledScreenAccessor) this).getBackgroundHeight() + 2, 0);
	}

	@Inject(
			method = "keyPressed(III)Z",
			at = @At("RETURN")
	)
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
