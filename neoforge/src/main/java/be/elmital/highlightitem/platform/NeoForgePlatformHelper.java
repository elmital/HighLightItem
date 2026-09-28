package be.elmital.highlightitem.platform;

import be.elmital.highlightitem.IScheduler;
import be.elmital.highlightitem.platform.services.IPlatformHelper;
import net.minecraft.client.KeyMapping;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public KeyMapping registerKeyBind(KeyMapping keyMapping) {
        // TODO
        return null;
    }

    @Override
    public IScheduler getScheduler() {
        // TODO
        return null;
    }

    @Override
    public Path getConfigDir() {
        // TODO
        return null;
    }
}