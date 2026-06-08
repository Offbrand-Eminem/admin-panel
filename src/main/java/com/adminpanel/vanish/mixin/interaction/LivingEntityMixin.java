package me.drex.vanish.mixin.interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_1309.class})
public abstract class LivingEntityMixin {
   @WrapOperation(
      method = {"method_5810"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1309;method_7325()Z"
)}
   )
   public boolean preventPushing(class_1309 entity, Operation<Boolean> original) {
      return (Boolean)original.call(new Object[]{entity}) || ConfigManager.vanish().interaction.entityCollisions && VanishAPI.isVanished(entity);
   }

   @WrapOperation(
      method = {"method_63625"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1309;method_7325()Z"
)}
   )
   public boolean preventArmorItemEquip(class_1309 entity, Operation<Boolean> original) {
      return (Boolean)original.call(new Object[]{entity}) || ConfigManager.vanish().interaction.entityPickup && VanishAPI.isVanished(entity);
   }
}
