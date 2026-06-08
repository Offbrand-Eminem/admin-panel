package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import java.util.List;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_12096;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({MinecraftServer.class})
public abstract class MinecraftServerMixin {
   @Shadow
   public abstract class_2168 method_3739();

   @ModifyReceiver(
      method = {"method_49386"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/util/List;size()I"
)}
   )
   public List<class_3222> getNonVanishedPlayerCount(List<class_3222> original) {
      return VanishAPI.getVisiblePlayers(this.method_3739().method_9206(class_12096.field_63207));
   }

   @ModifyReceiver(
      method = {"method_49386"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/util/List;get(I)Ljava/lang/Object;"
)}
   )
   public List<class_3222> getNonVanishedPlayer(List<class_3222> original, int index) {
      return VanishAPI.getVisiblePlayers(this.method_3739().method_9206(class_12096.field_63207));
   }

   @Overwrite
   public int method_3788() {
      return VanishAPI.getVisiblePlayers(this.method_3739().method_9206(class_12096.field_63207)).size();
   }
}
