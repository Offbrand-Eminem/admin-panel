package me.drex.vanish.mixin.interaction;

import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_10774;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2231;
import net.minecraft.class_2338;
import net.minecraft.class_2538;
import net.minecraft.class_2680;
import net.minecraft.class_5801;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2231.class, class_5801.class, class_2538.class})
public abstract class InsideBlockMixin {
   @Inject(
      method = {"method_9548"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cancelEntityInsideBlock(class_2680 blockState, class_1937 level, class_2338 blockPos, class_1297 entity, class_10774 insideBlockEffectApplier, boolean bl, CallbackInfo ci) {
      if (VanishAPI.isVanished(entity) && ConfigManager.vanish().interaction.blocks) {
         ci.cancel();
      }

   }
}
