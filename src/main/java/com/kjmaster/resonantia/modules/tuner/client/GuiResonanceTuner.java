package com.kjmaster.resonantia.modules.tuner.client;

import com.kjmaster.resonantia.modules.tuner.items.ResonanceTunerItem;
import com.kjmaster.resonantia.modules.tuner.network.PacketUpdateResonanceTuner;
import com.kjmaster.resonantia.setup.ResonantiaMessages;
import mcjty.lib.client.GuiTools;
import mcjty.lib.gui.BaseScreen;
import mcjty.lib.gui.IKeyReceiver;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.varia.ComponentFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;

import static mcjty.lib.gui.layout.AbstractLayout.DEFAULT_SPACING;
import static mcjty.lib.gui.widgets.Widgets.*;

public class GuiResonanceTuner extends BaseScreen implements IKeyReceiver {

    protected int xSize = 256;
    protected int ySize = 60;

    private Window window = null;

    private TextField frequencyText;
    private ScrollableLabel frequencySliderLabel;

    public GuiResonanceTuner() {
        super(ComponentFactory.translatable("gui.resonance_tuner.title"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        ItemStack heldItem = getStackToEdit();

        if (heldItem.isEmpty()) return;

        frequencySliderLabel = (new ScrollableLabel()).name("frequency").desiredWidth(30).realMinimum(0).realMaximum(10000).event(this::updateFrequencySlider);
        Slider frequencySlider = (new Slider()).desiredHeight(15).horizontal().minimumKnobSize(15).tooltips("Resonance Tuner Frequency").scrollableName("frequency");
        Panel frequencySliderPanel = Widgets.horizontal().children(new Widget[]{label("Freq:").desiredWidth(30), frequencySlider, frequencySliderLabel}).desiredHeight(20);

        frequencyText = new TextField().event((newText) -> updateFrequencyText()).text(String.valueOf(ResonanceTunerItem.getFrequency(getStackToEdit()))).desiredHeight(15);
        Panel frequencyTextPanel = horizontal(0, DEFAULT_SPACING).desiredHeight(20)
                .children(
                        label("Freq:").horizontalAlignment(HorizontalAlignment.ALIGN_RIGHT).desiredWidth(30),
                        frequencyText
                );

        Panel topLevel = Widgets.vertical().filledRectThickness(2).children(frequencySliderPanel, frequencyTextPanel);

        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        topLevel.setBounds(new Rectangle(k, l, xSize, ySize));

        window = new Window(this, topLevel);

        frequencySliderLabel.realValue(ResonanceTunerItem.getFrequency(getStackToEdit()));
    }

    private void updateFrequency(int i) {
        ItemStack stack = getStackToEdit();
        if (!stack.isEmpty()) {
            ResonanceTunerItem.setFrequency(stack, i);
            ResonantiaMessages.sendToServer(PacketUpdateResonanceTuner.create(stack));
        }
    }

    private void updateFrequencySlider(int i) {
        frequencyText.text(String.valueOf(i));
        updateFrequency(i);
    }

    private void updateFrequencyText() {
        int frequency = parseInt(this.frequencyText.getText());
        frequency = Math.clamp(frequency, 0, 10000);
        frequencySliderLabel.realValue(frequency);
        updateFrequency(frequency);
    }

    private ItemStack getStackToEdit() {
        return minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    protected void renderInternal(GuiGraphics graphics, int i, int i1, float v) {
        if (window == null) return;

        window.draw(graphics);

        List<String> tooltips = window.getTooltips();
        if (tooltips != null) {
            int x = GuiTools.getRelativeX(this);
            int y = GuiTools.getRelativeY(this);
            // @todo check on 1.16
            List<FormattedText> properties = tooltips.stream().map(ComponentFactory::literal).collect(Collectors.toList());
            List<FormattedCharSequence> processors = Language.getInstance().getVisualOrder(properties);
            graphics.renderTooltip(Minecraft.getInstance().font, processors, x, y);
        }
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new GuiResonanceTuner());
    }

    private final boolean[] buttons = new boolean[10];    // @todo ugly hack to get mouse buttons?

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        // If not initialized yet we do nothing
        if (window == null) {
            return;
        }
        window.mouseDragged(mouseX, mouseY, 0); // @todo 1.14 is this right? What button?
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        // If not initialized yet we do nothing
        if (window == null) {
            return false;
        }
        if (button < buttons.length) {
            buttons[button] = true;
        }
        boolean rc = super.mouseClicked(x, y, button);
        window.mouseClicked(x, y, button);
        return rc;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // If not initialized yet we do nothing
        if (window == null) {
            return false;
        }
        if (button < buttons.length) {
            buttons[button] = false;
        }
        boolean rc = super.mouseReleased(mouseX, mouseY, button);
        window.mouseReleased(mouseX, mouseY, button);
        return rc;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // If not initialized yet we do nothing
        if (window == null) {
            return false;
        }
        boolean rc = false;
        if (!window.keyTyped(keyCode, scanCode)) {
            rc = super.keyPressed(keyCode, scanCode, modifiers);
        }
        return rc;
    }

    @Override
    public Window getWindow() {
        return window;
    }

    @Override
    public void keyTypedFromEvent(int keyCode, int scanCode) {
        if (window != null) {
            if (window.keyTyped(keyCode, scanCode)) {
                super.keyPressed(keyCode, scanCode, 0); // @todo 1.14: modifiers?
            }
        }
    }

    @Override
    public void charTypedFromEvent(char codePoint) {
        if (window != null) {
            if (window.charTyped(codePoint)) {
                super.charTyped(codePoint, 0); // @todo 1.14: modifiers?
            }
        }
    }

    @Override
    public boolean mouseClickedFromEvent(double x, double y, int button) {
        WindowManager manager = getWindow().getWindowManager();
        manager.mouseClicked(x, y, button);
        return true;
    }

    @Override
    public boolean mouseReleasedFromEvent(double x, double y, int button) {
        WindowManager manager = getWindow().getWindowManager();
        manager.mouseReleased(x, y, button);
        return true;
    }

    @Override
    public boolean mouseScrolledFromEvent(double x, double y, double dx, double dy) {
        WindowManager manager = getWindow().getWindowManager();
        manager.mouseScrolled(x, y, dx, dy);
        return true;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double wheelX, double wheelY) {
        // If not initialized yet we do nothing
        if (window == null) {
            return false;
        }
        return super.mouseScrolled(x, y, wheelX, wheelY);
    }
}
