package me.declipsonator.chatcontrol;

import java.nio.file.Path;
import me.declipsonator.chatcontrol.util.Config;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatControl {
    public static final Logger LOG = LogManager.getLogger("Chat Control");
    public static final Path configFilePath = FMLPaths.CONFIGDIR.get().resolve("chatcontrol.json");

    public void onInitialize() {
        LOG.info("Initializing Chat Control for Forge");
        Config.loadConfig();
        Runtime.getRuntime().addShutdownHook(new Thread(Config::saveConfig));
    }
}
