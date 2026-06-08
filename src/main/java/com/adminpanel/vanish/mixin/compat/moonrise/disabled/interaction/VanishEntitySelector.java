package me.drex.vanish.mixin.compat.moonrise.disabled.interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Predicate;
import me.drex.vanish.VanishMod;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1297;
import net.minecraft.class_1924;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class VanishEntitySelector {
   @Mixin({class_1924.class})
   public interface EntityGetterMixin {
      @WrapOperation(
         method = {"method_20743"},
         at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_1301;field_6155:Ljava/util/function/Predicate;"
)}
      )
      default Predicate<class_1297> preventEntityCollisions(Operation<Predicate<class_1297>> original) {
         return ConfigManager.vanish().interaction.entityCollisions ? VanishMod.NO_SPECTATORS_AND_NO_VANISH : (Predicate)original.call(new Object[0]);
      }

      @WrapOperation(
         method = {"method_20743"},
         at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_1301;field_35589:Ljava/util/function/Predicate;"
)}
      )
      default Predicate<class_1297> preventEntityCollisions2(Operation<Predicate<class_1297>> original) {
         Predicate<class_1297> result = (Predicate)original.call(new Object[0]);
         if (ConfigManager.vanish().interaction.entityCollisions) {
            result = result.and(VanishMod.NO_VANISH);
         }

         return result;
      }
   }
}
