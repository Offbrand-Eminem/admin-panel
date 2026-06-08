package me.drex.vanish.mixin.death_message;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_1282;
import net.minecraft.class_1283;
import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_1283.class})
public abstract class CombatTrackerMixin {
   @WrapOperation(
      method = {"method_5548"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1282;method_5529()Lnet/minecraft/class_1297;"
)}
   )
   public class_1297 hideVanished(class_1282 instance, Operation<class_1297> original) {
      class_1297 entity = (class_1297)original.call(new Object[]{instance});
      return VanishAPI.isVanished(entity) ? null : entity;
   }
}
