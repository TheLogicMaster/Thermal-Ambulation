package com.logicmaster63.thermalambulation.machine;

import com.logicmaster63.thermalambulation.ThermalAmbulation;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.lang.reflect.Field;

public final class AmbulationUtils {

    public static IMachine machineFromNBT(NBTTagCompound nbt) {
        IMachine machine;
        switch (MachineType.valueOf(nbt.getString("Type"))) {
            case PROXY:
                machine = new MachineProxy();
                break;
            case NULL:
            default:
                machine = new NullMachine();
        }
        machine.readFromNBT(nbt);
        return machine;
    }

    public static void renderBlock(double x, double y, double z, float yaw, Block block) {
        Minecraft mc = Minecraft.getMinecraft();
        IBlockState state = block.getBlockState().getBaseState();
        IBakedModel ibakedmodel = mc.getBlockRendererDispatcher().getModelForState(state);
        GlStateManager.pushMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        // Todo: figure out a way to enable scaling without different translations
        //GlStateManager.translate(x - 0.4, y + 1, z - 0.4);
        //GlStateManager.scale(0.8, 0.8, 0.8);
        //GlStateManager.translate(.5, .5, .5);
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-yaw + 90, 0, 1, 0);
        GlStateManager.translate(-.5, 0, -.5);

        RenderHelper.disableStandardItemLighting();
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModel(mc.world, ibakedmodel, state, new BlockPos(0, 0, 0), tessellator.getBuffer(), true);
        worldRenderer.setTranslation(0, 0, 0);
        tessellator.draw();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    public static Object getPrivateValue(Object obj, @SuppressWarnings("rawtypes") Class c, String field) {
        return getPrivateValue(obj, c, new String[] {field});
    }

    public static Object getPrivateValue(Object obj, @SuppressWarnings("rawtypes") Class c, String[] fields)
    {
        for(String field : fields)
        {
            try {
                Field f = c.getDeclaredField(field);
                f.setAccessible(true);
                return f.get(obj);
            } catch(Exception e) {

            }
        }

        return null;
    }
}
