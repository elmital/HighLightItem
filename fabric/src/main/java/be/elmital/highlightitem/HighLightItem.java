package be.elmital.highlightitem;

import net.fabricmc.api.ModInitializer;

public class HighLightItem implements ModInitializer {

    @Override
    public void onInitialize() {
        HighLightItemCommon.init();
    }
}
