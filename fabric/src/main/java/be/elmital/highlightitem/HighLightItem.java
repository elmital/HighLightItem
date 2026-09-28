package be.elmital.highlightitem;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class HighLightItem implements ModInitializer {

    @Override
    public void onInitialize() {
        HighLightItemCommon.init();
        Constants.LOG.info("Registering key bind and notification tracking");
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;
            if (Configurator.TOGGLE_BIND.consumeClick()) {
                HighLightItemCommon.configurator.updateToggle(client.player, Configurator.NotificationContext.IN_GAME);
            }

            if (Configurator.COLOR_MENU.consumeClick()) {
                client.setScreenAndShow(new ConfigurationScreen(client.options));
            }

            if (Configurator.COLOR_HOVERED_BIND.consumeClick()) {
                HighLightItemCommon.configurator.changeColorHovered(client.player, Configurator.NotificationContext.IN_GAME);
            }

            if (Configurator.COMPARATOR_BIND.consumeClick()) {
                HighLightItemCommon.configurator.changeMode(client.player, Configurator.NotificationContext.IN_GAME);
            }
        });
    }
}
