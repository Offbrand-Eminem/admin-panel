package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_1297;
import net.minecraft.class_1924;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
   value = {class_1924.class},
   priority = 1500
)
public interface EntityGetterMixin {
   @WrapOperation(
      method = {"method_8611"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_1297;field_23807:Z"
)}
   )
   default boolean noBlockObstruction(class_1297 entity, Operation<Boolean> original) {
      if (entity instanceof class_3222 serverPlayer) {
         if (VanishAPI.isVanished(serverPlayer)) {
            return false;
         }
      }

      return (Boolean)original.call(new Object[]{entity});
   }
}
