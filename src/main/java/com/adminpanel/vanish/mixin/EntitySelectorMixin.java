package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.LinkedList;
import java.util.List;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_2168;
import net.minecraft.class_2300;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_2300.class})
public abstract class EntitySelectorMixin {
   @ModifyReturnValue(
      method = {"method_9813"},
      at = {@At("RETURN")}
   )
   public List<class_3222> removeVanishedPlayers(List<class_3222> original, class_2168 src) {
      List<class_3222> players = new LinkedList(original);
      class_3222 observer = src.method_44023();
      if (observer != null) {
         players.removeIf((actor) -> !VanishAPI.canSeePlayer(actor, observer));
      }

      return players;
   }
}
