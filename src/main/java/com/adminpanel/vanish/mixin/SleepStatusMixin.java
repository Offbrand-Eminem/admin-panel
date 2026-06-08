package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_3222;
import net.minecraft.class_5838;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_5838.class})
public abstract class SleepStatusMixin {
   @WrapOperation(
      method = {"method_33814"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3222;method_7325()Z"
)}
   )
   public boolean hideSleeping(class_3222 player, Operation<Boolean> original) {
      return (Boolean)original.call(new Object[]{player}) || VanishAPI.isVanished(player);
   }
}
