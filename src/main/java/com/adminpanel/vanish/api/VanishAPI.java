package me.drex.vanish.api;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.UUID;
import me.drex.vanish.util.VanishManager;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import net.minecraft.class_5250;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public interface VanishAPI {
   static boolean isVanished(@NotNull class_1297 entity) {
      return VanishManager.isVanished(entity);
   }

   static boolean isVanished(@NotNull MinecraftServer server, @NotNull UUID uuid) {
      return VanishManager.isVanished(server, uuid);
   }

   static boolean setVanish(@NotNull class_3222 player, boolean status) {
      return VanishManager.setVanished(player.method_72498(), player.method_51469().method_8503(), status);
   }

   static boolean canSeePlayer(@NotNull class_3222 actor, @NotNull class_3222 observer) {
      return VanishManager.canSeePlayer(actor, observer);
   }

   static boolean canSeePlayer(@NotNull MinecraftServer server, @NotNull UUID uuid, @NotNull class_3222 observer) {
      return VanishManager.canSeePlayer(server, uuid, observer);
   }

   static boolean canSeePlayer(@NotNull MinecraftServer server, @NotNull UUID uuid, @NotNull class_2168 observer) {
      return VanishManager.canSeePlayer(server, uuid, observer);
   }

   static boolean canViewVanished(class_3222 observer) {
      return VanishManager.canViewVanished(observer);
   }

   static boolean canViewVanished(class_2172 observer) {
      return VanishManager.canViewVanished(observer);
   }

   static @NotNull List<class_3222> getVisiblePlayers(@NotNull class_3222 observer) {
      MinecraftServer server = observer.method_51469().method_8503();
      ObjectArrayList<class_3222> list = new ObjectArrayList();

      for(class_3222 player : server.method_3760().method_14571()) {
         if (VanishManager.canSeePlayer(player, observer)) {
            list.add(player);
         }
      }

      return list;
   }

   static @NotNull List<class_3222> getVisiblePlayers(@NotNull class_2168 observer) {
      MinecraftServer server = observer.method_9211();
      ObjectArrayList<class_3222> list = new ObjectArrayList();

      for(class_3222 player : server.method_3760().method_14571()) {
         if (VanishManager.canSeePlayer(player, observer)) {
            list.add(player);
         }
      }

      return list;
   }

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   static @NotNull List<class_3222> getViewingPlayers(@NotNull class_3222 actor) {
      ObjectArrayList<class_3222> list = new ObjectArrayList();

      for(class_3222 observer : actor.method_51469().method_8503().method_3760().method_14571()) {
         if (canSeePlayer(actor, observer)) {
            list.add(observer);
         }
      }

      return list;
   }

   static void broadcastHiddenMessage(@NotNull class_3222 actor, @NotNull class_2561 message) {
      class_5250 component = message.method_27661();
      component.method_10852(class_2561.method_43471("text.vanish.chat.hidden").method_27695(new class_124[]{class_124.field_1080, class_124.field_1056}));

      for(class_3222 observer : actor.method_51469().method_8503().method_3760().method_14571()) {
         if (canSeePlayer(actor, observer)) {
            observer.method_64398(component);
         }
      }

   }

   static void sendHiddenMessage(@NotNull class_3222 actor, @NotNull class_3222 observer, @NotNull class_2561 message) {
      class_5250 component = message.method_27661();
      component.method_10852(class_2561.method_43471("text.vanish.chat.hidden").method_27695(new class_124[]{class_124.field_1080, class_124.field_1056}));
      if (canSeePlayer(actor, observer)) {
         observer.method_64398(component);
      }

   }
}
