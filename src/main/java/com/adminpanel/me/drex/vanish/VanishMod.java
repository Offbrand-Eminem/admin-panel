package me.drex.vanish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VanishMod {
    public static final String MOD_ID = "admin-panel";
    public static final Logger LOGGER = LoggerFactory.getLogger("vanish");

    private VanishMod() {
    }

    public static void initialize() {
        LOGGER.info("Vanish integrated into admin-panel");
    }
}
