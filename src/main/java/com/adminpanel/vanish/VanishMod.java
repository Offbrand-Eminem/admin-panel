package me.drex.vanish;

import java.util.function.Predicate;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.command.VanishCommand;
import me.drex.vanish.compat.ModCompat;
import me.drex.vanish.config.ConfigManager;
import me.drex.vanish.util.VanishManager;
import me.drex.vanish.util.VanishPlaceHolders;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.class_1297;
import net.minecraft.class_1301;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VanishMod implements ModInitializer {
   public static final String MOD_ID = "vanish";
   public static final Logger LOGGER = LoggerFactory.getLogger("vanish");
   public static final Predicate<class_1297> NO_VANISH = (entity) -> !VanishAPI.isVanished(entity);
   public static final Predicate<class_1297> NO_SPECTATORS_AND_NO_VANISH;

   public void onInitialize() {
      try {
         ConfigManager.load();
      } catch (Exception e) {
         LOGGER.error("An error occurred while loading the config, keeping default values", e);
      }

      CommandRegistrationCallback.EVENT.register(VanishCommand::register);
      VanishManager.init();
      VanishPlaceHolders.register();
      ModCompat.init();
   }

   static {
      NO_SPECTATORS_AND_NO_VANISH = class_1301.field_6155.and(NO_VANISH);
   }
}
