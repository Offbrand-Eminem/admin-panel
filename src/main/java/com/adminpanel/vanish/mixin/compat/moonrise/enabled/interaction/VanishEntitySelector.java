package me.drex.vanish.mixin.compat.moonrise.enabled.interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1297;
import net.minecraft.class_1924;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class VanishEntitySelector {
   @Mixin(
      value = {class_1924.class},
      priority = 1500
   )
   public interface EntityGetterMixin {
      @WrapOperation(
         method = {"method_20743"},
         at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_7325()Z"
)}
      )
      default boolean preventEntityCollisions(class_1297 entity, Operation<Boolean> original) {
         return (Boolean)original.call(new Object[]{entity}) || ConfigManager.vanish().interaction.entityCollisions && VanishAPI.isVanished(entity);
      }

      @WrapOperation(
         method = {"method_20743"},
         at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_30948(Lnet/minecraft/class_1297;)Z"
)}
      )
      default boolean preventEntityCollisions2(class_1297 entity, class_1297 other, Operation<Boolean> original) {
         return (Boolean)original.call(new Object[]{entity}) || ConfigManager.vanish().interaction.entityCollisions && VanishAPI.isVanished(entity);
      }
   }
}
