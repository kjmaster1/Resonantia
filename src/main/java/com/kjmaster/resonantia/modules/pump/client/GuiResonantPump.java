package com.kjmaster.resonantia.modules.pump.client;

import com.kjmaster.resonantia.client.gui.GuiModularResonatingMachine;
import com.kjmaster.resonantia.data.Mode;
import com.kjmaster.resonantia.modules.pump.PumpModule;
import com.kjmaster.resonantia.modules.pump.blocks.ResonantPumpTE;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.ManualEntry;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class GuiResonantPump extends GuiModularResonatingMachine<ResonantPumpTE, GenericContainer> {

    public GuiResonantPump(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, ManualEntry.EMPTY);
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(PumpModule.CONTAINER_RESONANT_PUMP.get(), GuiResonantPump::new);
    }

    @Override
    protected Mode[] getFluidModes() {
        return new Mode[]{Mode.MODE_NONE, Mode.MODE_OUTPUT};
    }

    @Override
    protected Direction[] getFluidDirections() {
        return new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    }

    @Override
    protected Direction[] getEnergyDirections() {
        return new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    }

    @Override
    protected Direction[] getItemDirections() {
        return new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    }
}