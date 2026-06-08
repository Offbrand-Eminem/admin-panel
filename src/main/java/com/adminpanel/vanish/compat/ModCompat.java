package me.drex.vanish.compat;

import de.bluecolored.bluemap.api.BlueMapAPI;
import eu.pb4.styledchat.StyledChatStyles;
import me.drex.vanish.api.VanishEvents;
import me.lucko.fabric.api.permissions.v0.Options;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import xyz.jpenilla.squaremap.api.SquaremapProvider;

public class ModCompat {
   public static final boolean STYLED_CHAT = FabricLoader.getInstance().isModLoaded("styledchat");
   public static final boolean BLUEMAP = FabricLoader.getInstance().isModLoaded("bluemap");
   public static final boolean DYNMAP = FabricLoader.getInstance().isModLoaded("dynmap");
   public static final boolean SQUAREMAP = FabricLoader.getInstance().isModLoaded("squaremap");
   public static final boolean PL3XMAP = FabricLoader.getInstance().isModLoaded("pl3xmap");
   public static final class_2960 VANISH_ON_JOIN = class_2960.method_60655("vanish", "vanish_on_join");
   public static boolean blueMapEventsRegistered = false;

   public static void init() {
      VanishEvents.VANISH_MESSAGE_EVENT.register((VanishEvents.VanishMessageEvent)(serverPlayer) -> class_2561.method_43469("multiplayer.player.left", new Object[]{serverPlayer.method_5476()}).method_27692(class_124.field_1054));
      VanishEvents.UN_VANISH_MESSAGE_EVENT.register((VanishEvents.UnVanishMessageEvent)(serverPlayer) -> class_2561.method_43469("multiplayer.player.joined", new Object[]{serverPlayer.method_5476()}).method_27692(class_124.field_1054));
      VanishEvents.JOIN_EVENT.addPhaseOrdering(Event.DEFAULT_PHASE, VANISH_ON_JOIN);
      VanishEvents.JOIN_EVENT.register(VANISH_ON_JOIN, (VanishEvents.JoinEvent)(player) -> (TriState)Options.get(player, "vanish_on_join", Boolean::valueOf).map(TriState::of).orElse(TriState.DEFAULT));
      if (STYLED_CHAT) {
         VanishEvents.UN_VANISH_MESSAGE_EVENT.register(StyledChatStyles::getJoin);
         VanishEvents.VANISH_MESSAGE_EVENT.register(StyledChatStyles::getLeft);
      }

      if (BLUEMAP) {
         BlueMapAPI.onEnable((blueMapAPI) -> {
            if (!blueMapEventsRegistered) {
               VanishEvents.VANISH_EVENT.register((VanishEvents.VanishEvent)(player, vanish) -> BlueMapAPI.getInstance().ifPresent((api) -> api.getWebApp().setPlayerVisibility(player.method_5667(), !vanish)));
               blueMapEventsRegistered = true;
            }

         });
      }

      if (DYNMAP) {
         DynmapCompat.init();
      }

      if (PL3XMAP) {
         Pl3xmapCompat.init();
      }

      if (SQUAREMAP) {
         VanishEvents.VANISH_EVENT.register((VanishEvents.VanishEvent)(player, vanish) -> {
            if (vanish) {
               SquaremapProvider.get().playerManager().hide(player.method_5667(), true);
            } else {
               SquaremapProvider.get().playerManager().show(player.method_5667(), true);
            }

         });
      }

   }
}
