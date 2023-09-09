package be.elmital.highlightItem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class HighLightItemClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        Configurator.TOGGLE_BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.highlightitem.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "HighLightItem"));
        Configurator.COLOR_MENU = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.highlightitem.color_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "HighLightItem"));
        Configurator.COLOR_HOVERED_BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.highlightitem.color_hover", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "HighLightItem"));
        Configurator.COMPARATOR_BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.highlightitem.comparator", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "HighLightItem"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;
            if (Configurator.TOGGLE_BIND.wasPressed()) {
                HighlightItem.configurator.updateToggle(client.player);
            }

            if (Configurator.COLOR_MENU.wasPressed()) {
                client.setScreen(new ConfigurationScreen(client.options));
            }

            if (Configurator.COLOR_HOVERED_BIND.wasPressed()) {
                HighlightItem.configurator.updateColorHovered(!Configurator.COLOR_HOVERED, client.player);
            }

            if (Configurator.COMPARATOR_BIND.wasPressed()) {
                if (Configurator.COMPARATOR.ordinal() == ItemComparator.Comparators.values().length - 1) {
                    HighlightItem.configurator.updateMode(ItemComparator.Comparators.ITEM_ONLY, client.player);
                } else {
                    for (ItemComparator.Comparators mode : ItemComparator.Comparators.values()) {
                        if (mode.ordinal() == Math.min(Configurator.COMPARATOR.ordinal() + 1, ItemComparator.Comparators.values().length - 1)) {
                            HighlightItem.configurator.updateMode(mode, client.player);
                            break;
                        }
                    }
                }
            }
        });
    }
}
