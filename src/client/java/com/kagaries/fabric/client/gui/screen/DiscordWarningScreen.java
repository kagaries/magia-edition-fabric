package com.kagaries.fabric.client.gui.screen;

import com.kagaries.util.Reference;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class DiscordWarningScreen extends Screen {
    private static final Text TITLE = Text.translatable("discord.warning.title");
    private static final Text MESSAGE = Text.translatable("discord.warning.message");
    private final GridWidget grid = (new GridWidget()).setColumnSpacing(10).setRowSpacing(20);

    public DiscordWarningScreen() {
        super(TITLE);
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(new Text[]{super.getNarratedTitle(), MESSAGE});
    }

    protected void init() {
        super.init();
        GridWidget.Adder adder = this.grid.createAdder(2);
        Positioner positioner = adder.copyPositioner().alignHorizontalCenter();
        adder.add(new TextWidget(this.title, this.textRenderer), 2, positioner);
        MultilineTextWidget multilineTextWidget = (MultilineTextWidget)adder.add((new MultilineTextWidget(MESSAGE, this.textRenderer)).setCentered(true), 2, positioner);
        multilineTextWidget.setMaxWidth(210);
        adder.add(ButtonWidget.builder(ScreenTexts.PROCEED, (button) -> {
            this.close();
            Util.getOperatingSystem().open(Reference.discordURL);
        }).build());
        adder.add(ButtonWidget.builder(ScreenTexts.BACK, (button) -> {
            this.close();
        }).build());
        this.grid.forEachChild((child) -> {
            ClickableWidget var10000 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.grid.refreshPositions();
        this.initTabNavigation();
    }

    protected void initTabNavigation() {
        SimplePositioningWidget.setPos(this.grid, 0, 0, this.width, this.height, 0.5F, 0.5F);
    }

    public void close() {
        MinecraftClient.getInstance().setScreen(new TitleScreen());
    }
}
