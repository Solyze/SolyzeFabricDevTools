package net.solyze.devtools.client.keybind.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.solyze.devtools.client.keybind.KeyHandler;
import net.solyze.devtools.client.util.Utils;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

public class ShowNbtKeyHandler extends KeyHandler {

    private boolean showNBT;

    public ShowNbtKeyHandler() {
        super("show-nbt", "tools", GLFW.GLFW_KEY_N);
    }

    @Override
    public void itemTooltipCallback(ItemStack stack, TooltipContext context, List<Text> lines) {
        if (!showNBT) return;
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return;
        lines.addAll(Arrays.asList(Text.empty(), Text.literal("{").formatted(Formatting.GRAY)));
        addNbtToTooltip(nbt, lines, "  ");
        lines.add(Text.literal("}").formatted(Formatting.GRAY));
    }

    @SuppressWarnings("SameParameterValue")
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

    @Override
    public void onWasPressed(MinecraftClient client) {
        showNBT = !showNBT;
        if (client.player == null) return;
        Utils.sendToggleMsg(client.player, "show-nbt", showNBT);
    }
}
