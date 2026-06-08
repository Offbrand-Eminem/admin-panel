package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_2168.class})
public abstract class CommandSourceStackMixin {
   @Shadow
   @Final
   private @Nullable class_1297 field_9820;

   @WrapOperation(
      method = {"method_9212"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3222;method_64398(Lnet/minecraft/class_2561;)V"
)}
   )
   public void hideCommandFeedback(class_3222 observer, class_2561 component, Operation<Void> original) {
      class_1297 var5 = this.field_9820;
      if (var5 instanceof class_3222 actor) {
         if (VanishAPI.isVanished(actor)) {
            VanishAPI.sendHiddenMessage(actor, observer, component);
            return;
         }
      }

      original.call(new Object[]{observer, component});
   }
}
