package me.drex.vanish.mixin.interaction;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.function.Predicate;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_3222;
import net.minecraft.class_4558;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({class_4558.class})
public abstract class SimpleCriterionTriggerMixin<T extends class_4558.class_8788> {
   @WrapMethod(
      method = {"method_22510"}
   )
   public void preventAdvancementProgress(class_3222 player, Predicate<T> predicate, Operation<Void> original) {
      if (!ConfigManager.vanish().interaction.advancementProgress || !VanishAPI.isVanished(player)) {
         if (!ConfigManager.vanish().interaction.spectatorAdvancementProgress || !player.method_7325()) {
            original.call(new Object[]{player, predicate});
         }
      }
   }
}
