package com.kjmaster.resonantia.capabilities.item;

import com.kjmaster.resonantia.data.Mode;
import net.minecraft.core.Direction;

public interface ISidedItemTE {
    Mode getItemMode(Direction side);
}
