package me.drex.vanish.util;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.parsers.NodeParser;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import net.minecraft.class_12096;
import net.minecraft.class_2561;
import net.minecraft.class_2960;

public class VanishPlaceHolders {
   public static final class_2960 VANISHED = class_2960.method_60655("vanish", "vanished");
   public static final class_2960 ONLINE = class_2960.method_60655("vanish", "online");
   private static final NodeParser PARSER = NodeParser.builder().simplifiedTextFormat().quickText().build();

   public static void register() {
      Placeholders.register(VANISHED, (context, argument) -> {
         if (context.player() != null) {
            return VanishAPI.isVanished(context.player()) ? PlaceholderResult.value(PARSER.parseText(ConfigManager.vanish().placeHolderDisplay, context.asParserContext())) : PlaceholderResult.value(class_2561.method_43473());
         } else {
            return PlaceholderResult.invalid("No player!");
         }
      });
      Placeholders.register(ONLINE, (context, argument) -> context.hasPlayer() ? PlaceholderResult.value(String.valueOf(VanishAPI.getVisiblePlayers(context.player()).size())) : PlaceholderResult.value(String.valueOf(VanishAPI.getVisiblePlayers(context.server().method_3739().method_9206(class_12096.field_63207)).size())));
   }
}
