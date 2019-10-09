package com.logicmaster63.thermalambulation.item.upgrade;

import cofh.redstoneflux.impl.EnergyStorage;

public class ConsumptionUpgrade extends ItemUpgrade {
    private EnergyStorage energyStorage;

    public ConsumptionUpgrade(String name, UpgradeType type, int capacity) {
        super(name, type);
        energyStorage = new EnergyStorage(capacity);
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}
