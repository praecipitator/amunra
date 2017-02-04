package de.katzenpapst.amunra.mob.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelSentry extends ModelBase {

    ModelRenderer body;


    ModelRenderer leftarm;
    ModelRenderer rightarm;
    ModelRenderer toparm;
    ModelRenderer bottomarm;

    public ModelSentry() {
        /*
        // see net.minecraft.client.model.ModelGhast
        byte heightOffset = -16;
        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16); // 16³ box
        this.body.rotationPointY += (float)(24 + heightOffset); // why 24?
         */
        body = new ModelRenderer(this, 0, 0);
        body.addBox(-8F, -8F, -8F, 16, 16, 16);
        body.setRotationPoint(0F, 8F, 0F);
        body.setTextureSize(64, 32);
        body.mirror = true;

        float yOffset = -8F;

        leftarm = new ModelRenderer(this, 0, 0);
        leftarm.addBox(-1F, 0F, -1F, 2, 12, 2);
        leftarm.setRotationPoint(-9F, 8F+yOffset, -4F);
        leftarm.setTextureSize(64, 32);
        leftarm.mirror = true;
        setRotation(leftarm, 1.570796F, 0F, 0F);
        rightarm = new ModelRenderer(this, 0, 0);
        rightarm.addBox(-1F, 0F, -1F, 2, 12, 2);
        rightarm.setRotationPoint(9F, 8F+yOffset, -4F);
        rightarm.setTextureSize(64, 32);
        rightarm.mirror = true;
        setRotation(rightarm, 1.570796F, 0F, 0F);
        toparm = new ModelRenderer(this, 0, 0);
        toparm.addBox(-1F, 0F, -1F, 2, 12, 2);
        toparm.setRotationPoint(0F, -1F+yOffset, -4F);
        toparm.setTextureSize(64, 32);
        toparm.mirror = true;
        setRotation(toparm, 1.570796F, 0F, 0F);
        bottomarm = new ModelRenderer(this, 0, 0);
        bottomarm.addBox(-1F, 0F, -1F, 2, 12, 2);
        bottomarm.setRotationPoint(0F, 17F+yOffset, -4F);
        bottomarm.setTextureSize(64, 32);
        bottomarm.mirror = true;
        setRotation(bottomarm, 1.570796F, 0F, 0F);

        body.addChild(leftarm);
        body.addChild(rightarm);
        body.addChild(toparm);
        body.addChild(bottomarm);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }


    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    @Override
    public void setRotationAngles(float limbSwingTime, float linbSwingAmount, float somethingWhatever, float rotationY, float rotationX, float p_78087_6_, Entity p_78087_7_)
    {
        this.body.rotateAngleY = rotationY / (180F / (float)Math.PI);
        this.body.rotateAngleX = rotationX / (180F / (float)Math.PI);

        rightarm.rotateAngleY = (float) (0.2F * MathHelper.sin(somethingWhatever * 0.3F ) + 0.4F );
        leftarm.rotateAngleY = - (float) (0.2F * MathHelper.sin(somethingWhatever * 0.3F ) + 0.4F );
        toparm.rotateAngleX =  (float) (0.2 * MathHelper.sin(somethingWhatever * 0.3F ) + 0.4 + Math.PI / 2.0);
        bottomarm.rotateAngleX = - (float) (0.2 * MathHelper.sin(somethingWhatever * 0.3F ) + 0.4 - Math.PI / 2.0);


    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity entity, float limbSwingTime, float limbSwingAmplitude, float totalTimeMaybe, float rotationY, float rotationX, float someConstant)
    {
        this.setRotationAngles(limbSwingTime, limbSwingAmplitude, totalTimeMaybe, rotationY, rotationX, someConstant, entity);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.6F, 0.0F);
        this.body.render(someConstant);
        /*
        leftarm.render(someConstant);
        rightarm.render(someConstant);
        toparm.render(someConstant);
        bottomarm.render(someConstant);
*/
        GL11.glPopMatrix();
    }

}
