package net.solyze.devtools.client.mixin;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.solyze.devtools.DevTools;
import net.solyze.devtools.config.DevToolsDataConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Optional;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow public abstract void render(DrawContext context, RenderTickCounter tickCounter);
    @Unique private int y;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        Optional<Object> optional = DevTools.INSTANCE.getConfig(DevToolsDataConfig.class);
        if (optional.isEmpty()) return;
        DevToolsDataConfig config = (DevToolsDataConfig) optional.get();
        if (!config.isHudEnabled()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getDebugHud().shouldShowDebugHud() || client.options.hudHidden) return;
        TextRenderer textRenderer = client.textRenderer;

        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();

        this.y = -6; // Set to -6 to render the first text at 4, 4
        draw(context, textRenderer, Text.literal(String.format("%s %s",
                DevTools.MOD_DISPLAY,
                DevTools.MOD_VERSION
        )).formatted(Formatting.GRAY));
        draw(context, textRenderer, Text.literal(String.format("Minecraft %s (%s/%s)",
                SharedConstants.getGameVersion().name(),
                client.getGameVersion(),
                ClientBrandRetriever.getClientModName()
        )).formatted(Formatting.GRAY));
        space(4);
        draw(context, textRenderer, "FPS", client.getCurrentFps());
        if (client.getNetworkHandler() != null && client.player != null) {
            PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) {
                String ping = entry.getLatency() + (client.isInSingleplayer() ? "ms (Singleplayer)" : "ms");
                draw(context, textRenderer, "Ping", ping);
            }
        }

        draw(context, textRenderer, "CPU",
                GLX._getCpuInfo() + String.format(" (%s of %sMB)",
                getMaxPercentageString((double) ((totalMemory - freeMemory) * 100L) / maxMemory),
                toMiB(maxMemory)
        ));
        GpuDevice gpuDevice = RenderSystem.getDevice();
        draw(context, textRenderer, "GPU", String.format("%s (%s)",
                gpuDevice.getRenderer(), getMaxPercentageString(client.getGpuUtilizationPercentage())));
        if (client.cameraEntity != null) {
            draw(context, textRenderer, "XYZ", String.format("%.2f / %.2f / %.2f",
                    client.cameraEntity.getX(),
                    client.cameraEntity.getY(),
                    client.cameraEntity.getZ()
            ));
            if (client.world != null) {
                BlockPos blockPos = client.cameraEntity.getBlockPos();
                if (blockPos.getY() >= client.world.getBottomY() && blockPos.getY() <
                        client.world.getTopY(Heightmap.Type.WORLD_SURFACE, blockPos.getX(), blockPos.getZ())) {
                    RegistryEntry<Biome> var27 = client.world.getBiome(blockPos);
                    draw(context, textRenderer, "Biome", getBiomeString(var27));
                }
            }
            Direction direction = client.cameraEntity.getHorizontalFacing();
            String string2;
            switch (direction) {
                case NORTH -> string2 = "-Z";
                case SOUTH -> string2 = "+Z";
                case WEST -> string2 = "-X";
                case EAST -> string2 = "+X";
                default -> string2 = "Invalid";
            }
            draw(context, textRenderer, "Facing", String.format("%s (%s) (%.2f / %.2f)",
                    direction, string2,
                    MathHelper.wrapDegrees(client.cameraEntity.getYaw()),
                    MathHelper.wrapDegrees(client.cameraEntity.getPitch())
            ));
            if (System.getProperty("os.arch") != null) {
                draw(context, textRenderer, "Java", String.format("%s %dbit",
                        System.getProperty("java.version"),
                        System.getProperty("os.arch").contains("64") ? 64 : 32)
                );
            }
            HitResult blockHit = client.cameraEntity.raycast(20.0, 0.0F, false);
            if (!hasReducedDebugInfo(client) && blockHit.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult) blockHit).getBlockPos();
                if (client.world != null) {
                    BlockState blockState = client.world.getBlockState(blockPos);
                    draw(context, textRenderer, "Looking at", String.format("%s (%s / %s / %s)",
                            Registries.BLOCK.getId(blockState.getBlock()),
                            blockPos.getX(), blockPos.getY(), blockPos.getZ()
                    ));
                }
            }
            if (client.targetedEntity != null) {
                draw(context, textRenderer, "Targeted Entity",
                        Registries.ENTITY_TYPE.getId(client.targetedEntity.getType()));
            }
        }
    }

    @Unique
    private int calculateMedian(Integer[] numbers) {
        Arrays.sort(numbers);
        int length = numbers.length;
        if (length % 2 == 1) {
            return numbers[length / 2];
        } else {
            int mid1 = length / 2;
            int mid2 = mid1 - 1;
            return (numbers[mid1] + numbers[mid2]) / 2;
        }
    }


    @Unique
    private String getMaxPercentageString(double percentage) {
        return percentage > 100.0 ? Formatting.RED + "100%" : Math.round(percentage) + "%";
    }

    @Unique
    private boolean hasReducedDebugInfo(MinecraftClient client) {
        return client.player != null && client.player.hasReducedDebugInfo()
                || client.options.getReducedDebugInfo().getValue();
    }

    @Unique
    private String getBiomeString(RegistryEntry<Biome> biome) {
        return biome.getKeyOrValue().map((biomeKey) -> biomeKey.getValue().toString(),
                (biome_) -> "[unregistered " + biome_ + "]");
    }

    @Unique
    private void space(int amount) {
        this.y += amount;
    }

    @Unique
    private void draw(DrawContext context, TextRenderer textRenderer, Text text) {
        space(10);
        int color = 0xFFFFFF;
        int finalColor = ((230 & 0xFF) << 24) | (color & 0xFFFFFF);
        context.drawText(textRenderer, text, 4, this.y, finalColor, true);
    }

    @Unique
    private void draw(DrawContext context, TextRenderer textRenderer, String name, Object value) {
        draw(context, textRenderer, Text.literal(name).append(Text.literal(": "))
                .append(Text.literal(value.toString()).formatted(Formatting.DARK_AQUA)));
    }

    @Unique
    private long toMiB(long bytes) {
        return bytes / 1024L / 1024L;
    }
}