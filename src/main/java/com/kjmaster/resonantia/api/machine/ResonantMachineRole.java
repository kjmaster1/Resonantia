package com.kjmaster.resonantia.api.machine;

public enum ResonantMachineRole {
    TRANSMITTER,
    RECEIVER,
    PROCESSOR,
    PASSIVE;

    public boolean isCompatibleWith(ResonantMachineRole other) {
        return (this == TRANSMITTER || other == TRANSMITTER) && (this != other);
    }
}
