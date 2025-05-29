package com.kjmaster.resonantia.client.gui;

import com.kjmaster.resonantia.client.gui.widgets.EnergySideSelector;
import com.kjmaster.resonantia.client.gui.widgets.FluidSideSelector;
import com.kjmaster.resonantia.client.gui.widgets.ItemSideSelector;
import com.kjmaster.resonantia.data.Mode;
import com.kjmaster.resonantia.tileentity.ModularResonatingMachineTE;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.gui.widgets.EnergyBar;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.Widgets;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.RedstoneMode;
import mcjty.rftoolsbase.RFToolsBase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public abstract class GuiModularResonatingMachine<T extends ModularResonatingMachineTE, C extends AbstractContainerMenu> extends GenericGuiContainer<T, C> {

    protected EnergyBar energyBar;
    private static final ResourceLocation iconGuiElements = ResourceLocation.fromNamespaceAndPath(RFToolsBase.MODID, "textures/gui/guielements.png");

    public GuiModularResonatingMachine(C container, Inventory inventory, Component title, ManualEntry manualEntry) {
        super(container, inventory, title, manualEntry);

    }

    @Override
    public void init() {
        T be = getBE();
        super.init();

        energyBar = getEnergyBar(be);
        ImageChoiceLabel redstoneMode = initRedstoneMode();

        Panel topLevel = Widgets.positional().background(be.getGui()).children(redstoneMode);
        addToPanel(topLevel);

        topLevel.bounds(leftPos, topPos, imageWidth, imageHeight);
        window = new Window(this, topLevel);
        window.bind("redstone", be, GenericTileEntity.VALUE_RSMODE.name());

        initSideSelectors(be, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
    }

    protected void addToPanel(Panel topLevel) {
        if (energyBar != null) {
            topLevel.children(energyBar);
        }
    }

    protected void initSideSelectors(T be, int guiLeft, int guiTop, int xSize, int ySize) {

        int sideLeft = guiLeft + xSize;
        int sideTop = guiTop + (ySize - 57) / 2 - 48;

        int y = 1;
        int totalY = 1;

        Panel sidePanel = Widgets.positional();

        if (be.energyStorage != null) {
            EnergySideSelector energySideSelector = new EnergySideSelector(getEnergyModes(), getEnergyDirections(), getWindowManager(), window, be)
                    .name("energySideSelector").hint(1, y, 16, 16)
                    .text("E").tooltips("Sided Energy I/O Config");
            totalY += y;
            y += 18;
            totalY += y;
            sidePanel.children(energySideSelector);
        }

        if (be.items != null) {
            ItemSideSelector itemSideSelector = new ItemSideSelector(getItemModes(), getItemDirections(), getWindowManager(), window, be)
                    .name("itemSideSelector").hint(1, y, 16, 16)
                    .text("I").tooltips("Sided Item I/O Config");
            totalY += y;
            y += 18;
            totalY += y;
            sidePanel.children(itemSideSelector);
        }

        if (be.fluidHandler != null) {
            FluidSideSelector fluidSideSelector = new FluidSideSelector(getFluidModes(), getFluidDirections(), getWindowManager(), window, be)
                    .name("fluidSideSelector").hint(1, y, 16, 16)
                    .text("F").tooltips("Sided Fluid I/O Config");
            totalY += y;
            y += 18;
            totalY += y;
            sidePanel.children(fluidSideSelector);
        }

        sidePanel.bounds(sideLeft, sideTop, 20, totalY);
        getWindowManager().addWindow(new Window(this, sidePanel));
    }

    protected Mode[] getEnergyModes() {
        return new Mode[]{Mode.MODE_NONE, Mode.MODE_INPUT};
    }

    protected Direction[] getEnergyDirections() {
        return new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    }

    protected Mode[] getItemModes() {
        return new Mode[]{Mode.MODE_NONE, Mode.MODE_INPUT, Mode.MODE_OUTPUT, Mode.MODE_BOTH};
    }

    protected Direction[] getItemDirections() {
        return new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    }

    protected Mode[] getFluidModes() {
        return new Mode[]{Mode.MODE_NONE, Mode.MODE_INPUT, Mode.MODE_OUTPUT, Mode.MODE_BOTH};
    }

    protected Direction[] getFluidDirections() {
        return new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    }

    protected EnergyBar getEnergyBar(T be) {
        if (be.energyStorage != null) {
            return new EnergyBar().vertical().hint(10, 7, 8, 54).showText(false);
        }
        return null;
    }

    private ImageChoiceLabel initRedstoneMode() {
        ImageChoiceLabel redstoneMode = new ImageChoiceLabel()
                .name("redstone")
                .choice(RedstoneMode.REDSTONE_IGNORED.getDescription(), "Redstone mode:\nIgnored", iconGuiElements, 0, 0)
                .choice(RedstoneMode.REDSTONE_OFFREQUIRED.getDescription(), "Redstone mode:\nOff to activate", iconGuiElements, 16, 0)
                .choice(RedstoneMode.REDSTONE_ONREQUIRED.getDescription(), "Redstone mode:\nOn to activate", iconGuiElements, 32, 0);
        redstoneMode.hint(19, 44, 16, 16);
        redstoneMode.setCurrentChoice(getBE().getRSMode().ordinal());
        return redstoneMode;
    }

    protected void updateFields() {
        if (window == null) return;
        if (energyBar != null) {
            updateEnergyBar(energyBar);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
        updateFields();
        drawWindow(graphics, partialTicks, x, y);
    }

    @Override
    public List<Rect2i> getExtraWindowBounds() {
        List<Rect2i> bounds = super.getExtraWindowBounds();
        WindowManager wm = getWindowManager();
        if (wm != null) {
            for (Window w : wm.getModalWindows().toList()) {
                if (w != null) {
                    Rectangle r = w.getToplevel().getBounds();
                    bounds.add(new Rect2i(r.x, r.y, r.width, r.height));
                }
            }
        }
        return bounds;
    }
}
