package com.logicmaster63.thermalambulation.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import static com.logicmaster63.thermalambulation.ThermalAmbulation.instance;

public class Entities {

    private static int index = 0;

    public static void register() {
        registerEntity("walker");
    }

    private static void registerEntity(String name) {
        ResourceLocation location = new ResourceLocation("thermalambulation", name);
        EntityRegistry.registerModEntity(location, EntityWalker.class, location.toString(), index++, instance, 64, 1, true, 0, 16777215);
    }

    public static void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityWalker.class, RenderWalkerFactory.instance);
    }
}
