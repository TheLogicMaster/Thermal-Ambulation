package com.logicmaster63.thermalambulation.dimension;

import com.logicmaster63.thermalambulation.ThermalAmbulation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class Dimensions {
    public static DimensionType ambulationDimensionType;

    public static void init() {
        registerDimensionTypes();
        registerDimensions();
    }

    private static void registerDimensionTypes() {
        ambulationDimensionType = DimensionType.register(ThermalAmbulation.MOD_ID, "_test", 63, AmbulationWorldProvider.class, true);
    }

    private static void registerDimensions() {
        DimensionManager.registerDimension(63, ambulationDimensionType);
    }
}
