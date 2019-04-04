package com.logicmaster63.thermalambulation.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderWalkerFactory implements IRenderFactory<EntityWalker> {

    public static final RenderWalkerFactory instance = new RenderWalkerFactory();

    @Override
    public Render<? super EntityWalker> createRenderFor(RenderManager manager) {
        return new RenderWalker(manager);
    }
}
