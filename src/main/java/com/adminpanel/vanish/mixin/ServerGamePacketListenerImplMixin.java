package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.util.Arguments;
import net.minecraft.class_1297;
import net.minecraft.class_2561;
import net.minecraft.class_2824;
import net.minecraft.class_2846;
import net.minecraft.class_2885;
import net.minecraft.class_2886;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.class_3324;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_3244.class})
public abstract class ServerGamePacketListenerImplMixin {
   @Shadow
   public class_3222 field_14140;

   @WrapOperation(
      method = {"method_52415"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3324;method_43514(Lnet/minecraft/class_2561;Z)V"
)}
   )
   public void hideLeaveMessage(class_3324 playerList, class_2561 component, boolean bl, Operation<Void> original) {
      if (VanishAPI.isVanished(this.field_14140)) {
         VanishAPI.broadcastHiddenMessage(this.field_14140, component);
      } else {
         original.call(new Object[]{playerList, component, bl});
      }

   }

   @WrapOperation(
      method = {"method_12062"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2824;method_12248(Lnet/minecraft/class_3218;)Lnet/minecraft/class_1297;"
)}
   )
   public class_1297 preventInteraction(class_2824 instance, class_3218 serverLevel, Operation<class_1297> original) {
      class_1297 entity = (class_1297)original.call(new Object[]{instance, serverLevel});
      if (entity instanceof class_3222 actor) {
         if (!VanishAPI.canSeePlayer(actor, this.field_14140)) {
            return null;
         }
      }

      return entity;
   }

   @Inject(
      method = {"method_12066"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_2846;method_12362()Lnet/minecraft/class_2338;"
)}
   )
   public void beforeHandlePlayerAction(class_2846 serverboundPlayerActionPacket, CallbackInfo ci) {
      Arguments.ACTIVE_ENTITY.set(this.field_14140);
   }

   @Inject(
      method = {"method_12046"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3244;method_41255(I)V"
)}
   )
   public void beforeHandleUseItemOn(class_2885 serverboundUseItemOnPacket, CallbackInfo ci) {
      Arguments.ACTIVE_ENTITY.set(this.field_14140);
   }

   @Inject(
      method = {"method_12065"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3244;method_41255(I)V"
)}
   )
   public void beforeHandleUseItem(class_2886 serverboundUseItemPacket, CallbackInfo ci) {
      Arguments.ACTIVE_ENTITY.set(this.field_14140);
   }

   @Inject(
      method = {"method_12062"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3222;method_14234()V"
)}
   )
   public void beforeHandleInteract(class_2824 serverboundInteractPacket, CallbackInfo ci) {
      Arguments.ACTIVE_ENTITY.set(this.field_14140);
   }

   @Inject(
      method = {"method_12066", "method_12046", "method_12065", "method_12062"},
      at = {@At("RETURN")}
   )
   public void afterPacket(CallbackInfo ci) {
      Arguments.ACTIVE_ENTITY.remove();
   }

   @Inject(
      method = {"method_18784"},
      at = {@At("HEAD")}
   )
   public void beforeTick(CallbackInfo ci) {
      Arguments.ACTIVE_ENTITY.set(this.field_14140);
   }

   @Inject(
      method = {"method_18784"},
      at = {@At("RETURN")}
   )
   public void afterTick(CallbackInfo ci) {
      Arguments.ACTIVE_ENTITY.remove();
   }
}
