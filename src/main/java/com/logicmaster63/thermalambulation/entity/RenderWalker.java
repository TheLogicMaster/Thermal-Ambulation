package com.logicmaster63.thermalambulation.entity;

import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.block.BlockRenderingRegistry;
import codechicken.lib.util.ClientUtils;
import cofh.core.block.BlockCoreTile;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.render.BakeryMachine;
import cofh.thermalexpansion.render.CubeBakeryBase;
import cofh.thermalexpansion.render.RenderStrongbox;
import com.logicmaster63.thermalambulation.RemoteMachineRegistry;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderCow;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
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
        //super(rendermanagerIn, new ModelCow(), 0.7F);
        super(rendermanagerIn, new ModelWalker(), 0.3f);
    }

    @Override
    public void doRender(EntityWalker entity, double x, double y, double z, float yaw, float partialTicks) {
        if(entity.getMachine() != null)
            entity.getMachine().render(x, y, z, yaw, partialTicks);
        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityWalker entity) {
        //return new ResourceLocation("textures/entity/cow/cow.png");
        return new ResourceLocation(ThermalAmbulation.MOD_ID,"textures/entities/walker.png");
    }
}
