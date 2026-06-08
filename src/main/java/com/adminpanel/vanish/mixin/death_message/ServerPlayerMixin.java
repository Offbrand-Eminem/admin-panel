package me.drex.vanish.mixin.death_message;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import net.minecraft.class_3324;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_3222.class})
public abstract class ServerPlayerMixin {
   @WrapOperation(
      method = {"method_6078"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3324;method_14564(Lnet/minecraft/class_1657;Lnet/minecraft/class_2561;)V"
)}
   )
   private void hideTeamDeathMessage(class_3324 playerList, class_1657 player, class_2561 component, Operation<Void> original) {
      if (VanishAPI.isVanished((class_3222)this)) {
         VanishAPI.broadcastHiddenMessage((class_3222)this, component);
      } else {
         original.call(new Object[]{playerList, player, component});
      }

   }

   @WrapOperation(
      method = {"method_6078"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3324;method_14565(Lnet/minecraft/class_1657;Lnet/minecraft/class_2561;)V"
)}
   )
   private void hideExceptTeamDeathMessage(class_3324 playerList, class_1657 player, class_2561 component, Operation<Void> original) {
      if (VanishAPI.isVanished((class_3222)this)) {
         VanishAPI.broadcastHiddenMessage((class_3222)this, component);
      } else {
         original.call(new Object[]{playerList, player, component});
      }

   }

   @WrapOperation(
      method = {"method_6078"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3324;method_43514(Lnet/minecraft/class_2561;Z)V"
)}
   )
   private void hideDeathMessage(class_3324 playerList, class_2561 component, boolean overlay, Operation<Void> original) {
      if (VanishAPI.isVanished((class_3222)this)) {
         VanishAPI.broadcastHiddenMessage((class_3222)this, component);
      } else {
         original.call(new Object[]{playerList, component, overlay});
      }

   }
}
