package me.drex.vanish.mixin.interaction;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_1657.class})
public abstract class PlayerMixin {
   @WrapWithCondition(
      method = {"method_7341"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_5694(Lnet/minecraft/class_1657;)V"
)}
   )
   private boolean preventPickup(class_1297 entity, class_1657 player) {
      return !VanishAPI.isVanished(player) || !ConfigManager.vanish().interaction.entityPickup;
   }

   @ModifyReturnValue(
      method = {"method_49108"},
      at = {@At("RETURN")}
   )
   public boolean preventProjectileHits(boolean original) {
      if (!original) {
         return false;
      } else {
         return !VanishAPI.isVanished((class_1657)this) || !ConfigManager.vanish().interaction.entityCollisions;
      }
   }
}
