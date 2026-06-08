package me.drex.vanish.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.class_2561;
import net.minecraft.class_3222;

public class VanishEvents {
   public static final Event<JoinEvent> JOIN_EVENT = EventFactory.createArrayBacked(JoinEvent.class, (callbacks) -> (player) -> {
         for(JoinEvent callback : callbacks) {
            TriState result = callback.onJoin(player);
            if (result != TriState.DEFAULT) {
               return result;
            }
         }

         return TriState.DEFAULT;
      });
   public static final Event<VanishEvent> VANISH_EVENT = EventFactory.createArrayBacked(VanishEvent.class, (callbacks) -> (player, vanish) -> {
         for(VanishEvent callback : callbacks) {
            callback.onVanish(player, vanish);
         }

      });
   public static final Event<VanishMessageEvent> VANISH_MESSAGE_EVENT = EventFactory.createArrayBacked(VanishMessageEvent.class, (callbacks) -> (player) -> {
         class_2561 result = class_2561.method_43473();

         for(VanishMessageEvent callback : callbacks) {
            result = callback.getVanishMessage(player);
         }

         return result;
      });
   public static final Event<UnVanishMessageEvent> UN_VANISH_MESSAGE_EVENT = EventFactory.createArrayBacked(UnVanishMessageEvent.class, (callbacks) -> (player) -> {
         class_2561 result = class_2561.method_43473();

         for(UnVanishMessageEvent callback : callbacks) {
            result = callback.getUnVanishMessage(player);
         }

         return result;
      });

   public interface JoinEvent {
      TriState onJoin(class_3222 var1);
   }

   public interface UnVanishMessageEvent {
      class_2561 getUnVanishMessage(class_3222 var1);
   }

   public interface VanishEvent {
      void onVanish(class_3222 var1, boolean var2);
   }

   public interface VanishMessageEvent {
      class_2561 getVanishMessage(class_3222 var1);
   }
}
