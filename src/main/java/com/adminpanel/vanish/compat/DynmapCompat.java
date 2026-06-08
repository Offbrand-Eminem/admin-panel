package me.drex.vanish.compat;

import me.drex.vanish.api.VanishEvents;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;

public class DynmapCompat {
   public static void init() {
      DynmapCommonAPIListener.register(new DynmapCommonAPIListener() {
         public void apiEnabled(DynmapCommonAPI dynmapCommonAPI) {
            VanishEvents.VANISH_EVENT.register((VanishEvents.VanishEvent)(player, vanish) -> {
               dynmapCommonAPI.setPlayerVisiblity(player.method_5820(), !vanish);
               dynmapCommonAPI.postPlayerJoinQuitToWeb(player.method_5820(), player.method_5476().getString(), !vanish);
            });
         }
      });
   }
}
