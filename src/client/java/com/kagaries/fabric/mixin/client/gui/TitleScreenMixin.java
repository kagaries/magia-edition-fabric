package com.kagaries.fabric.mixin.client.gui;

import com.kagaries.fabric.mixin.client.accessor.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Shadow @Nullable private SplashRenderer splash;

    @Mutable
    @Shadow @Final private PanoramaRenderer panorama;
    @Mutable
    @Shadow @Final public static CubeMap CUBE_MAP;

    private String PanoramaLocation;
    private RandomSource randomSource = RandomSource.create();

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        if (this.splash == null) {
            this.splash = ((MinecraftAccessor) Minecraft.getInstance()).getSplashManager().getSplash();
        }
    }

    @Inject(at = @At("HEAD"), method = "removed()V")
    private void removed(CallbackInfo info) {
        this.splash = null;
    }

    @Inject(at = @At("TAIL"), method = "createNormalMenuOptions")
    private void createNormalMenuOptions(CallbackInfo info) {
        panorama = null;

        int randomInt = randomSource.nextInt(16);
        if (randomInt == 0) {
            PanoramaLocation = "textures/gui/title/background/nethera/panorama";
        } else if (randomInt == 1) {
            PanoramaLocation = "textures/gui/title/background/overworlda/panorama";
        } else if (randomInt == 2) {
            PanoramaLocation = "textures/gui/title/background/enda/panorama";
        } else if (randomInt == 3) {
            PanoramaLocation = "textures/gui/title/background/endb/panorama";
        } else if (randomInt == 4) {
            PanoramaLocation = "textures/gui/title/background/overworldb/panorama";
        } else if (randomInt == 5) {
            PanoramaLocation = "textures/gui/title/background/netherb/panorama";
        } else if (randomInt == 6) {
            PanoramaLocation = "textures/gui/title/background/overworldc/panorama";
        } else if (randomInt == 7) {
            PanoramaLocation = "textures/gui/title/background/netherc/panorama";
        } else if (randomInt == 8) {
            PanoramaLocation = "textures/gui/title/background/overworldd/panorama";
        } else if (randomInt == 9) {
            PanoramaLocation = "textures/gui/title/background/netherd/panorama";
        } else if (randomInt == 10) {
            PanoramaLocation = "textures/gui/title/background/overworlde/panorama";
        } else if (randomInt == 11) {
            PanoramaLocation = "textures/gui/title/background/nethere/panorama";
        } else if (randomInt == 12) {
            PanoramaLocation = "textures/gui/title/background/overworldf/panorama";
        } else if (randomInt == 13) {
            PanoramaLocation = "textures/gui/title/background/netherf/panorama";
        } else if (randomInt == 14) {
            PanoramaLocation = "textures/gui/title/background/overworldg/panorama";
        } else if (randomInt == 15) {
            PanoramaLocation = "textures/gui/title/background/overworldh/panorama";
        }

        CUBE_MAP = new CubeMap(new ResourceLocation(PanoramaLocation));
        panorama = new PanoramaRenderer(CUBE_MAP);
    }
}
