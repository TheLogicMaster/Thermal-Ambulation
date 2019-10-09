package com.logicmaster63.thermalambulation.item.upgrade;

import com.logicmaster63.thermalambulation.entity.EntityWalker;
import com.logicmaster63.thermalambulation.item.ItemBase;

public class ItemUpgrade extends ItemBase {

    private UpgradeType type;

    public ItemUpgrade(String name, UpgradeType type) {
        super(name);
        this.type = type;
    }

    public UpgradeType getUpgradeType() {
        return type;
    }

    public void render(EntityWalker entity, double x, double y, double z, float yaw, float partialTicks) {

    }

    public enum UpgradeType {
        SOLAR(0), WEAPON(1), CAPACITOR(0);

        int priority;

        public int getPriority() {
            return priority;
        }

        UpgradeType(int priority) {
            this.priority = priority;
        }
    }
}
