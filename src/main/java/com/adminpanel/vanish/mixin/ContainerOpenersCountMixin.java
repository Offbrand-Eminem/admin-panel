package me.drex.vanish.mixin;

import java.util.function.Predicate;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_1297;
import net.minecraft.class_5561;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({class_5561.class})
public class ContainerOpenersCountMixin {
   @ModifyArg(
      method = {"method_56121"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1937;method_8333(Lnet/minecraft/class_1297;Lnet/minecraft/class_238;Ljava/util/function/Predicate;)Ljava/util/List;"
),
      index = 2
   )
   private Predicate<class_1297> excludeVanished(Predicate<class_1297> predicate) {
      return predicate.and((entity) -> !VanishAPI.isVanished(entity));
   }
}
