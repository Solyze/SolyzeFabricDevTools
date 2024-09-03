package net.solyze.devtools.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.solyze.devtools.DevTools;
import net.solyze.devtools.client.keybind.handler.ToggleFullbrightKeyHandler;
import net.solyze.devtools.client.keybind.KeyHandler;
import net.solyze.devtools.client.keybind.handler.ShowNbtKeyHandler;
import net.solyze.devtools.client.keybind.handler.ToggleHudKeyHandler;

import java.util.ArrayList;
import java.util.List;

public class DevToolsClient implements ClientModInitializer {

    public static DevToolsClient INSTANCE;

    private final List<KeyHandler> keyBindHandlers = new ArrayList<>();

    public DevToolsClient() {
        DevTools.LOGGER.info("{} (Client) initializing...", DevTools.MOD_DISPLAY);
    }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        registerKeyBindHandler(new ShowNbtKeyHandler());
        registerKeyBindHandler(new ToggleFullbrightKeyHandler());
        registerKeyBindHandler(new ToggleHudKeyHandler());

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
        ItemTooltipCallback.EVENT.register(this::itemTooltipCallback);
        DevTools.LOGGER.info("{} (Client) has been initialized.", DevTools.MOD_DISPLAY);
    }

    private void onEndClientTick(MinecraftClient client) {
        if (client.options == null) return;
        for (KeyHandler keyHandler : keyBindHandlers) keyHandler.preCheckPress(client);
        for (KeyHandler keyHandler : keyBindHandlers) if (keyHandler.getKeyBinding().wasPressed())
            keyHandler.onWasPressed(client);
    }

    private void itemTooltipCallback(ItemStack stack, TooltipContext context, List<Text> lines) {
        for (KeyHandler keyHandler : keyBindHandlers) keyHandler.itemTooltipCallback(stack, context, lines);
    }

    private void registerKeyBindHandler(KeyHandler keyHandler) {
        KeyBindingHelper.registerKeyBinding(keyHandler.getKeyBinding());
        keyHandler.onInitializeClient();
        this.keyBindHandlers.add(keyHandler);
    }
}
