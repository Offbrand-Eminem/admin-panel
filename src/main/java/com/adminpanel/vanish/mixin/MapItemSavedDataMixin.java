package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_22;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_22.class})
public abstract class MapItemSavedDataMixin {
   @Shadow
   protected abstract void method_32368(String var1);

   @WrapOperation(
      method = {"method_102"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_22;field_114:Z",
   ordinal = 0
)}
   )
   private boolean hideFromMap(class_22 instance, Operation<Boolean> original, @Local class_22.class_23 holdingPlayer) {
      if (VanishAPI.isVanished(holdingPlayer.field_125)) {
         this.method_32368(holdingPlayer.field_125.method_5477().getString());
         return false;
      } else {
         return (Boolean)original.call(new Object[]{instance});
      }
   }
}
