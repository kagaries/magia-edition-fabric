package com.kagaries.fabric.mixin.client.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftAccessor {
    @Accessor
    SplashTextResourceSupplier getSplashTextLoader();
}
