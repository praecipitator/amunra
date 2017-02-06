package de.katzenpapst.amunra.mob.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBug extends ModelBase {

    ModelRenderer body;
    ModelRenderer head;
    ModelRenderer tail1;
    ModelRenderer lefthand2;
    ModelRenderer lefthand1;
    ModelRenderer leftarm;
    ModelRenderer righthand2;
    ModelRenderer righthand1;
    ModelRenderer rightarm;
    ModelRenderer legLeft3;
    ModelRenderer legLeft2;
    ModelRenderer legLeft1;
    ModelRenderer legRight3;
    ModelRenderer legRight2;
    ModelRenderer legRight1;

    public ModelBug() {
        textureWidth = 64;
        textureHeight = 64;

        body = new ModelRenderer(this, 0, 0);
        body.addBox(-7F, -4F, -7F, 14, 8, 14);
        body.setRotationPoint(0F, 0F, 0F);
        body.setTextureSize(64, 64);
        body.mirror = true;
        setRotation(body, 0F, 0F, 0F);

        head = new ModelRenderer(this, 0, 22);
        head.addBox(-3F, -3F, -6F, 6, 6, 6);
        head.setRotationPoint(0F, 0F, -7F);
        head.setTextureSize(64, 64);
        head.mirror = true;
        setRotation(head, 0F, 0F, 0F);

        tail1 = new ModelRenderer(this, 32, 22);
        tail1.addBox(-4F, -3F, 0F, 8, 4, 3);
        tail1.setRotationPoint(0F, 1F, 7F);
        tail1.setTextureSize(64, 64);
        tail1.mirror = true;
        setRotation(tail1, 0F, 0F, 0F);

        // left claw
        lefthand2 = new ModelRenderer(this, 0, 0);
        lefthand2.mirror = true;
        lefthand2.addBox(0F, -1F, -4F, 1, 2, 4);
        lefthand2.setRotationPoint(0F, 8F, 0F);
        lefthand2.setTextureSize(64, 64);
        lefthand2.mirror = true;
        setRotation(lefthand2, 1.570796F, 0F, 0F);
        lefthand2.mirror = false;

        lefthand1 = new ModelRenderer(this, 0, 0);
        lefthand1.addBox(-1F, -1F, -4F, 1, 2, 4);
        lefthand1.setRotationPoint(0F, 8F, 0F);
        lefthand1.setTextureSize(64, 64);
        lefthand1.mirror = true;
        setRotation(lefthand1, 1.570796F, 0F, 0F);

        leftarm = new ModelRenderer(this, 24, 22);
        leftarm.addBox(-1F, 0F, -1F, 2, 8, 2);
        leftarm.setRotationPoint(-5F, 0F, -7F);
        leftarm.setTextureSize(64, 64);
        leftarm.mirror = true;
        //setRotation(leftarm, -1.570796F, 0F, 0F);
        setRotation(leftarm, -1.570796F, 0.5F, 0F);
        leftarm.addChild(lefthand1);
        leftarm.addChild(lefthand2);

        // right claw
        righthand2 = new ModelRenderer(this, 0, 0);
        righthand2.addBox(-1F, -1F, -4F, 1, 2, 4);
        righthand2.setRotationPoint(0F, 8F, 0F);
        righthand2.setTextureSize(64, 64);
        righthand2.mirror = true;
        setRotation(righthand2, 1.570796F, 0F, 0F);

        righthand1 = new ModelRenderer(this, 0, 0);
        righthand1.mirror = true;
        righthand1.addBox(0F, -1F, -4F, 1, 2, 4);
        righthand1.setRotationPoint(0F, 8F, 0F);
        righthand1.setTextureSize(64, 64);
        righthand1.mirror = true;
        setRotation(righthand1, 1.570796F, 0F, 0F);
        righthand1.mirror = false;

        rightarm = new ModelRenderer(this, 24, 22);
        rightarm.mirror = true;
        rightarm.addBox(-1F, 0F, -1F, 2, 8, 2);
        rightarm.setRotationPoint(5F, 0F, -7F);
        rightarm.setTextureSize(64, 64);
        rightarm.mirror = true;
        //setRotation(rightarm, -1.570796F, 0F, 0F);
        setRotation(rightarm, -1.570796F, -0.5F, 0F);
        rightarm.mirror = false;
        rightarm.addChild(righthand1);
        rightarm.addChild(righthand2);

        // left legs
        legLeft3 = new ModelRenderer(this, 42, 0);
        legLeft3.addBox(-1F, 0F, -1F, 2, 10, 2);
        legLeft3.setRotationPoint(-7F, 1F, 4F);
        legLeft3.setTextureSize(64, 64);
        legLeft3.mirror = true;
        //setRotation(legLeft3, 0F, 0F, 1.134464F);
        setRotation(legLeft3, 0.3F, 0F, 1.134464F);

        legLeft2 = new ModelRenderer(this, 42, 0);
        legLeft2.addBox(-1F, 0F, -1F, 2, 10, 2);
        legLeft2.setRotationPoint(-7F, 1F, 0F);
        legLeft2.setTextureSize(64, 64);
        legLeft2.mirror = true;
        setRotation(legLeft2, 0F, 0F, 1.134464F);

        legLeft1 = new ModelRenderer(this, 42, 0);
        legLeft1.addBox(-1F, 0F, -1F, 2, 10, 2);
        legLeft1.setRotationPoint(-7F, 1F, -4F);
        legLeft1.setTextureSize(64, 64);
        legLeft1.mirror = true;
        //setRotation(legLeft1, 0F, 0F, 1.134464F);
        setRotation(legLeft1, -0.3F, 0F, 1.134464F);

        // right legs
        legRight3 = new ModelRenderer(this, 42, 0);
        legRight3.mirror = true;
        legRight3.addBox(-1F, 0F, -1F, 2, 10, 2);
        legRight3.setRotationPoint(7F, 1F, 4F);
        legRight3.setTextureSize(64, 64);
        legRight3.mirror = true;
        //setRotation(legRight3, 0F, 0F, -1.134464F);
        setRotation(legRight3, 0.3F, 0F, -1.134464F);
        legRight3.mirror = false;

        legRight2 = new ModelRenderer(this, 42, 0);
        legRight2.mirror = true;
        legRight2.addBox(-1F, 0F, -1F, 2, 10, 2);
        legRight2.setRotationPoint(7F, 1F, 0F);
        legRight2.setTextureSize(64, 64);
        legRight2.mirror = true;
        setRotation(legRight2, 0F, 0F, -1.134464F);
        legRight2.mirror = false;

        legRight1 = new ModelRenderer(this, 42, 0);
        legRight1.mirror = true;
        legRight1.addBox(-1F, 0F, -1F, 2, 10, 2);
        legRight1.setRotationPoint(7F, 1F, -4F);
        legRight1.setTextureSize(64, 64);
        legRight1.mirror = true;
        //setRotation(legRight1, 0F, 0F, -1.134464F);
        setRotation(legRight1, -0.3F, 0F, -1.134464F);
        legRight1.mirror = false;
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        body.render(f5);
        head.render(f5);
        tail1.render(f5);
        //lefthand2.render(f5);
        //lefthand1.render(f5);
        leftarm.render(f5);
        //righthand2.render(f5);
        //righthand1.render(f5);
        rightarm.render(f5);
        legLeft3.render(f5);
        legLeft2.render(f5);
        legLeft1.render(f5);
        legRight3.render(f5);
        legRight2.render(f5);
        legRight1.render(f5);
    }

    @Override
    // public void setRotationAngles(float time, float walkSpeed, float appendageRotation, float rotationYaw, float rotationPitch, float scale, Entity entity)
    public void setRotationAngles(float time, float walkSpeed, float appendageRotation, float rotationYaw, float rotationPitch, float cale, Entity entity)
    {
        this.head.rotateAngleY = rotationYaw / (180F / (float)Math.PI);
        this.head.rotateAngleX = rotationPitch / (180F / (float)Math.PI);

        // reset to default
        setRotation(legLeft1, -0.3F, 0F, 1.134464F);
        setRotation(legLeft2, 0F, 0F, 1.134464F);
        setRotation(legLeft3, 0.3F, 0F, 1.134464F);

        setRotation(legRight1, -0.3F, 0F, -1.134464F);
        setRotation(legRight2, 0F, 0F, -1.134464F);
        setRotation(legRight3, 0.3F, 0F, -1.134464F);

        setRotation(leftarm, -1.570796F, 0.5F, 0F);
        setRotation(rightarm, -1.570796F, -0.5F, 0F);


        setRotation(leftarm, -1.570796F, 0.5F, 0F);
        //setRotation(lefthand1, -0.3F, 0F, 1.134464F);
        setRotation(lefthand1, 1.570796F, 0F, 0F);
        setRotation(lefthand2, 1.570796F, 0F, 0F);

        setRotation(rightarm, -1.570796F, -0.5F, 0F);
        setRotation(righthand1, 1.570796F, 0F, 0F);
        setRotation(righthand2, 1.570796F, 0F, 0F);

        // now rotate

        float leg1Y = -(MathHelper.cos(time * 0.6662F * 2.0F + 0.0F) * 0.4F) * walkSpeed;
        float leg3Y = -(MathHelper.cos(time * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * walkSpeed;
        //float leg6Y = -(MathHelper.cos(time * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * walkSpeed;
        float leg7Y = -(MathHelper.cos(time * 0.6662F * 2.0F + ((float)Math.PI * 3F / 2F)) * 0.4F) * walkSpeed;

        float leg1Z = Math.abs(MathHelper.sin(time * 0.6662F + 0.0F) * 0.4F) * walkSpeed;
        float leg3Z = Math.abs(MathHelper.sin(time * 0.6662F + (float)Math.PI) * 0.4F) * walkSpeed;
        //float leg5Z = Math.abs(MathHelper.sin(time * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * walkSpeed;
        float leg7Z = Math.abs(MathHelper.sin(time * 0.6662F + ((float)Math.PI * 3F / 2F)) * 0.4F) * walkSpeed;

        this.legLeft1.rotateAngleY  += leg1Y;
        this.legRight1.rotateAngleY -= leg1Y;
        this.legLeft1.rotateAngleX  += leg1Z;
        this.legRight1.rotateAngleX -= leg1Z;

        this.legLeft2.rotateAngleY  += leg3Y;
        this.legRight2.rotateAngleY -= leg3Y;
        this.legLeft2.rotateAngleX  += leg3Z;
        this.legRight2.rotateAngleX -= leg3Z;

        this.legLeft3.rotateAngleY  += leg7Y;
        this.legRight3.rotateAngleY -= leg7Y;
        this.legLeft3.rotateAngleX  += leg7Z;
        this.legRight3.rotateAngleX -= leg7Z;

        rightarm.rotateAngleZ += MathHelper.cos(appendageRotation * 0.09F) * 0.05F + 0.05F;
        rightarm.rotateAngleX += MathHelper.sin(appendageRotation * 0.067F) * 0.05F;
        righthand1.rotateAngleZ += MathHelper.cos(appendageRotation * 0.5F) * 0.5F - 0.5F;
        righthand2.rotateAngleZ -= MathHelper.cos(appendageRotation * 0.5F) * 0.5F - 0.5F;

        leftarm.rotateAngleZ -= MathHelper.cos(appendageRotation * 0.09F) * 0.05F + 0.05F;
        leftarm.rotateAngleX -= MathHelper.sin(appendageRotation * 0.067F) * 0.05F;
        lefthand1.rotateAngleZ -= MathHelper.cos(appendageRotation * 0.5F) * 0.5F - 0.5F;
        lefthand2.rotateAngleZ += MathHelper.cos(appendageRotation * 0.5F) * 0.5F - 0.5F;
        /*
        this.spiderLeg1.rotateAngleY += leg1Y;
        this.spiderLeg2.rotateAngleY += -leg1Y;
        this.spiderLeg3.rotateAngleY += leg3Y;
        this.spiderLeg4.rotateAngleY += -leg3Y;
        this.spiderLeg5.rotateAngleY += leg6Y;
        this.spiderLeg6.rotateAngleY += -leg6Y;
        this.spiderLeg7.rotateAngleY += leg7Y;
        this.spiderLeg8.rotateAngleY += -leg7Y;

        this.spiderLeg1.rotateAngleZ += leg1Z;
        this.spiderLeg2.rotateAngleZ += -leg1Z;
        this.spiderLeg3.rotateAngleZ += leg3Z;
        this.spiderLeg4.rotateAngleZ += -leg3Z;
        this.spiderLeg5.rotateAngleZ += leg5Z;
        this.spiderLeg6.rotateAngleZ += -leg5Z;
        this.spiderLeg7.rotateAngleZ += leg7Z;
        this.spiderLeg8.rotateAngleZ += -leg7Z;
        */

        //this.legLeft1.rotateAngleZ = f / (180F / (float)Math.PI);

        super.setRotationAngles(time, walkSpeed, appendageRotation, rotationYaw, rotationPitch, cale, entity);
    }
}
