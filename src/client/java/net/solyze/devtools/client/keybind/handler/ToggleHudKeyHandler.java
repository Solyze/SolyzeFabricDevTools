package net.solyze.devtools.client.keybind.handler;

import net.minecraft.client.MinecraftClient;
import net.solyze.devtools.client.keybind.KeyHandler;
import net.solyze.devtools.client.util.Utils;
import org.lwjgl.glfw.GLFW;

public class ToggleHudKeyHandler extends KeyHandler {

    public static boolean HUD_ENABLED = true;

    public ToggleHudKeyHandler() {
        super("toggle-hud", "tools", GLFW.GLFW_KEY_U);
    }

    @Override
    public void onWasPressed(MinecraftClient client) {
        HUD_ENABLED = !HUD_ENABLED;
        if (client.player == null) return;
        Utils.sendToggleMsg(client.player, "toggle-hud", HUD_ENABLED);
    }
}