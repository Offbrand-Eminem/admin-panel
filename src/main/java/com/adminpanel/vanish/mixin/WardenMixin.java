package me.drex.vanish.mixin;

import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1297;
import net.minecraft.class_7260;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_7260.class})
public abstract class WardenMixin {
   @Inject(
      method = {"method_42206"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_7260;method_5722(Lnet/minecraft/class_1297;)Z"
)},
      cancellable = true
   )
   public void excludeVanished(class_1297 entity, CallbackInfoReturnable<Boolean> cir) {
      if (ConfigManager.vanish().hideFromEntities && VanishAPI.isVanished(entity)) {
         cir.setReturnValue(false);
      }

   }
}
