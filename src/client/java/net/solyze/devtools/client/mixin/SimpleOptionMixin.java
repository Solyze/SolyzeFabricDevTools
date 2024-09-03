package net.solyze.devtools.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.solyze.devtools.DevTools;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleOption.class)
public class SimpleOptionMixin<T> {

    @Shadow
    @Mutable
    T value;

    @Inject(method = "setValue", at = @At("HEAD"), cancellable = true)
    public void onSetValue(T value, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        GameOptions options = client.options;
        if (options == null) return;

        if ((Object) this == options.getGamma()) {
            DevTools.LOGGER.info("Gamma value being set to {}, injecting to allow any value.", value);
            double gammaValue = (double) value;

            @SuppressWarnings("unchecked")
            T newValue = (T) Double.valueOf(gammaValue);
            this.value = newValue;
            ci.cancel();
        }
    }
}