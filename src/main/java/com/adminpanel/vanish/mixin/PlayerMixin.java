package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_1657;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_1657.class})
public abstract class PlayerMixin {
   @ModifyReturnValue(
      method = {"method_5679"},
      at = {@At("RETURN")}
   )
   private boolean invulnerablePlayers(boolean original) {
      return ConfigManager.vanish().invulnerable && VanishAPI.isVanished((class_1657)this) ? true : original;
   }
}
