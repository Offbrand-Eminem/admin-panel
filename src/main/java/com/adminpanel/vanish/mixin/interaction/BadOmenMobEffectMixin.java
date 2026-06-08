package me.drex.vanish.mixin.interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
   targets = {"net/minecraft/class_8631"}
)
public abstract class BadOmenMobEffectMixin {
   @WrapOperation(
      method = {"method_5572"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3222;method_7325()Z"
)}
   )
   public boolean preventRaid(class_3222 serverPlayer, Operation<Boolean> original) {
      Boolean isSpectator = (Boolean)original.call(new Object[]{serverPlayer});
      if (!ConfigManager.vanish().interaction.mobSpawning) {
         return isSpectator;
      } else {
         return isSpectator || VanishAPI.isVanished(serverPlayer);
      }
   }
}
