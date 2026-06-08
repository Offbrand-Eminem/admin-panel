package me.drex.vanish.mixin.interaction;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_3222;
import net.minecraft.class_3898;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_3898.class})
public abstract class ChunkMapMixin {
   @ModifyReturnValue(
      method = {"method_18722"},
      at = {@At("RETURN")}
   )
   public boolean preventChunkGeneration(boolean original, class_3222 player) {
      return original || ConfigManager.vanish().interaction.chunkLoading && VanishAPI.isVanished(player);
   }

   @WrapOperation(
      method = {"method_38782"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3222;method_7325()Z"
)}
   )
   public boolean preventMobSpawning(class_3222 player, Operation<Boolean> original) {
      return (Boolean)original.call(new Object[]{player}) || ConfigManager.vanish().interaction.mobSpawning && VanishAPI.isVanished(player);
   }
}
