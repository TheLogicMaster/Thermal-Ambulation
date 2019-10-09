package com.logicmaster63.thermalambulation.entity;

import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.item.upgrade.ItemUpgrade;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderWalker extends RenderLiving<EntityWalker> {

    public RenderWalker(RenderManager rendermanagerIn) {
        //super(rendermanagerIn, new ModelCow(), 0.7F);
        super(rendermanagerIn, new ModelWalker(), 0.3f);
    }

    @Override
    public void doRender(EntityWalker entity, double x, double y, double z, float yaw, float partialTicks) {
        float realYaw = interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
        if(entity.getMachine() != null)
            entity.getMachine().render(x, y, z, realYaw, partialTicks);

        for (ItemStack upgrade: entity.getUpgradeItems())
            ((ItemUpgrade) upgrade.getItem()).render(entity, x, y, z, realYaw, partialTicks);

        super.doRender(entity, x, y, z, yaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityWalker entity) {
        //return new ResourceLocation("textures/entity/cow/cow.png");
        return new ResourceLocation(ThermalAmbulation.MOD_ID,"textures/entities/walker.png");
    }
}
