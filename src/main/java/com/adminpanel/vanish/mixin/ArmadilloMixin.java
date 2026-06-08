package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1657;
import net.minecraft.class_9069;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_9069.class})
public abstract class ArmadilloMixin {
   @WrapOperation(
      method = {"method_55721"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1657;method_7325()Z"
)}
   )
   public boolean preventPhantoms(class_1657 player, Operation<Boolean> original) {
      Boolean isSpectator = (Boolean)original.call(new Object[]{player});
      if (!ConfigManager.vanish().hideFromEntities) {
         return isSpectator;
      } else {
         return isSpectator || VanishAPI.isVanished(player);
      }
   }
}
