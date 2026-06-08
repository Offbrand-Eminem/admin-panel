package me.drex.vanish.mixin;

import com.mojang.authlib.GameProfile;
import eu.pb4.playerdata.api.PlayerDataApi;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.util.VanishData;
import me.drex.vanish.util.VanishManager;
import me.drex.vanish.util.VanishedEntity;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_3222.class})
public abstract class VanishedServerPlayerMixin extends class_1657 implements VanishedEntity {
   @Shadow
   @Final
   public MinecraftServer field_13995;
   @Unique
   private boolean vanished$dirty = true;
   @Unique
   private boolean vanished$vanished;

   public VanishedServerPlayerMixin(class_1937 level, GameProfile gameProfile) {
      super(level, gameProfile);
   }

   public boolean vanish$isVanished() {
      if (this.vanished$dirty) {
         VanishData data = (VanishData)PlayerDataApi.getCustomDataFor(this.field_13995, this.field_6021, VanishManager.VANISH_DATA_STORAGE);
         this.vanished$vanished = data != null && data.vanished;
         this.vanished$dirty = false;
      }

      return this.vanished$vanished;
   }

   public void vanish$setDirty() {
      this.vanished$dirty = true;
   }

   @Inject(
      method = {"method_5680"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void shouldBroadcast(class_3222 observer, CallbackInfoReturnable<Boolean> cir) {
      if (!VanishAPI.canSeePlayer((class_3222)this, observer)) {
         cir.setReturnValue(false);
      }

   }
}
