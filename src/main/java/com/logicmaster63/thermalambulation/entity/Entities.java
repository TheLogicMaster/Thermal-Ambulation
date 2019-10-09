package com.logicmaster63.thermalambulation.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import static com.logicmaster63.thermalambulation.ThermalAmbulation.instance;

public class Entities {

    private static int index = 0;

    public static void register() {
        ResourceLocation location = new ResourceLocation("thermalambulation", "walker");
        EntityRegistry.registerModEntity(location, EntityWalker.class, location.toString(), index++, instance, 80, 3, true, 0, 16777215);
    }

    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityWalker.class, RenderWalkerFactory.instance);
    }
}
