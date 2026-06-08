package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_2561;
import net.minecraft.class_2985;
import net.minecraft.class_3222;
import net.minecraft.class_3324;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_2985.class})
public abstract class PlayerAdvancementsMixin {
   @Shadow
   private class_3222 field_13391;

   @WrapOperation(
      method = {"method_53637"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3324;method_43514(Lnet/minecraft/class_2561;Z)V"
)}
   )
   public void hideAdvancementMessage(class_3324 playerList, class_2561 component, boolean bl, Operation<Void> original) {
      if (VanishAPI.isVanished(this.field_13391)) {
         VanishAPI.broadcastHiddenMessage(this.field_13391, component);
      } else {
         original.call(new Object[]{playerList, component, bl});
      }

   }
}
