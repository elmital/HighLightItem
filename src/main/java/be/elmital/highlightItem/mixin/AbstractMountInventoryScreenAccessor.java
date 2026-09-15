package be.elmital.highlightItem.mixin;


import net.minecraft.client.gui.screens.inventory.AbstractMountInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractMountInventoryScreen.class)
public interface AbstractMountInventoryScreenAccessor {
    @Accessor("inventoryColumns")
    int getInventoryColumns();
}
