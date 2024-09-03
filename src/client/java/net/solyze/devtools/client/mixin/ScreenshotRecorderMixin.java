package net.solyze.devtools.client.mixin;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.solyze.devtools.DevTools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.function.Consumer;

@Mixin(ScreenshotRecorder.class)
public abstract class ScreenshotRecorderMixin {

    // TODO: Copy the screenshot to clipboard so you dont have to open it

    @Inject(method = "saveScreenshotInner", at = @At(value = "HEAD"), cancellable = true)
    private static void inSaveScreenshotInner(File gameDirectory, String fileName, Framebuffer framebuffer,
                                              Consumer<Text> messageReceiver, CallbackInfo ci) {
        NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(framebuffer);
        File file = new File(gameDirectory, "screenshots");
        //noinspection ResultOfMethodCallIgnored
        file.mkdir();
        File file2 = (fileName == null) ? getScreenshotFilename(file) : new File(file, fileName);
        Util.getIoWorkerExecutor().execute(() -> {
            try {
                nativeImage.writeTo(file2);
                Text fileText = Text.literal(file2.getName()).formatted(Formatting.DARK_AQUA)
                        .formatted(Formatting.UNDERLINE).styled((style) -> style
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text
                                        .translatable("text." + DevTools.MOD_ID + ".screenshot.hover"))));
                messageReceiver.accept(Text.translatable("text." +
                        DevTools.MOD_ID + ".screenshot.success", fileText));
            } catch (Exception ex) {
                DevTools.LOGGER.warn("Couldn't save screenshot", ex);
                messageReceiver.accept(Text.translatable("text." +
                        DevTools.MOD_ID + ".screenshot.failure", ex.getMessage()));
            } finally {
                nativeImage.close();
            }

        });
        ci.cancel();
    }

    @Unique
    private static File getScreenshotFilename(File directory) {
        String string = Util.getFormattedCurrentTime();
        int i = 1;
        while(true) {
            File file = new File(directory, string + (i == 1 ? "" : "_" + i) + ".png");
            if (!file.exists()) {
                return file;
            }
            ++i;
        }
    }
}