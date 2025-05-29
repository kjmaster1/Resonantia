package com.kjmaster.resonantia.client.gui.widgets;

import com.kjmaster.resonantia.data.Mode;
import com.kjmaster.resonantia.tileentity.ModularResonatingMachineTE;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.WindowManager;
import mcjty.lib.typed.TypedMap;
import net.minecraft.core.Direction;

public class ItemSideSelector extends AbstractSideSelector<ItemSideSelector> {

    private final ModularResonatingMachineTE te;

    public ItemSideSelector(Mode[] choices, Direction[] directions, WindowManager mainWindowManager, Window mainWindow, ModularResonatingMachineTE te) {
        super(mainWindowManager, mainWindow, choices, directions);
        this.te = te;
    }

    @Override
    protected String getCurrentMode(Direction direction) {
        return te.getItemMode(direction).getSerializedName();
    }

    @Override
    protected void sendServerCommand(String modeString, Direction direction) {
        Mode mode = Mode.getMode(modeString);
        mainWindow.sendServerCommand(ModularResonatingMachineTE.ACTION_SET_ITEM_MODE,
                TypedMap.builder()
                        .put(ModularResonatingMachineTE.PARAM_DIRECTION, direction.ordinal())
                        .put(ModularResonatingMachineTE.PARAM_ITEM_MODE, mode.ordinal())
                        .build());
    }

    @Override
    protected String getLabelPrefix() {
        return "item";
    }

    @Override
    protected String getTooltipBase(Direction direction) {
        return direction.name().charAt(0) + direction.name().substring(1).toLowerCase() + " Item";
    }
}

