package me.drex.vanish.mixin;

import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_11565;
import net.minecraft.class_1730;
import net.minecraft.class_2595;
import net.minecraft.class_2627;
import net.minecraft.class_3222;
import net.minecraft.class_3719;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_3719.class, class_2595.class, class_1730.class, class_2627.class})
public abstract class ContainerMixin {
   @Inject(
      method = {"method_5435"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void cancelOpenAnimation(class_11565 player, CallbackInfo ci) {
      if (player instanceof class_3222 serverPlayer) {
         if (VanishAPI.isVanished(serverPlayer)) {
            ci.cancel();
         }
      }

   }

   @Inject(
      method = {"method_5432"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void cancelCloseAnimation(class_11565 player, CallbackInfo ci) {
      if (player instanceof class_3222 serverPlayer) {
         if (VanishAPI.isVanished(serverPlayer)) {
            ci.cancel();
         }
      }

   }
}
