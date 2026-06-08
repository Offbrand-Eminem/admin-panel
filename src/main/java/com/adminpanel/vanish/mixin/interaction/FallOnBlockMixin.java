package me.drex.vanish.mixin.interaction;

import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2344;
import net.minecraft.class_2542;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2344.class, class_2542.class})
public abstract class FallOnBlockMixin {
   @Inject(
      method = {"method_9554"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cancelEntityFallOnBlock(class_1937 level, class_2680 blockState, class_2338 blockPos, class_1297 entity, double d, CallbackInfo ci) {
      if (VanishAPI.isVanished(entity) && ConfigManager.vanish().interaction.blocks) {
         ci.cancel();
      }

   }
}
