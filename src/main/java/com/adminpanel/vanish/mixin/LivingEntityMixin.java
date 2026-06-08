package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1309;
import net.minecraft.class_2394;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1309.class})
public abstract class LivingEntityMixin {
   @WrapOperation(
      method = {"method_5623"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3218;method_65096(Lnet/minecraft/class_2394;DDDIDDDD)I"
)}
   )
   public <T extends class_2394> int hideFallingParticles(class_3218 instance, T particleOptions, double d, double e, double f, int i, double g, double h, double j, double k, Operation<Integer> original) {
      return !VanishAPI.isVanished((class_1309)this) ? (Integer)original.call(new Object[]{instance, particleOptions, d, e, f, i, g, h, j, k}) : 0;
   }

   @Inject(
      method = {"method_36608"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void hideFromEntities(CallbackInfoReturnable<Boolean> cir) {
      if (ConfigManager.vanish().hideFromEntities && VanishAPI.isVanished((class_1309)this)) {
         cir.setReturnValue(false);
      }

   }
}
