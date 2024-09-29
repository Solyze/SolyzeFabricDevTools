package net.solyze.devtools.client.keybind.handler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.solyze.devtools.DevTools;
import net.solyze.devtools.client.keybind.KeyHandler;
import net.solyze.devtools.client.util.Utils;
import net.solyze.devtools.config.DevToolsDataConfig;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ShowItemComponentsHandler extends KeyHandler {

    public ShowItemComponentsHandler() {
        super("show-item-components", "tools", GLFW.GLFW_KEY_N);
    }

    @Override
    public void itemTooltipCallback(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
        Optional<Object> optional = DevTools.INSTANCE.getConfig(DevToolsDataConfig.class);
        if (optional.isEmpty()) return;
        DevToolsDataConfig config = (DevToolsDataConfig) optional.get();
        if (!config.isShowItemComponents()) return;
        ComponentMap components = stack.getComponents();
        if (components == ComponentMap.EMPTY) return;
        addComponentsToTooltip(components, lines);
    }

    private void addComponentsToTooltip(ComponentMap components, List<Text> lines) {
        lines.add(Text.empty());
        Text header = Text.empty()
                .append(Text.literal("Item Components").formatted(Formatting.GRAY, Formatting.BOLD))
                .append(Text.literal(" [Toggle with ").formatted(Formatting.DARK_GRAY))
                .append(Text.keybind("key." + DevTools.MOD_ID + ".show-item-components").formatted(Formatting.DARK_GRAY))
                .append(Text.literal("]").formatted(Formatting.DARK_GRAY));
        lines.add(header);
        Set<ComponentType<?>> types = components.getTypes();
        for (ComponentType<?> type : types) {
            Object value = components.get(type);
            if (value == null) continue;
            lines.add(Text.literal(type.toString() + ": " + value).formatted(Formatting.GRAY));
        }
    }

    @Override
    public void onWasPressed(MinecraftClient client) {
        Optional<Object> optional = DevTools.INSTANCE.getConfig(DevToolsDataConfig.class);
        if (optional.isEmpty()) return;
        DevToolsDataConfig config = (DevToolsDataConfig) optional.get();
        boolean toggled = !config.isShowItemComponents();
        config.setShowItemComponents(toggled);
        if (client.player == null) return;
        Utils.sendToggleMsg(client.player, "show-item-components", toggled);
    }
}
