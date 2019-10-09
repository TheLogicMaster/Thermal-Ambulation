package com.logicmaster63.thermalambulation.entity;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.world.World;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

//Todo: migrate common code here upon creating different machine entities
public abstract class EntityMachine extends EntityTameable {

    public EntityMachine(World worldIn) {
        super(worldIn);
    }
}
