package com.logicmaster63.thermalambulation.entity;

import net.minecraft.client.model.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelWalker extends ModelBase {

    private final ModelRenderer Frame;
    private final ModelRenderer Leg1;
    private final ModelRenderer Leg2;
    private final ModelRenderer Leg3;
    private final ModelRenderer Leg4;

    public ModelWalker() {
        textureWidth = 32;
        textureHeight = 32;

        Frame = new ModelRenderer(this);
        Frame.setRotationPoint(0.0F, 24.0F, 0.0F);
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, -8.0F, -2.0F, -8.0F, 16, 2, 2, 0.0F, false));
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, -8.0F, -2.0F, 6.0F, 16, 2, 2, 0.0F, false));
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, -8.0F, -16.0F, 6.0F, 16, 2, 2, 0.0F, false));
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, -8.0F, -16.0F, -8.0F, 16, 2, 2, 0.0F, false));
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, -8.0F, -14.0F, 6.0F, 2, 12, 2, 0.0F, false));
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, -8.0F, -14.0F, -8.0F, 2, 12, 2, 0.0F, false));
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, 6.0F, -14.0F, -8.0F, 2, 12, 2, 0.0F, false));
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, 6.0F, -14.0F, 6.0F, 2, 12, 2, 0.0F, false));
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, -8.0F, -2.0F, -6.0F, 2, 2, 12, 0.0F, false));
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, -8.0F, -16.0F, -6.0F, 2, 2, 12, 0.0F, false));
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, 6.0F, -16.0F, -6.0F, 2, 2, 12, 0.0F, false));
        Frame.cubeList.add(new ModelBox(Frame, 0, 0, 6.0F, -2.0F, -6.0F, 2, 2, 12, 0.0F, false));

        Leg1 = new ModelRenderer(this);
        Leg1.setRotationPoint(0.0F, 24.0F, 0.0F);
        Leg1.cubeList.add(new ModelBox(Leg1, 0, 0, 8.0F, -3.0F, -10.0F, 2, 4, 4, 0.0F, false));

        Leg2 = new ModelRenderer(this);
        Leg2.setRotationPoint(0.0F, 24.0F, 0.0F);
        Leg2.cubeList.add(new ModelBox(Leg2, 0, 0, -10.0F, -3.0F, -10.0F, 2, 4, 4, 0.0F, false));

        Leg3 = new ModelRenderer(this);
        Leg3.setRotationPoint(0.0F, 24.0F, 0.0F);
        Leg3.cubeList.add(new ModelBox(Leg3, 0, 0, 8.0F, -3.0F, 6.0F, 2, 4, 4, 0.0F, false));

        Leg4 = new ModelRenderer(this);
        Leg4.setRotationPoint(0.0F, 24.0F, 0.0F);
        Leg4.cubeList.add(new ModelBox(Leg4, 0, 0, -10.0F, -3.0F, 6.0F, 2, 4, 4, 0.0F, false));
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Frame.render(f5);
        Leg1.render(f5);
        Leg2.render(f5);
        Leg3.render(f5);
        Leg4.render(f5);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        // Todo: add sitting animation or something
        this.Leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 0.4F * limbSwingAmount;
        this.Leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 0.4F * limbSwingAmount;
        this.Leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 0.4F * limbSwingAmount;
        this.Leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 0.4F * limbSwingAmount;
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
