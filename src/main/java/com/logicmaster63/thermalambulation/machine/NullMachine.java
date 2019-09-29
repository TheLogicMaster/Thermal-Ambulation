package com.logicmaster63.thermalambulation.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class NullMachine implements IMachine {

    @Override
    public String getType() {
        return "Null";
    }

    @Override
    public void init() {

    }

    @Override
    public void render(double x, double y, double z, float yaw, float partialTicks) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        return false;
    }
}
