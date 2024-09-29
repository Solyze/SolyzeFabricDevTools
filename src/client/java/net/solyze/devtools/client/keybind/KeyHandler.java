package net.solyze.devtools.client.keybind;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.solyze.devtools.DevTools;

import java.util.List;

public abstract class KeyHandler {

    private final String id, category;
    private final int keyCode;
    private final KeyBinding keyBinding;

    public KeyHandler(String id, String category, int keyCode) {
        this.id = id;
        this.category = category;
        this.keyCode = keyCode;
        this.keyBinding = new KeyBinding("key." + DevTools.MOD_ID + "." + id,
                InputUtil.Type.KEYSYM, keyCode, "category." + DevTools.MOD_ID + "." + category);
    }

    public abstract void onWasPressed(MinecraftClient client);
    public void preCheckPress(MinecraftClient client) {}
    public void onInitializeClient() {}
    public void itemTooltipCallback(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {}

    public int getKeyCode() {
        return keyCode;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }
}
