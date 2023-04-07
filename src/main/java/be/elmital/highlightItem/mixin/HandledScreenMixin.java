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
import com.mojang.blaze3d.systems.RenderSystem;
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
		if (Configurator.TOGGLE) {
			Slot focusedSlot = ((HandledScreenAccessor) this).getFocusedSlot();

			if(focusedSlot != null && focusedSlot.getStack() != null) {
				ScreenHandler handler = ((HandledScreenAccessor) this).getHandler();

				for(int k = 0; k < handler.slots.size(); ++k) {
					Slot slot = handler.slots.get(k);

					if(focusedSlot.equals(slot) && !Configurator.COLOR_HOVERED)
						continue;

					if(slot.isEnabled() && !slot.getStack().isEmpty() && slot.getStack().getItem().equals(focusedSlot.getStack().getItem())) {
						var activeHighLightColor = Configurator.HIGHLIGHT_COLOR.getShaderColor();
						RenderSystem.setShaderColor(activeHighLightColor[0], activeHighLightColor[1], activeHighLightColor[2], activeHighLightColor[3]);
						drawSlotHighlight(matrices, slot.x, slot.y, 0);
					}
				}
				RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
	}

}
