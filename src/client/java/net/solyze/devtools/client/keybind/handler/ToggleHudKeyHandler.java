package net.solyze.devtools.client.keybind.handler;

import net.minecraft.client.MinecraftClient;
import net.solyze.devtools.DevTools;
import net.solyze.devtools.client.keybind.KeyHandler;
import net.solyze.devtools.client.util.Utils;
import net.solyze.devtools.config.DevToolsDataConfig;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class ToggleHudKeyHandler extends KeyHandler {

    public ToggleHudKeyHandler() {
        super("toggle-hud", "tools", GLFW.GLFW_KEY_U);
    }

    @Override
    public void onWasPressed(MinecraftClient client) {
        Optional<Object> optional = DevTools.INSTANCE.getConfig(DevToolsDataConfig.class);
        if (optional.isEmpty()) return;
        DevToolsDataConfig config = (DevToolsDataConfig) optional.get();
        boolean toggled = !config.isHudEnabled();
        config.setHudEnabled(toggled);
        if (client.player == null) return;
        Utils.sendToggleMsg(client.player, "toggle-hud", toggled);
    }
}