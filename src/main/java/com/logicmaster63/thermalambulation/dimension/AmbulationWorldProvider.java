package com.logicmaster63.thermalambulation.dimension;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class AmbulationWorldProvider extends WorldProvider {
    @Override
    public DimensionType getDimensionType() {
        return Dimensions.ambulationDimensionType;
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
