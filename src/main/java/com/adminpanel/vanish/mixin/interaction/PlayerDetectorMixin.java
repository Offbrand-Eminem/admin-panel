package me.drex.vanish.mixin.interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1657;
import net.minecraft.class_8962;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_8962.class})
public interface PlayerDetectorMixin {
   @WrapOperation(
      method = {"method_56723"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1657;method_7325()Z"
)}
   )
   private static boolean preventTrialSpawning(class_1657 player, Operation<Boolean> original) {
      boolean cancel = ConfigManager.vanish().interaction.mobSpawning && VanishAPI.isVanished(player);
      return (Boolean)original.call(new Object[]{player}) || cancel;
   }

   @WrapOperation(
      method = {"method_56721"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1657;method_7325()Z"
)}
   )
   private static boolean preventVaultOpening(class_1657 player, Operation<Boolean> original) {
      boolean cancel = ConfigManager.vanish().interaction.blocks && VanishAPI.isVanished(player);
      return (Boolean)original.call(new Object[]{player}) || cancel;
   }
}
