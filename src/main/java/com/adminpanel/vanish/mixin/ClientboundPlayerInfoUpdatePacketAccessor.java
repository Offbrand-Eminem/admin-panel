package me.drex.vanish.mixin;

import java.util.List;
import net.minecraft.class_2703;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2703.class})
public interface ClientboundPlayerInfoUpdatePacketAccessor {
   @Accessor("field_12369")
   @Mutable
   void setEntries(List<class_2703.class_2705> var1);
}
