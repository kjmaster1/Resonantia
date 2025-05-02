package com.kjmaster.resonantia.modules.resonantenergy.client.gui;

import com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule;
import com.kjmaster.resonantia.modules.resonantenergy.blocks.transmitter.ResonantEnergyTransmitterTileEntity;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.jetbrains.annotations.NotNull;

import static com.kjmaster.resonantia.Resonantia.MODID;

public class GuiResonantEnergyReceiver extends GenericGuiContainer<ResonantEnergyTransmitterTileEntity, GenericContainer> {

    private EnergyBar energyBar;

    public GuiResonantEnergyReceiver(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, ResonantEnergyModule.SIMPLE_RESONANT_ENERGY_RECEIVER.block().get().getManualEntry());
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(ResonantEnergyModule.CONTAINER_RESONANT_ENERGY_RECEIVER.get(), GuiResonantEnergyReceiver::new);
    }

    @Override
    public void init() {
        window = new Window(this, this.getBE(), ResourceLocation.fromNamespaceAndPath(MODID, "gui/resonant_energy_receiver.gui"));
        super.init();
        initializeFields();
    }

    private void initializeFields() {
        energyBar = window.findChild("energybar");
        updateFields();
    }

    private void updateFields() {
        if (window == null) {
            return;
        }

        updateEnergyBar(energyBar);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
        updateFields();
        drawWindow(graphics, partialTicks, x, y);
    }
}
