package me.drex.vanish.mixin.interaction;

import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_5712;
import net.minecraft.class_6880;
import net.minecraft.class_8514;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_8514.class_8516.class})
public class VibrationSystemMixin {
   @Inject(
      method = {"method_32947"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void preventEntityVibrations(class_3218 serverLevel, class_6880<class_5712> holder, class_5712.class_7397 context, class_243 vec3, CallbackInfoReturnable<Boolean> cir) {
      class_1297 sourceEntity = context.comp_713();
      if (sourceEntity != null && VanishAPI.isVanished(sourceEntity) && ConfigManager.vanish().interaction.vibrations) {
         cir.setReturnValue(false);
      }

   }
}
