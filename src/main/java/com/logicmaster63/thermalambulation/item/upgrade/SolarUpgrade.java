package com.logicmaster63.thermalambulation.item.upgrade;

import codechicken.lib.model.bakery.ModelBakery;
import cofh.core.block.BlockCoreTile;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.entity.EntityMachine;
import com.logicmaster63.thermalambulation.entity.EntityWalker;
import com.logicmaster63.thermalambulation.machine.AmbulationUtils;
import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.api.SolarInstance;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.Optional;
import org.lwjgl.opengl.GL11;

public class SolarUpgrade extends ProductionUpgrade {

    SolarInfo solar;

    public SolarUpgrade(SolarInfo solar) {
        super("solarupgrade." + solar.getRegistryName(), UpgradeType.SOLAR, 0);
        this.solar = solar;
    }

    @Override
    public int getProduction(EntityMachine machine) {
        BlockPos pos = new BlockPos(machine.posX, machine.posY, machine.posZ);
        float intensity;
        if (machine.world.getLightFor(EnumSkyBlock.SKY, pos) <= 0 || !machine.world.canBlockSeeSky(pos))
            intensity = 0;
        else {
            float celestialAngleRadians = machine.world.getCelestialAngleRadians(1F);
            if (celestialAngleRadians > Math.PI)
                celestialAngleRadians = (float) (2 * Math.PI - celestialAngleRadians);
            int lowLightCount = 0;
            float multiplicator = 1.5F - (lowLightCount * .122F);
            float displacement = 1.2F + (lowLightCount * .08F);

            intensity = MathHelper.clamp(multiplicator * MathHelper.cos(celestialAngleRadians / displacement), 0, 1);
        }
        return (int) (solar.maxGeneration * intensity);
    }

    @Override
    public void render(EntityWalker entity, double x, double y, double z, float yaw, float partialTicks) {
        AmbulationUtils.renderBlock(x, y + 1, z, yaw, solar.getBlock());
    }
}
