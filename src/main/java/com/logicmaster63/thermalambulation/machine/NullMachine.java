package com.logicmaster63.thermalambulation.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;

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
    public ArrayList<ItemStack> destroy() {
        return null;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        return false;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("Type", MachineType.NULL.name());
        return nbt;
    }
}
