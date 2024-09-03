package net.solyze.devtools.client.util;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.solyze.devtools.DevTools;
import org.jetbrains.annotations.NotNull;

public class Utils {

    public static void sendToggleMsg(@NotNull ClientPlayerEntity player, String id, boolean condition) {
        MutableText text = Text.translatable("text." + DevTools.MOD_ID
                + "." + id + "." + (condition ? "enabled" : "disabled"));
        player.sendMessage(text.formatted(condition ? Formatting.GREEN : Formatting.GRAY), true);
    }
}
