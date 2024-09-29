package net.solyze.devtools.client.keybind.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.solyze.devtools.DevTools;
import net.solyze.devtools.client.keybind.KeyHandler;
import net.solyze.devtools.client.util.Utils;
import net.solyze.devtools.config.DevToolsDataConfig;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class ToggleFullbrightKeyHandler extends KeyHandler {

    private double previousGamma = -Integer.MAX_VALUE;

    public ToggleFullbrightKeyHandler() {
        super("toggle-fullbright", "tools", GLFW.GLFW_KEY_B);
    }

    @Override
    public void onWasPressed(MinecraftClient client) {
        Optional<Object> optional = DevTools.INSTANCE.getConfig(DevToolsDataConfig.class);
        if (optional.isEmpty()) return;
        DevToolsDataConfig config = (DevToolsDataConfig) optional.get();
        GameOptions options = client.options;
        if (config.isFullbrightEnabled()) {
            options.getGamma().setValue(previousGamma);
        } else {
            setPreviousGamma(client);
            options.getGamma().setValue(10.0);
        }
        boolean toggled = !config.isFullbrightEnabled();
        config.setFullbrightEnabled(toggled);
        if (client.player == null) return;
        Utils.sendToggleMsg(client.player, "toggle-fullbright", toggled);
    }

    @Override
    public void preCheckPress(MinecraftClient client) {
        if (previousGamma == -Integer.MAX_VALUE) setPreviousGamma(client);
    }

    private void setPreviousGamma(MinecraftClient client) {
        if (client.options == null) return;
        previousGamma = client.options.getGamma().getValue();
    }
}
