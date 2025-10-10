package be.elmital.highlightItem.mixin;

import net.minecraft.client.toast.SystemToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SystemToast.class)
public interface SystemToastAccessor {
    @Mutable
    @Accessor("width")
    void setWidth(int width);
}
