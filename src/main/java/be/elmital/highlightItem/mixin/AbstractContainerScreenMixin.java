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
import be.elmital.highlightItem.ItemComparator;
import be.elmital.highlightItem.UnsupportedMinecraftClassOperationException;
import be.elmital.highlightItem.Utils;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
	@Shadow protected abstract void extractSlotHighlightFront(GuiGraphicsExtractor context);

	@Shadow @Nullable protected Slot hoveredSlot;

	@Unique private boolean highlightItemCompatible;

	@Inject(method = "<init>(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/network/chat/Component;II)V", at	= @At(value = "TAIL"))
	private void construct(CallbackInfo ci, @Local(argsOnly = true) AbstractContainerMenu menu) {
		// Check possible incompatibility
		try {
			Utils.isInPlayerInventory(menu.getSlot(0), (AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this);
			this.highlightItemCompatible = true;
		} catch (UnsupportedMinecraftClassOperationException e) {
			this.highlightItemCompatible = false;
			HighlightItem.LOGGER.error(e);
		} catch (UnsupportedOperationException e) {
			// Change Screen context value if the Screen context is set to non default value
			if (!Configurator.SCREEN_CONTEXT.equals(Configurator.ScreenContext.EVERYWHERE) || !Configurator.SCREEN_CONTEXT.equals(Configurator.ScreenContext.EXCLUDE_CREATIVE)) {
				Configurator.SCREEN_CONTEXT = Configurator.ScreenContext.EXCLUDE_CREATIVE;
				Minecraft.getInstance().player.sendSystemMessage(Component.literal("The option for Screen limitation have been deactivated due to a compatibility issue! Please, check your logs and report it to the HighLightItem issue tracker.").withColor(TextColor.RED));
			}

			this.highlightItemCompatible = false;
			HighlightItem.LOGGER.error(e);
		}
	}

	@Inject(method = "extractSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/inventory/Slot;II)V", shift = At.Shift.AFTER))
	private void drawSlot(GuiGraphicsExtractor guiGraphics, int i, int j, CallbackInfo ci, @Local Slot slot) {
		if (Configurator.TOGGLE) {
			if (hoveredSlot == null)
				return;

			if (hoveredSlot.equals(slot))  {
				if (!slot.isHighlightable())
					return;
                if (Configurator.COLOR_HOVERED.equals(Configurator.ColorHoveredOptions.COLORED) || (Configurator.COLOR_HOVERED.equals(Configurator.ColorHoveredOptions.COLORED_NOT_EMPTY) && !slot.getItem().isEmpty())) {
					HighlightItem.toDrawFromMod = slot;
					extractSlotHighlightFront(guiGraphics);
					HighlightItem.toDrawFromMod = null;
				} else if (Configurator.COLOR_HOVERED.equals(Configurator.ColorHoveredOptions.VANILLA_COLORED) || (Configurator.COLOR_HOVERED.equals(Configurator.ColorHoveredOptions.VANILLA_COLORED_NOT_EMPTY) && !slot.getItem().isEmpty())) {
					extractSlotHighlightFront(guiGraphics);
				}
				return;
			}

			if (!slot.isActive() || slot.getItem().isEmpty())
				return;

			if (shouldSkip(slot))
				return;

			if (ItemComparator.test(Configurator.COMPARATOR, hoveredSlot.getItem(), slot.getItem())) {
				HighlightItem.toDrawFromMod = slot;
				extractSlotHighlightFront(guiGraphics);
				HighlightItem.toDrawFromMod = null;
			}
		}
	}

	@Unique
    @SuppressWarnings("ConstantConditions")
	private boolean shouldSkip(Slot slot) {
		if (!this.highlightItemCompatible)
			return false;

		if (Configurator.SCREEN_CONTEXT.equals(Configurator.ScreenContext.EVERYWHERE))
			return false;

		if (Configurator.SCREEN_CONTEXT.excludeCreativeScreen() && CreativeModeInventoryScreen.class.isInstance(this))
			return true;

		try {
			if (Configurator.SCREEN_CONTEXT.inContainer() && Utils.isInPlayerInventory(slot, (AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this))
				return true;

			if (Configurator.SCREEN_CONTEXT.inPlayerInventoryPart() && !Utils.isInPlayerInventory(slot, (AbstractContainerScreen<? extends AbstractContainerMenu>) (Object) this))
				return true;
		} catch (UnsupportedOperationException _) {}

		return false;
	}

	@ModifyArgs(method = "extractSlotHighlightFront", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"))
	private void colorizeIfMod(Args args) {
		if (HighlightItem.toDrawFromMod != null) {
			if (Configurator.COLOR == Colors.HighLightColor.DEFAULT.colorInteger()) {
				args.set(2, HighlightItem.toDrawFromMod.x - 4);
				args.set(3, HighlightItem.toDrawFromMod.y - 4);
			} else {
				args.set(2, HighlightItem.toDrawFromMod.x);
				args.set(3, HighlightItem.toDrawFromMod.y);
				args.set(4, HighlightItem.toDrawFromMod.x + 16);
				args.set(5, HighlightItem.toDrawFromMod.y + 16);
			}
		}
	}

	@Inject(method = "keyPressed", at = @At("RETURN"))
	private boolean keyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> info) {
		if (Configurator.TOGGLE_BIND.matches(input)) {
			HighlightItem.configurator.updateToggle(Minecraft.getInstance().player, Configurator.NotificationContext.ON_SCREEN);
			return true;
		}

		if (!Configurator.TOGGLE)
			return info.getReturnValue();

		if (Configurator.COLOR_HOVERED_BIND.matches(input)) {
			HighlightItem.configurator.changeColorHovered(Minecraft.getInstance().player, Configurator.NotificationContext.ON_SCREEN);
			return true;
		} else if (Configurator.COMPARATOR_BIND.matches(input)) {
			HighlightItem.configurator.changeMode(Minecraft.getInstance().player, Configurator.NotificationContext.ON_SCREEN);
			return true;
		} else {
			return info.getReturnValue();
		}
	}
}
