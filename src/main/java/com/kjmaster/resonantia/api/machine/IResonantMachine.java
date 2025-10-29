package com.kjmaster.resonantia.api.machine;

import com.kjmaster.resonantia.api.frequency.DriftContext;

public interface IResonantMachine {
    ResonantMachineRole getMachineRole();
    boolean isUnstable();
    int getDriftInterval();
    int getLinkTolerance();
    int getDestabilizationTolerance();
    int getRadius();
    int calculateDrift(DriftContext driftContext);
}
