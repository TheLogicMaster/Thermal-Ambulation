package com.logicmaster63.thermalambulation.machine;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import java.io.Serializable;

public interface IMachine extends Serializable {

    String getType();

    void init();

    void render(double x, double y, double z, float yaw, float partialTicks);

    void destroy();

    boolean processInteract(EntityPlayer player, EnumHand hand);

}
