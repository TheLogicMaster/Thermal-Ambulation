package com.logicmaster63.thermalambulation.dimension;

import com.logicmaster63.thermalambulation.ThermalAmbulation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class AmbulationWorldProvider extends WorldProvider {
    @Override
    public DimensionType getDimensionType() {
        return Dimensions.ambulationDimensionType;
    }

    @Override
    public boolean canDropChunk(int x, int z) {
        return super.canDropChunk(x, z);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public String getSaveFolder() {
        return "TEST";
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new AmbulationChunkGenerator(world);
    }
}
