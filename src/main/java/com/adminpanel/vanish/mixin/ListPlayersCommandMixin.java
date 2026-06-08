package me.drex.vanish.mixin;

import java.util.List;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_2168;
import net.minecraft.class_3078;
import net.minecraft.class_3222;
import net.minecraft.class_3324;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_3078.class})
public abstract class ListPlayersCommandMixin {
   @Redirect(
      method = {"method_13434"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3324;method_14571()Ljava/util/List;"
)
   )
   private static List<class_3222> removeVanishedPlayers(class_3324 playerList, class_2168 observer) {
      return VanishAPI.getVisiblePlayers(observer);
   }
}
