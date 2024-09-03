package net.solyze.devtools.client.keybind.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.solyze.devtools.client.keybind.KeyHandler;
import net.solyze.devtools.client.util.Utils;
import org.lwjgl.glfw.GLFW;

public class ToggleFullbrightKeyHandler extends KeyHandler {

    private boolean fullbrightEnabled;
    private double previousGamma = -Integer.MAX_VALUE;

    public ToggleFullbrightKeyHandler() {
        super("toggle-fullbright", "tools", GLFW.GLFW_KEY_B);
    }

    @Override
    public void onWasPressed(MinecraftClient client) {
        GameOptions options = client.options;
        if (fullbrightEnabled) {
            options.getGamma().setValue(previousGamma);
        } else {
            setPreviousGamma(client);
            options.getGamma().setValue(10.0);
        }
        fullbrightEnabled = !fullbrightEnabled;
        if (client.player == null) return;
        Utils.sendToggleMsg(client.player, "toggle-fullbright", fullbrightEnabled);
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
