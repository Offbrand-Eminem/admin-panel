package me.drex.vanish.mixin;

import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_11212;
import net.minecraft.class_1309;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_11212.class})
public interface WaypointTransmitterMixin {
   @Inject(
      method = {"method_70796"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void hideWaypoints(class_1309 livingEntity, class_3222 serverPlayer, CallbackInfoReturnable<Boolean> cir) {
      if (livingEntity instanceof class_3222 actor) {
         if (!VanishAPI.canSeePlayer(actor, serverPlayer)) {
            cir.setReturnValue(true);
         }
      }

   }
}
