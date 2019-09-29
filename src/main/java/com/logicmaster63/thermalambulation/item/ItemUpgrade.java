package com.logicmaster63.thermalambulation.item;

public class ItemUpgrade extends ItemBase {

    private UpgradeType type;

    public ItemUpgrade(String name, UpgradeType type) {
        super(name);
        this.type = type;
    }

    public UpgradeType getType() {
        return type;
    }

    public enum UpgradeType {
        SOLAR
    }
}
