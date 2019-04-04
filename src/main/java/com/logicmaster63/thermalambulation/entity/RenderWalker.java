package com.logicmaster63.thermalambulation.entity;

import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.util.ClientUtils;
import cofh.core.block.BlockCoreTile;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.render.BakeryMachine;
import cofh.thermalexpansion.render.CubeBakeryBase;
import cofh.thermalexpansion.render.RenderStrongbox;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class RenderWalker extends RenderLiving<EntityWalker> {

    public RenderWalker(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelWalker(), 0.5f);
    }

    @Override
    public void doRender(EntityWalker entity, double x, double y, double z, float yaw, float partialTicks) {
        //super.doRender(entity, x, y, z, yaw, partialTicks);
        ThermalAmbulation.logger.log(Level.INFO, x + "," + y + "," + z);
        if(entity.getMachine() != null)
            entity.getMachine().render(entity.posX, entity.posY, entity.posZ, yaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityWalker entity) {
        return new ResourceLocation(ThermalAmbulation.MOD_ID,"textures/entities/walker.png");
    }
}
