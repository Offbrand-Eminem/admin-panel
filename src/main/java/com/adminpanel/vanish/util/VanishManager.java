package me.drex.vanish.util;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.JsonDataStorage;
import eu.pb4.playerdata.api.storage.PlayerDataStorage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.api.VanishEvents;
import me.drex.vanish.config.ConfigManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.class_11560;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2703;
import net.minecraft.class_3222;
import net.minecraft.class_3324;
import net.minecraft.class_7828;
import net.minecraft.server.MinecraftServer;

public class VanishManager {
   public static final PlayerDataStorage<VanishData> VANISH_DATA_STORAGE = new JsonDataStorage("vanish", VanishData.class);
   private static final Map<class_3222, Boolean> CAN_VIEW_VANISHED_CACHE = new HashMap();

   public static void init() {
      ServerTickEvents.START_SERVER_TICK.register((ServerTickEvents.StartTick)(server) -> {
         if (ConfigManager.vanish().actionBar) {
            for(class_3222 player : server.method_3760().method_14571()) {
               if (isVanished(player)) {
                  player.method_43502(class_2561.method_43471("text.vanish.general.vanished"), true);
               }
            }
         }

         CAN_VIEW_VANISHED_CACHE.clear();
      });
      ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((ServerMessageEvents.AllowChatMessage)(message, sender, params) -> {
         if (isVanished(sender) && ConfigManager.vanish().disableChat) {
            sender.method_64398(class_2561.method_43471("text.vanish.chat.disabled").method_27692(class_124.field_1061));
            return false;
         } else {
            return true;
         }
      });
      ServerMessageEvents.ALLOW_COMMAND_MESSAGE.register((ServerMessageEvents.AllowCommandMessage)(message, source, params) -> {
         class_3222 sender = source.method_44023();
         if (sender != null) {
            if (isVanished(sender) && ConfigManager.vanish().disableChat) {
               sender.method_64398(class_2561.method_43471("text.vanish.chat.disabled").method_27692(class_124.field_1061));
               return false;
            } else {
               return true;
            }
         } else {
            return true;
         }
      });
      PlayerDataApi.register(VANISH_DATA_STORAGE);
   }

   public static boolean isVanished(class_1297 entity) {
      if (entity instanceof VanishedEntity vanishedEntity) {
         return vanishedEntity.vanish$isVanished();
      } else {
         return false;
      }
   }

   public static boolean isVanished(MinecraftServer server, UUID uuid) {
      VanishData data = (VanishData)PlayerDataApi.getCustomDataFor(server, uuid, VANISH_DATA_STORAGE);
      return data != null && data.vanished;
   }

   public static boolean canViewVanished(class_3222 observer) {
      return (Boolean)CAN_VIEW_VANISHED_CACHE.computeIfAbsent(observer, (k) -> canViewVanished((class_2172)observer.method_64396()));
   }

   public static boolean canViewVanished(class_2172 observer) {
      return Permissions.check(observer, "vanish.feature.view", 2);
   }

   public static boolean canSeePlayer(class_3222 actor, class_3222 observer) {
      return canSeePlayer(actor.method_51469().method_8503(), actor.method_5667(), observer);
   }

   public static boolean canSeePlayer(MinecraftServer server, UUID actor, class_3222 observer) {
      if (isVanished(server, actor)) {
         return actor.equals(observer.method_5667()) ? true : canViewVanished(observer);
      } else {
         return true;
      }
   }

   public static boolean canSeePlayer(class_3222 actor, class_2168 observer) {
      if (isVanished(actor)) {
         return observer.method_9228() != null && actor.equals(observer.method_9228()) ? true : canViewVanished((class_2172)observer);
      } else {
         return true;
      }
   }

   public static boolean canSeePlayer(MinecraftServer server, UUID actor, class_2168 observer) {
      if (isVanished(server, actor)) {
         return observer.method_9228() != null && actor.equals(observer.method_9228().method_5667()) ? true : canViewVanished((class_2172)observer);
      } else {
         return true;
      }
   }

   public static boolean setVanished(class_11560 profile, MinecraftServer server, boolean vanish) {
      UUID uuid = profile.comp_4422();
      if (isVanished(server, uuid) == vanish) {
         return false;
      } else {
         class_3222 player = server.method_3760().method_14602(uuid);
         boolean isOnline = player != null;
         if (vanish && isOnline) {
            vanish(player);
         }

         VanishData data = (VanishData)PlayerDataApi.getCustomDataFor(server, uuid, VANISH_DATA_STORAGE);
         if (data == null) {
            data = new VanishData();
         }

         data.vanished = vanish;
         PlayerDataApi.setCustomDataFor(server, uuid, VANISH_DATA_STORAGE, data);
         if (isOnline) {
            ((VanishedEntity)player).vanish$setDirty();
         }

         if (!vanish && isOnline) {
            unVanish(player);
         }

         if (isOnline) {
            server.method_3856();
            ((VanishEvents.VanishEvent)VanishEvents.VANISH_EVENT.invoker()).onVanish(player, vanish);
         }

         return true;
      }
   }

   private static void unVanish(class_3222 actor) {
      class_3324 list = actor.method_51469().method_8503().method_3760();
      broadcastToOthers(actor, class_2703.method_43886(Collections.singletonList(actor)));
      if (ConfigManager.vanish().sendJoinDisconnectMessage) {
         list.method_43514(((VanishEvents.UnVanishMessageEvent)VanishEvents.UN_VANISH_MESSAGE_EVENT.invoker()).getUnVanishMessage(actor), false);
      }

   }

   private static void vanish(class_3222 actor) {
      class_3324 list = actor.method_51469().method_8503().method_3760();
      broadcastToOthers(actor, new class_7828(Collections.singletonList(actor.method_5667())));
      if (ConfigManager.vanish().sendJoinDisconnectMessage) {
         list.method_43514(((VanishEvents.VanishMessageEvent)VanishEvents.VANISH_MESSAGE_EVENT.invoker()).getVanishMessage(actor), false);
      }

   }

   private static void broadcastToOthers(class_3222 actor, class_2596<?> packet) {
      for(class_3222 observer : actor.method_51469().method_8503().method_3760().method_14571()) {
         if (!VanishAPI.canViewVanished(observer) && !observer.equals(actor)) {
            observer.field_13987.method_14364(packet);
         }
      }

   }
}
