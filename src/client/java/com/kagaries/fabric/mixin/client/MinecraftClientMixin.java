package com.kagaries.fabric.mixin.client;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    //Changes window title to "Magia Edition"
    @Redirect(method = "getWindowTitle", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;toString()Ljava/lang/String;"))
    private String getWindowTitle(StringBuilder instance) {
        StringBuilder stringBuilder = new StringBuilder("Magia Edition");

        stringBuilder.append(" ");
        stringBuilder.append(SharedConstants.getGameVersion().getName());
        ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            stringBuilder.append(" - ");
            ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
            if (MinecraftClient.getInstance().getServer() != null && !MinecraftClient.getInstance().getServer().isRemote()) {
                stringBuilder.append(I18n.translate("title.singleplayer", new Object[0]));
            } else if (serverInfo != null && serverInfo.isRealm()) {
                stringBuilder.append(I18n.translate("title.multiplayer.realms", new Object[0]));
            } else if (MinecraftClient.getInstance().getServer() == null && (serverInfo == null || !serverInfo.isLocal())) {
                stringBuilder.append(I18n.translate("title.multiplayer.other", new Object[0]));
            } else {
                stringBuilder.append(I18n.translate("title.multiplayer.lan", new Object[0]));
            }
        }

        return stringBuilder.toString();
    }
}
