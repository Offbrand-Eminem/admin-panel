package me.drex.vanish.mixin;

import me.drex.vanish.api.VanishAPI;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_3222;
import net.minecraft.class_3414;
import net.minecraft.class_8046;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1297.class})
public abstract class EntityMixin {
   @Inject(
      method = {"method_5680"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void shouldBroadcast(class_3222 observer, CallbackInfoReturnable<Boolean> cir) {
      class_1297 self = (class_1297)this;
      class_3222 actor;
      if (self instanceof class_3222 player) {
         actor = player;
      } else {
         if (!(self instanceof class_8046)) {
            return;
         }

         class_8046 traceableEntity = (class_8046)self;
         class_1297 var8 = traceableEntity.method_24921();
         if (!(var8 instanceof class_3222)) {
            return;
         }

         class_3222 owner = (class_3222)var8;
         actor = owner;
      }

      if (!VanishAPI.canSeePlayer(actor, observer)) {
         cir.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_5756"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void markRenderInvisible(class_1657 player, CallbackInfoReturnable<Boolean> cir) {
      class_1297 self = (class_1297)this;
      if (self instanceof class_3222 actor) {
         if (player instanceof class_3222 observer) {
            if (!VanishAPI.canSeePlayer(actor, observer)) {
               cir.setReturnValue(true);
            }
         }
      }

   }

   @Inject(
      method = {"method_5783"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void preventSound(class_3414 soundEvent, float f, float g, CallbackInfo ci) {
      class_1297 self = (class_1297)this;
      class_3222 actor;
      if (self instanceof class_3222 player) {
         actor = player;
      } else {
         if (!(self instanceof class_8046)) {
            return;
         }

         class_8046 traceableEntity = (class_8046)self;
         class_1297 var10 = traceableEntity.method_24921();
         if (!(var10 instanceof class_3222)) {
            return;
         }

         class_3222 owner = (class_3222)var10;
         actor = owner;
      }

      if (VanishAPI.isVanished(actor)) {
         ci.cancel();
      }

   }
}
