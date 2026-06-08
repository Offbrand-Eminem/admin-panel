package me.drex.vanish.mixin;

import io.netty.channel.ChannelFutureListener;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import me.drex.vanish.util.Arguments;
import net.minecraft.class_1297;
import net.minecraft.class_1934;
import net.minecraft.class_2596;
import net.minecraft.class_2703;
import net.minecraft.class_2716;
import net.minecraft.class_2775;
import net.minecraft.class_3222;
import net.minecraft.class_3244;
import net.minecraft.class_8609;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_8609.class})
public abstract class ServerCommonPacketListenerImplMixin {
   @Shadow
   @Final
   protected MinecraftServer field_45012;

   @Shadow
   public abstract void method_14364(class_2596<?> var1);

   @Inject(
      method = {"method_52391"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void modifyPackets(class_2596<?> packet, ChannelFutureListener channelFutureListener, CallbackInfo ci) {
      if (this instanceof class_3244 listener) {
         if (packet instanceof class_2775 takeItemEntityPacket) {
            class_1297 entity = listener.field_14140.method_51469().method_8469(takeItemEntityPacket.method_11912());
            if (entity instanceof class_3222 actor) {
               if (!VanishAPI.canSeePlayer(actor, listener.field_14140)) {
                  this.method_14364(new class_2716(new int[]{takeItemEntityPacket.method_11915()}));
                  ci.cancel();
               }
            }
         } else if (packet instanceof class_2703 playerInfoPacket) {
            if (Arguments.PACKET_CONTEXT.get() != null) {
               return;
            }

            boolean hideGameMode = ConfigManager.vanish().hideGameMode;
            boolean canViewVanished = hideGameMode && VanishAPI.canViewVanished(listener.field_14140);
            ObjectArrayList<class_2703.class_2705> modifiedEntries = new ObjectArrayList(playerInfoPacket.method_46329().size());
            boolean changed = false;

            for(class_2703.class_2705 playerUpdate : playerInfoPacket.method_46329()) {
               class_3222 player = this.field_45012.method_3760().method_14602(playerUpdate.comp_1106());
               if (player != null && !VanishAPI.canSeePlayer(player, listener.field_14140)) {
                  changed = true;
               } else {
                  class_2703.class_2705 adjustedEntry = playerUpdate;
                  if (hideGameMode && !canViewVanished && player != null && player != listener.field_14140 && playerUpdate.comp_1110() != class_1934.field_28045) {
                     adjustedEntry = new class_2703.class_2705(playerUpdate.comp_1106(), playerUpdate.comp_1107(), playerUpdate.comp_1108(), playerUpdate.comp_1109(), class_1934.field_28045, playerUpdate.comp_1111(), playerUpdate.comp_3324(), playerUpdate.comp_2889(), playerUpdate.comp_1112());
                     changed = true;
                  }

                  modifiedEntries.add(adjustedEntry);
               }
            }

            if (!changed) {
               return;
            }

            if (!modifiedEntries.isEmpty()) {
               class_3222 prev = (class_3222)Arguments.PACKET_CONTEXT.get();

               try {
                  Arguments.PACKET_CONTEXT.set(listener.field_14140);
                  class_2703 modifiedPacket = new class_2703(playerInfoPacket.method_46327(), List.of());
                  ((ClientboundPlayerInfoUpdatePacketAccessor)modifiedPacket).setEntries(modifiedEntries);
                  this.method_14364(modifiedPacket);
               } finally {
                  Arguments.PACKET_CONTEXT.set(prev);
               }
            }

            ci.cancel();
         }
      }

   }
}
