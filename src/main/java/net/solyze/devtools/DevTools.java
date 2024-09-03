package net.solyze.devtools;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevTools implements ModInitializer {

    public static final String MOD_ID = "solyze-fabric-dev-tools",
                               MOD_DISPLAY = "SolyzeFabricDevTools",
                               MOD_VERSION = "v1.02";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_DISPLAY);

    public DevTools() {
        LOGGER.info("{} initializing...", MOD_DISPLAY);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("{} has been initialized.", MOD_DISPLAY);
    }
}
