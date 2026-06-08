package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_1297;
import net.minecraft.class_3222;
import net.minecraft.class_7299;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_7299.class})
public abstract class AllayAiMixin {
   @WrapOperation(
      method = {"method_43093"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3222;method_24516(Lnet/minecraft/class_1297;D)Z"
)}
   )
   private static boolean excludeVanished(class_3222 instance, class_1297 entity, double distance, Operation<Boolean> original) {
      return (Boolean)original.call(new Object[]{instance, entity, distance}) && !VanishAPI.isVanished(instance);
   }
}
