package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.util.Arguments;
import net.minecraft.class_1297;
import net.minecraft.class_2596;
import net.minecraft.class_2620;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_3218.class})
public abstract class ServerLevelMixin {
   @Shadow
   public abstract @Nullable class_1297 method_8469(int var1);

   @WrapOperation(
      method = {"method_8517"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3244;method_14364(Lnet/minecraft/class_2596;)V"
)}
   )
   public void hideBlockDestroyProgress(class_3244 packetListener, class_2596<?> packet, Operation<Void> original) {
      class_1297 entity = this.method_8469(((class_2620)packet).method_11280());
      if (entity instanceof class_3222 player) {
         if (!VanishAPI.canSeePlayer(player, packetListener.field_14140)) {
            return;
         }
      }

      original.call(new Object[]{packetListener, packet});
   }

   @Inject(
      method = {"method_18762"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_5773()V"
)}
   )
   public void beforeEntityTickNonPassenger(class_1297 entity, CallbackInfo ci) {
      Arguments.ACTIVE_ENTITY.set(entity);
   }

   @Inject(
      method = {"method_18762"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_5773()V",
   shift = Shift.AFTER
)}
   )
   public void afterEntityTickNonPassenger(class_1297 entity, CallbackInfo ci) {
      Arguments.ACTIVE_ENTITY.remove();
   }

   @Inject(
      method = {"method_18763"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_5842()V"
)}
   )
   public void beforeEntityTick(class_1297 entity, class_1297 entity2, CallbackInfo ci) {
      Arguments.ACTIVE_ENTITY.set(entity2);
   }

   @Inject(
      method = {"method_18763"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1297;method_5842()V",
   shift = Shift.AFTER
)}
   )
   public void afterEntityTick(class_1297 entity, class_1297 entity2, CallbackInfo ci) {
      Arguments.ACTIVE_ENTITY.remove();
   }
}
