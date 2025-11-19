package be.elmital.highlightItem.libs;

import be.elmital.highlightItem.ConfigurationScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.Minecraft;

public class ModMenuAPIIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> new ConfigurationScreen(screen, Minecraft.getInstance().options);
    }
}
