package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eu.pb4.playerdata.api.PlayerDataApi;
import java.util.List;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.api.VanishEvents;
import me.drex.vanish.util.Arguments;
import me.drex.vanish.util.VanishData;
import me.drex.vanish.util.VanishManager;
import me.drex.vanish.util.VanishedEntity;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.class_12096;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2535;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.class_3324;
import net.minecraft.class_8046;
import net.minecraft.class_8792;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_3324.class})
public abstract class PlayerListMixin {
   @Inject(
      method = {"method_14570"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3324;method_14576(Lnet/minecraft/class_3222;)V"
)}
   )
   private void vanishOnJoin(class_2535 connection, class_3222 actor, class_8792 commonListenerCookie, CallbackInfo ci) {
      TriState result = ((VanishEvents.JoinEvent)VanishEvents.JOIN_EVENT.invoker()).onJoin(actor);
      if (result != TriState.DEFAULT) {
         VanishData data = (VanishData)PlayerDataApi.getCustomDataFor(actor.method_51469().method_8503(), actor.method_5667(), VanishManager.VANISH_DATA_STORAGE);
         if (data == null) {
            data = new VanishData();
         }

         data.vanished = result.get();
         PlayerDataApi.setCustomDataFor(actor.method_51469().method_8503(), actor.method_5667(), VanishManager.VANISH_DATA_STORAGE, data);
         ((VanishedEntity)actor).vanish$setDirty();
      }

   }

   @WrapOperation(
      method = {"method_14570"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3324;method_43514(Lnet/minecraft/class_2561;Z)V"
)}
   )
   public void hideJoinMessage(class_3324 playerList, class_2561 component, boolean bl, Operation<Void> original, class_2535 connection, class_3222 actor) {
      if (VanishAPI.isVanished(actor)) {
         VanishAPI.broadcastHiddenMessage(actor, component);
      } else {
         original.call(new Object[]{playerList, component, bl});
      }

   }

   @WrapWithCondition(
      method = {"method_14605"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3244;method_14364(Lnet/minecraft/class_2596;)V"
)}
   )
   public boolean hideGameEvents(class_3244 packetListener, class_2596<?> packet, class_1657 player) {
      class_1297 entity;
      if (player instanceof class_3222 serverPlayer) {
         entity = serverPlayer;
      } else {
         entity = (class_1297)Arguments.ACTIVE_ENTITY.get();
      }

      if (entity instanceof class_8046 traceableEntity) {
         class_1297 var7 = traceableEntity.method_24921();
         if (var7 instanceof class_3222 owner) {
            entity = owner;
         }
      }

      if (entity instanceof class_3222 actor) {
         return VanishAPI.canSeePlayer(actor, packetListener.field_14140);
      } else {
         return true;
      }
   }

   @Redirect(
      method = {"method_14586"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/class_3324;field_14351:Ljava/util/List;"
)
   )
   private List<class_3222> getNonVanishedPlayerCount(class_3324 playerList) {
      return VanishAPI.getVisiblePlayers(playerList.method_14561().method_3739().method_9206(class_12096.field_63207));
   }
}
