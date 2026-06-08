package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_1657;
import net.minecraft.class_3222;
import net.minecraft.class_3988;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_3988.class})
public class AbstractVillagerMixin {
   @Shadow
   private @Nullable class_1657 field_17722;

   @ModifyReturnValue(
      method = {"method_18009"},
      at = {@At("RETURN")}
   )
   private boolean allowTradingIfVanished(boolean original) {
      if (original) {
         assert this.field_17722 != null;

         return !VanishAPI.isVanished(this.field_17722);
      } else {
         return false;
      }
   }

   @Inject(
      method = {"method_8259"},
      at = {@At("HEAD")}
   )
   private void closeVanishedPlayerTradeScreen(class_1657 player, CallbackInfo ci) {
      if (player != null && player != this.field_17722) {
         class_1657 var4 = this.field_17722;
         if (var4 instanceof class_3222) {
            class_3222 serverPlayer = (class_3222)var4;
            serverPlayer.method_7346();
         }
      }

   }
}
