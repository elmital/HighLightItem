package be.elmital.highlightitem;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class HighLightItem {

    public HighLightItem(IEventBus eventBus) {
        // TODO
        HighLightItemCommon.init();
    }
}