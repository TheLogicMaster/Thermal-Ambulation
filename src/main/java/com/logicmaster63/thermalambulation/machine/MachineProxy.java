package com.logicmaster63.thermalambulation.machine;

import codechicken.lib.model.bakery.ModelBakery;
import cofh.core.block.BlockCoreTile;
import com.logicmaster63.thermalambulation.RemoteMachineRegistry;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledByteBufAllocator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

public class MachineProxy implements IMachine {
    private int index;
    private transient TileEntity tileEntity;
    private transient World proxiedWorld;

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void init() {
        tileEntity = RemoteMachineRegistry.get().getRemoteTileEntity(index);
        proxiedWorld = DimensionManager.getWorld(63);
    }

    @Override
    public void destroy() {

    }

    @Override
    public String getType() {
        return "Proxy";
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        return false;
    }

    @Override
    public void render(double x, double y, double z, float yaw, float partialTicks) {
        ThermalAmbulation.logger.log(Level.INFO, "Render");
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.world;
        IBlockState state = proxiedWorld.getBlockState(tileEntity.getPos());
        if(state.getBlock() instanceof BlockCoreTile) {
            IBakedModel ibakedmodel = mc.getBlockRendererDispatcher().getModelForState(Block.getBlockFromName("minecraft:dirt").getDefaultState());
            if (proxiedWorld.getTileEntity(tileEntity.getPos()) != null)
                ibakedmodel = ModelBakery.generateModel((IExtendedBlockState) ((BlockCoreTile) state.getBlock()).getExtendedState(state, proxiedWorld, tileEntity.getPos()));

            Tessellator tessellator = Tessellator.getInstance();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableBlend();
            GlStateManager.disableCull();
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            RenderHelper.disableStandardItemLighting();
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            BufferBuilder worldRenderer = tessellator.getBuffer();
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            worldRenderer.setTranslation(-.5, -.5, -.5);

            mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, ibakedmodel, state, new BlockPos(0, 0, 0), tessellator.getBuffer(), true);

            worldRenderer.setTranslation(0, 0, 0);
            tessellator.draw();
            GlStateManager.popMatrix();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
        }
    }
}
