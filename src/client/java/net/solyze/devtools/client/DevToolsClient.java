package net.solyze.devtools.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.solyze.devtools.DevTools;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class DevToolsClient implements ClientModInitializer {

    // TODO: Cleanup. Make classes for handling stuff bla bla you get it (I hope)

    public static DevToolsClient INSTANCE;
    private final KeyBinding showNBTKeyBinding;
    private final KeyBinding toggleFullbrightKeyBinding;
    private boolean showNBT;
    private boolean fullbrightEnabled;
    private double previousGamma = -Integer.MAX_VALUE;

    public DevToolsClient() {
        DevTools.LOGGER.info("{} (Client) initializing...", DevTools.MOD_DISPLAY);
        showNBTKeyBinding = key("show-nbt", GLFW.GLFW_KEY_N, "tools");
        toggleFullbrightKeyBinding = key("toggle-fullbright", GLFW.GLFW_KEY_B, "tools");
    }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        KeyBindingHelper.registerKeyBinding(showNBTKeyBinding);
        KeyBindingHelper.registerKeyBinding(toggleFullbrightKeyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
        ItemTooltipCallback.EVENT.register(this::showNBTTooltipCallback);
        DevTools.LOGGER.info("{} (Client) has been initialized.", DevTools.MOD_DISPLAY);
    }

    private void onEndClientTick(MinecraftClient client) {
        if (client.options == null) return;
        if (previousGamma == -Integer.MAX_VALUE) {
            previousGamma = client.options.getGamma().getValue();
        }
        if (showNBTKeyBinding.wasPressed()) toggleShowNBT(client);
        if (toggleFullbrightKeyBinding.wasPressed()) toggleFullbright();
    }

    private void toggleFullbright() {
        MinecraftClient client = MinecraftClient.getInstance();
        GameOptions options = client.options;
        if (fullbrightEnabled) {
            options.getGamma().setValue(previousGamma);
        } else {
            options.getGamma().setValue(1000.0);
        }
        fullbrightEnabled = !fullbrightEnabled;
        if (client.player == null) return;
        sendToggleMsg(client.player, "toggle-fullbright", fullbrightEnabled);
    }

    private void toggleShowNBT(MinecraftClient client) {
        showNBT = !showNBT;
        if (client.player == null) return;
        sendToggleMsg(client.player, "show-nbt", showNBT);
    }

    private void sendToggleMsg(@NotNull ClientPlayerEntity player, String id, boolean condition) {
        MutableText text = Text.translatable("text." + DevTools.MOD_ID
                + "." + id + "." + (condition ? "enabled" : "disabled"));
        player.sendMessage(text.formatted(condition ? Formatting.GREEN : Formatting.GRAY), true);
    }

    private void showNBTTooltipCallback(ItemStack stack, TooltipContext context, List<Text> lines) {
        if (!showNBT) return;
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return;
        lines.addAll(Arrays.asList(Text.empty(), Text.literal("{").formatted(Formatting.GRAY)));
        addNbtToTooltip(nbt, lines, "  ");
        lines.add(Text.literal("}").formatted(Formatting.GRAY));
    }

    private void addNbtToTooltip(NbtCompound nbt, List<Text> lines, String prefix) {
        for (String key : nbt.getKeys()) {
            NbtElement element = nbt.get(key);
            if (element instanceof NbtCompound) {
                addCompoundToTooltip((NbtCompound) element, lines, prefix, key);
            } else if (element instanceof NbtList list) {
                addListToTooltip(list, lines, prefix, key);
            } else {
                assert element != null;
                addSimpleElementToTooltip(element, lines, prefix, key);
            }
        }
    }

    private void addCompoundToTooltip(NbtCompound compound, List<Text> lines, String prefix, String key) {
        lines.add(Text.literal(prefix + key + ": {").formatted(Formatting.GRAY));
        addNbtToTooltip(compound, lines, prefix + "  ");
        lines.add(Text.literal(prefix + "}").formatted(Formatting.GRAY));
    }

    private void addListToTooltip(NbtList list, List<Text> lines, String prefix, String key) {
        lines.add(Text.literal(prefix + key + ": [").formatted(Formatting.GRAY));
        for (NbtElement element : list) {
            lines.add(Text.literal(prefix + "  " + element.asString()).formatted(Formatting.GRAY));
        }
        lines.add(Text.literal(prefix + "]").formatted(Formatting.GRAY));
    }

    private void addSimpleElementToTooltip(NbtElement element, List<Text> lines, String prefix, String key) {
        lines.add(Text.literal(prefix + key + ": " + element.asString()).formatted(Formatting.GRAY));
    }

    @SuppressWarnings("SameParameterValue")
    private KeyBinding key(String id, int code, String category) {
        return new KeyBinding("key." + DevTools.MOD_ID + "." + id,
                InputUtil.Type.KEYSYM, code, "category." + DevTools.MOD_ID + "." + category);
    }
}
