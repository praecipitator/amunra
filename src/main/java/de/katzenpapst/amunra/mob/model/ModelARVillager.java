package de.katzenpapst.amunra.mob.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelARVillager extends ModelVillager
{
    // public ModelRenderer brain;

    public ModelRenderer antenna1;
    public ModelRenderer antenna2;

    protected ModelRenderer[] tailBoxes = new ModelRenderer[4];

    private static final int[][] tailBoxPositions = { {32, 48}, {32, 8}, {32, 0}, {56, 16} };
    private static final int[] tailBoxSizes = { 8, 6, 4, 2 };
    private static final float[] offsets = { -2.0F, 1.5F, 4.0F, 6.0F };

    public ModelARVillager(float par1)
    {
        this(par1, 0.0F, 64, 64);
    }

    public ModelARVillager(float scaleOrSo, float par2, int textureX, int textureY)
    {
        super(scaleOrSo, par2, 0, 0);

        this.villagerHead = new ModelRenderer(this).setTextureSize(textureX, textureY);
        this.villagerHead.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
        this.villagerHead.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, scaleOrSo + 0.001F);
        this.villagerNose = new ModelRenderer(this).setTextureSize(textureX, textureY);
        this.villagerNose.setRotationPoint(0.0F, par2 - 2.0F, 0.0F);
        this.villagerNose.setTextureOffset(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, scaleOrSo + 0.002F);
        this.villagerHead.addChild(this.villagerNose);
        this.villagerBody = new ModelRenderer(this).setTextureSize(textureX, textureY);
        this.villagerBody.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
        this.villagerBody.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, scaleOrSo + 0.003F);
        this.villagerBody.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, scaleOrSo + 0.5F + 0.004F);
        this.villagerArms = new ModelRenderer(this).setTextureSize(textureX, textureY);
        this.villagerArms.setRotationPoint(0.0F, 0.0F + par2 + 2.0F, 0.0F);
        this.villagerArms.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, scaleOrSo + 0.005F);
        this.villagerArms.setTextureOffset(44, 22).addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, scaleOrSo + 0.0001F);
        this.villagerArms.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, scaleOrSo + 0.0004F);
        this.rightVillagerLeg = new ModelRenderer(this, 0, 22).setTextureSize(textureX, textureY);
        this.rightVillagerLeg.setRotationPoint(-2.0F, 12.0F + par2, 0.0F);
        this.rightVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scaleOrSo + 0.0006F);
        this.leftVillagerLeg = new ModelRenderer(this, 0, 22).setTextureSize(textureX, textureY);
        this.leftVillagerLeg.mirror = true;
        this.leftVillagerLeg.setRotationPoint(2.0F, 12.0F + par2, 0.0F);
        this.leftVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scaleOrSo + 0.0002F);
        //this.brain = new ModelRenderer(this).setTextureSize(par3, par4);
       // this.brain.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
       // this.brain.setTextureOffset(32, 0).addBox(-4.0F, -16.0F, -4.0F, 8, 8, 8, par1 + 0.5F);




        float antennaOffset = -6.0F;

        antenna1 = new ModelRenderer(this).setTextureSize(textureX, textureY);
        antenna1.setRotationPoint(0.5F, antennaOffset + par2, 0.0F); // +1
        antenna1.setTextureOffset(54, 6).addBox(4.0F, -0.5F, -0.5F, 4, 1, 1, scaleOrSo + 0.5F);

        ModelRenderer antennaTip1 = new ModelRenderer(this).setTextureSize(textureX, textureY);
        antennaTip1.setRotationPoint(8.0F, 0.0F, 0.0F);
        antennaTip1.setTextureOffset(56, 8).addBox(0.0F, -1.5F, -1.5F, 1, 3, 3, scaleOrSo + 0.5F);
        antennaTip1.rotateAngleX = (float) Math.PI / 4;
        antenna1.addChild(antennaTip1);
        antenna1.rotateAngleZ = (float) -Math.PI / 4;



        antenna2 = new ModelRenderer(this).setTextureSize(textureX, textureY);
        antenna2.setRotationPoint(-0.5F, antennaOffset + par2, 0.0F); // -1
        antenna2.setTextureOffset(54, 6).addBox(4.0F, -0.5F, -0.5F, 4, 1, 1, scaleOrSo + 0.5F);

        ModelRenderer antennaTip2 = new ModelRenderer(this).setTextureSize(textureX, textureY);
        antennaTip2.setRotationPoint(8.0F, 0.0F, 0.0F);
        antennaTip2.setTextureOffset(56, 8).addBox(0.0F, -1.5F, -1.5F, 1, 3, 3, scaleOrSo + 0.5F);
        antennaTip2.rotateAngleX = (float) Math.PI / 4;
        antenna2.addChild(antennaTip2);
        antenna2.rotateAngleY = (float) Math.PI;
        antenna2.rotateAngleZ = (float) Math.PI / 4;

        // OMG
        //tailBoxes[0] = new ModelRenderer(this).setTextureSize(textureX, textureY);
        // tailBoxes[0]

        // try to do a tail
        // float curOffset = 0;
        for(int i=0;i<tailBoxes.length;i++) {
            tailBoxes[i] = new ModelRenderer(this).setTextureSize(textureX, textureY);
            int[] curData = tailBoxPositions[i];
            int curSize = tailBoxSizes[i];
            float halfSize = curSize / 2.0F;
            float curPos = offsets[i];
            tailBoxes[i].setRotationPoint(0.0F, 12.0F + par2, curPos-2);
            /*
            if(i < tailBoxes.length-1) {
                curOffset += (tailBoxSizes[i+1] * 2);
            }
            if(i > 0) {
                curOffset += (tailBoxSizes[i-1]);
            }
             */
            // curOffset += (tailBoxSizes[i]);
            tailBoxes[i].setTextureOffset(curData[0], curData[1]).addBox(
                    -halfSize,
                    -curSize + 12.0F,
                    curPos,
                    curSize, curSize, curSize);
            //curOffset += (2 + curSize);
            //tailBoxes[i].s
        }



        villagerHead.addChild(antenna1);
        villagerHead.addChild(antenna2);

    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    @Override
    public void setRotationAngles(float limbSwingTime, float limbSwingAmount, float totalTimeMaybe, float rotationY, float rotationX, float someConstant, Entity theEntity)
    {
        this.villagerHead.rotateAngleY = rotationY / (180F / (float)Math.PI);
        this.villagerHead.rotateAngleX = rotationX / (180F / (float)Math.PI);
        this.villagerArms.rotationPointY = 3.0F;
        this.villagerArms.rotationPointZ = -1.0F;
        this.villagerArms.rotateAngleX = -0.75F;

        float test = MathHelper.cos(limbSwingTime * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;

        this.rightVillagerLeg.rotateAngleX = test;
        this.leftVillagerLeg.rotateAngleX = MathHelper.cos(limbSwingTime * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.rightVillagerLeg.rotateAngleY = 0.0F;
        this.leftVillagerLeg.rotateAngleY = 0.0F;


        for(int i=0;i<tailBoxes.length;i++) {
            // float val = MathHelper.sin(totalTimeMaybe * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.2F * (float)Math.abs(i - 2);
  //          float val = MathHelper.cos(limbSwingTime * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
//            float val2 = this.leftVillagerLeg.rotateAngleX = MathHelper.cos(limbSwingTime * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
            float curPos = offsets[i];
            tailBoxes[i].rotationPointZ = curPos*test*2 + 5*test - 2;
        }

    }

    @Override
    public void render(Entity curEntity, float limbSwingTime, float limbSwingAmplitude, float totalTimeMaybe, float rotationY, float totationX, float someConstant)
    {
        this.setRotationAngles(limbSwingTime, limbSwingAmplitude, totalTimeMaybe, rotationY, totationX, someConstant, curEntity);
        this.villagerHead.render(someConstant);
        this.villagerBody.render(someConstant);
        //this.rightVillagerLeg.render(someConstant);
        //this.leftVillagerLeg.render(someConstant);
        this.villagerArms.render(someConstant);

        for(int i=0;i<tailBoxes.length;i++) {
            tailBoxes[i].render(someConstant);
        }

    }


}