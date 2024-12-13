package com.kagaries.fabric.mixin.client.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SplashManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor
    SplashManager getSplashManager();
}
