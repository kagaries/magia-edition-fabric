package com.kagaries.fabric.mixin.client.gui;

import com.kagaries.fabric.client.gui.screen.DiscordWarningScreen;
import com.kagaries.fabric.mixin.client.accessor.MinecraftAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Shadow @Nullable private SplashTextRenderer splashText;

    @Mutable
    @Shadow @Final private RotatingCubeMapRenderer backgroundRenderer;
    @Mutable
    @Shadow @Final public static CubeMapRenderer PANORAMA_CUBE_MAP;

    private String PanoramaLocation;
    private Random randomSource = Random.create();

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        if (this.splashText == null) {
            this.splashText = ((MinecraftAccessor) MinecraftClient.getInstance()).getSplashTextLoader().get();
        }
    }

    @Redirect(method = "switchToRealms", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void switchToRealms(MinecraftClient instance, Screen screen) {
        MinecraftClient.getInstance().setScreen(new DiscordWarningScreen());
    }

    @Inject(at = @At("HEAD"), method = "removed()V")
    private void removed(CallbackInfo info) {
        this.splashText = null;
    }

    @Inject(at = @At("TAIL"), method = "initWidgetsNormal")
    private void createNormalMenuOptions(CallbackInfo info) {
        backgroundRenderer = null;

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

        PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier(PanoramaLocation));
        backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
    }
}
