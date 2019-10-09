package com.logicmaster63.thermalambulation.machine;

import cofh.redstoneflux.api.IEnergyStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import java.io.Serializable;
import java.util.ArrayList;

public interface IMachine extends IEnergyStorage {

    String getType();

    void init();

    void render(double x, double y, double z, float yaw, float partialTicks);

    ArrayList<ItemStack> destroy();

    boolean processInteract(EntityPlayer player, EnumHand hand);

    void readFromNBT(NBTTagCompound nbt);

    NBTTagCompound toNBT();
}
