package net.solyze.devtools.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.solyze.devtools.DevTools;
import net.solyze.devtools.client.keybind.handler.ToggleFullbrightKeyHandler;
import net.solyze.devtools.client.keybind.KeyHandler;
import net.solyze.devtools.client.keybind.handler.ShowItemComponentsHandler;
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

        this.registerKeyBindHandler(new ShowItemComponentsHandler());
        this.registerKeyBindHandler(new ToggleFullbrightKeyHandler());
        this.registerKeyBindHandler(new ToggleHudKeyHandler());

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
        ItemTooltipCallback.EVENT.register(this::itemTooltipCallback);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> DevTools.INSTANCE.saveConfigs());

        DevTools.LOGGER.info("{} (Client) has been initialized.", DevTools.MOD_DISPLAY);
    }

    private void onEndClientTick(MinecraftClient client) {
        if (client.options == null) return;
        for (KeyHandler keyHandler : this.keyBindHandlers) keyHandler.preCheckPress(client);
        for (KeyHandler keyHandler : this.keyBindHandlers) if (keyHandler.getKeyBinding().wasPressed())
            keyHandler.onWasPressed(client);
    }

    private void itemTooltipCallback(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
        for (KeyHandler keyHandler : this.keyBindHandlers) keyHandler.itemTooltipCallback(stack, context, type, lines);
    }

    private void registerKeyBindHandler(KeyHandler keyHandler) {
        KeyBindingHelper.registerKeyBinding(keyHandler.getKeyBinding());
        keyHandler.onInitializeClient();
        this.keyBindHandlers.add(keyHandler);
    }
}
