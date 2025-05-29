package com.kjmaster.resonantia.modules.resonantenergy.client.gui;

import com.kjmaster.resonantia.client.gui.GuiModularResonatingMachine;
import com.kjmaster.resonantia.data.Mode;
import com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule;
import com.kjmaster.resonantia.modules.resonantenergy.blocks.receiver.ResonantEnergyReceiverTileEntity;
import mcjty.lib.container.GenericContainer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class GuiResonantEnergyReceiver extends GuiModularResonatingMachine<ResonantEnergyReceiverTileEntity, GenericContainer> {

    public GuiResonantEnergyReceiver(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, ResonantEnergyModule.SIMPLE_RESONANT_ENERGY_RECEIVER.block().get().getManualEntry());
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(ResonantEnergyModule.CONTAINER_RESONANT_ENERGY_RECEIVER.get(), GuiResonantEnergyReceiver::new);
    }

    @Override
    protected Mode[] getEnergyModes() {
        return new Mode[]{Mode.MODE_NONE, Mode.MODE_OUTPUT};
    }

    @Override
    protected Direction[] getItemDirections() {
        return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    }

    @Override
    protected Direction[] getEnergyDirections() {
        return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    }
}
