package me.drex.vanish.mixin;

import java.util.Collection;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_124;
import net.minecraft.class_2168;
import net.minecraft.class_2561;
import net.minecraft.class_3082;
import net.minecraft.class_3222;
import net.minecraft.class_7471;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_3082.class})
public class MsgCommandMixin {
   @Inject(
      method = {"method_13462"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void stopMessage(class_2168 commandSourceStack, Collection<class_3222> collection, class_7471 playerChatMessage, CallbackInfo ci) {
      class_3222 player = commandSourceStack.method_44023();
      if (player != null) {
         if (ConfigManager.vanish().disableMsg && VanishAPI.isVanished(player)) {
            player.method_64398(class_2561.method_43471("text.vanish.chat.disabled").method_27692(class_124.field_1061));
            ci.cancel();
         }

      }
   }
}
