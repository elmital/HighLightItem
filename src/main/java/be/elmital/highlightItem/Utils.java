package be.elmital.highlightItem;


import be.elmital.highlightItem.mixin.AbstractMountInventoryScreenAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.AbstractMountInventoryScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CrafterScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class Utils {
    // https://minecraft.wiki/w/Java_Edition_protocol/Inventory
    public static <T extends AbstractContainerMenu> boolean isInPlayerInventory(Slot slot, AbstractContainerScreen<T> screen) throws UnsupportedOperationException {
        if (screen instanceof InventoryScreen)
            return true;
        if (screen instanceof CreativeModeInventoryScreen)
            return slot.index > 44;
        if (screen instanceof AbstractMountInventoryScreen abstractMountInventoryScreen) {
            // If there is columns it means the mount has a chest where we can store items, each column contains 3 slots
            int chestSlots = ((AbstractMountInventoryScreenAccessor) abstractMountInventoryScreen).getInventoryColumns() * 3;
            return slot.index > 1 + chestSlots;
        }
        if (screen instanceof ContainerScreen containerScreen)
            return slot.index >= containerScreen.getMenu().getRowCount() * 9;
        if (screen instanceof DispenserScreen || screen instanceof CrafterScreen)
            return slot.index > 8 && slot.index < 45; // Crafter have another slot but is index is 45
        if (screen instanceof AnvilScreen)
            return slot.index > 2;
        if (screen instanceof BeaconScreen)
            return slot.index > 0;
        if (screen instanceof AbstractFurnaceScreen)
            return slot.index > 2;
        if (screen instanceof BrewingStandScreen)
            return slot.index > 4;
        if (screen instanceof CraftingScreen)
            return slot.index > 9;
        if (screen instanceof EnchantmentScreen)
            return slot.index > 1;
        if (screen instanceof GrindstoneScreen)
            return slot.index > 2;
        if (screen instanceof HopperScreen)
            return slot.index > 4;
        if (screen instanceof LoomScreen)
            return slot.index > 3;
        if (screen instanceof MerchantScreen)
            return slot.index > 2;
        if (screen instanceof ShulkerBoxScreen)
            return slot.index > 26;
        if (screen instanceof SmithingScreen)
            return slot.index > 3;
        if (screen instanceof CartographyTableScreen)
            return slot.index > 2;
        if (screen instanceof StonecutterScreen)
            return slot.index > 1;

        if (screen.getClass().getName().startsWith("net.minecraft"))
            throw new UnsupportedMinecraftClassOperationException("Missing class for inventory indexes checks '" + screen.getClass().getName() + "', please report the issue in the HighLightItem issue tracker!");
        throw new UnsupportedOperationException("A mod is implementing the Screen interface " + screen.getClass().getName() + " and the mod doesn't support it this could cause issue with Screen limitations option!");
    }
}
