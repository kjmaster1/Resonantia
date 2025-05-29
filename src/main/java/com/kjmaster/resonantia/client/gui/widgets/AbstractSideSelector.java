package com.kjmaster.resonantia.client.gui.widgets;

import com.kjmaster.resonantia.data.Mode;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.VerticalAlignment;
import mcjty.lib.gui.widgets.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractSideSelector<T extends AbstractSideSelector<T>> extends AbstractLabel<T> {

    protected final WindowManager mainWindowManager;
    protected final Window mainWindow;
    protected final Mode[] choices;
    protected final Direction[] directions;
    protected final String[] choiceStrings;

    protected AbstractSideSelector(WindowManager mainWindowManager, Window mainWindow, Mode[] choices, Direction[] directions) {
        this.mainWindowManager = mainWindowManager;
        this.mainWindow = mainWindow;
        this.choices = choices;
        this.directions = directions;
        this.choiceStrings = getChoiceStrings();
    }

    private String[] getChoiceStrings() {
        return Arrays.stream(choices)
                .map(Mode::getSerializedName)
                .toArray(String[]::new);
    }

    @Override
    public void draw(Screen gui, GuiGraphics graphics, int x, int y) {
        if (this.visible) {
            int xx = x + this.bounds.x;
            int yy = y + this.bounds.y;
            if (this.isEnabledAndVisible()) {
                if (this.isHovering()) {
                    this.drawStyledBoxHovering(mainWindow, graphics, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
                } else {
                    this.drawStyledBoxNormal(mainWindow, graphics, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
                }
            } else {
                this.drawStyledBoxDisabled(mainWindow, graphics, xx, yy, xx + bounds.width - 1, yy + bounds.height - 1);
            }
            super.drawOffset(gui, graphics, x, y, 0, 1);
        }
    }

    @Override
    public Widget<?> mouseClick(double x, double y, int button) {
        if (this.isEnabledAndVisible()) {
            createSideSelectorWindow();
        }
        return null;
    }

    private void createSideSelectorWindow() {
        Panel modalDialog = Widgets.positional().filledRectThickness(2);
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) mainWindow.getGui();
        int guiLeft = screen.getGuiLeft();
        int guiTop = screen.getGuiTop();
        int xSize = screen.getXSize();
        int ySize = screen.getYSize();

        int labelHeight = 12;
        int labelSpacing = 0;
        int topPadding = 4;
        int bottomPadding = 20;
        int totalHeight = directions.length * (labelHeight + labelSpacing) + topPadding + bottomPadding;

        int sideLeft = guiLeft + xSize + 20;
        int sideTop = guiTop + (ySize - totalHeight) / 2;

        modalDialog.bounds(sideLeft, sideTop, 100, totalHeight);

        List<ChoiceLabel> labels = new ArrayList<>();

        for (int i = 0; i < directions.length; i++) {
            Direction dir = directions[i];
            String currentMode = getCurrentMode(dir);
            String name = getLabelPrefix() + dir.name().charAt(0) + dir.name().substring(1).toLowerCase();
            String tooltip = getTooltipBase(dir);
            labels.add(createChoiceLabel(name, currentMode, dir, tooltip, topPadding + i * (labelHeight + labelSpacing)));
        }

        Button close = Widgets.button(25, totalHeight - 15, 50, 12, "Close")
                .horizontalAlignment(HorizontalAlignment.ALIGN_CENTER)
                .verticalAlignment(VerticalAlignment.ALIGN_CENTER);

        modalDialog.children(Stream.concat(labels.stream(), Stream.of(close)).toArray(Widget[]::new));
        Window modalWindow = mainWindowManager.createModalWindow(modalDialog);
        close.event(() -> mainWindowManager.closeWindow(modalWindow));
    }

    private ChoiceLabel createChoiceLabel(String name, String currentMode, Direction direction, String tooltipBase, int yOffset) {
        ChoiceLabel label = new ChoiceLabel()
                .name(name)
                .choices(choiceStrings);

        for (String choice : choiceStrings) {
            String toolTip = tooltipBase + ": " + choice.charAt(0) + choice.substring(1).toLowerCase();
            label.choiceTooltip(choice, toolTip);
        }

        label.hint(25, yOffset, 50, 10)
                .horizontalAlignment(HorizontalAlignment.ALIGN_CENTER)
                .verticalAlignment(VerticalAlignment.ALIGN_CENTER)
                .event(s -> sendServerCommand(s, direction))
                .choice(currentMode);
        return label;
    }

    protected abstract String getCurrentMode(Direction direction);

    protected abstract void sendServerCommand(String modeString, Direction direction);

    protected abstract String getLabelPrefix();

    protected abstract String getTooltipBase(Direction direction);
}

