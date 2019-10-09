package com.logicmaster63.thermalambulation.item.upgrade;

import com.logicmaster63.thermalambulation.entity.EntityMachine;

public abstract class ProductionUpgrade extends ItemUpgrade {

    private int capacity;

    public ProductionUpgrade(String name, UpgradeType type, int capacity) {
        super(name, type);
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public abstract int getProduction(EntityMachine machine);
}
