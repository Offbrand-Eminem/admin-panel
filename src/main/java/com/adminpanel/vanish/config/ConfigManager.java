package me.drex.vanish.config;

import java.nio.file.Path;
import me.drex.vanish.VanishMod;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

public class ConfigManager {
   private static final Logger LOGGER;
   private static final Path CONFIG_FILE;
   private static VanishConfig vanishConfig;

   private ConfigManager() {
   }

   public static void load() throws Exception {
      LOGGER.info("Loading vanish configuration...");
      HoconConfigurationLoader loader = ((HoconConfigurationLoader.Builder)HoconConfigurationLoader.builder().path(CONFIG_FILE)).build();
      CommentedConfigurationNode rootNode = (CommentedConfigurationNode)loader.load();
      if (!CONFIG_FILE.toFile().exists()) {
         LOGGER.info("Creating vanish configuration file!");
         rootNode.set(VanishConfig.class, vanishConfig);
      } else {
         vanishConfig = (VanishConfig)rootNode.get(VanishConfig.class, vanishConfig);
      }

      loader.save(rootNode);
   }

   public static VanishConfig vanish() {
      return vanishConfig;
   }

   static {
      LOGGER = VanishMod.LOGGER;
      CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("vanish.hocon");
      vanishConfig = new VanishConfig();
   }
}
