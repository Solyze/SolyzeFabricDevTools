package net.solyze.devtools;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevTools implements ModInitializer {

    public static final String MOD_ID = "solyze-fabric-dev-tools",
                               MOD_DISPLAY = "SolyzeFabricDevTools";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public DevTools() {
        LOGGER.info("{} initializing...", MOD_DISPLAY);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("{} has been initialized.", MOD_DISPLAY);
    }
}
