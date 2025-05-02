package com.kjmaster.resonantia.api.machine;

public enum ResonantMachineRole {
    TRANSMITTER,
    RECEIVER,
    PROCESSOR,
    PASSIVE;

    public boolean isCompatibleWith(ResonantMachineRole other) {
        if (this == TRANSMITTER && other == RECEIVER) return true;
        if (this == TRANSMITTER && other == PROCESSOR) return true;
        if (this == TRANSMITTER && other == PASSIVE) return true;
        if (this == RECEIVER && other == TRANSMITTER) return true;
        if (this == PROCESSOR && other == TRANSMITTER) return true;
        if (this == PASSIVE && other == TRANSMITTER) return true;
        return false;
    }
}
