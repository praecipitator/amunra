package de.katzenpapst.amunra.mob.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelVillager;

public class ModelARVillager extends ModelVillager
{
    public ModelRenderer brain;

    public ModelRenderer antenna1;
    public ModelRenderer antenna2;

    public ModelARVillager(float par1)
    {
        this(par1, 0.0F, 64, 64);
    }

    public ModelARVillager(float par1, float par2, int par3, int par4)
    {
        super(par1, par2, 0, 0);

        this.villagerHead = new ModelRenderer(this).setTextureSize(par3, par4);
        this.villagerHead.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
        this.villagerHead.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, par1 + 0.001F);
        this.villagerNose = new ModelRenderer(this).setTextureSize(par3, par4);
        this.villagerNose.setRotationPoint(0.0F, par2 - 2.0F, 0.0F);
        this.villagerNose.setTextureOffset(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, par1 + 0.002F);
        this.villagerHead.addChild(this.villagerNose);
        this.villagerBody = new ModelRenderer(this).setTextureSize(par3, par4);
        this.villagerBody.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
        this.villagerBody.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, par1 + 0.003F);
        this.villagerBody.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, par1 + 0.5F + 0.004F);
        this.villagerArms = new ModelRenderer(this).setTextureSize(par3, par4);
        this.villagerArms.setRotationPoint(0.0F, 0.0F + par2 + 2.0F, 0.0F);
        this.villagerArms.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, par1 + 0.005F);
        this.villagerArms.setTextureOffset(44, 22).addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, par1 + 0.0001F);
        this.villagerArms.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, par1 + 0.0004F);
        this.rightVillagerLeg = new ModelRenderer(this, 0, 22).setTextureSize(par3, par4);
        this.rightVillagerLeg.setRotationPoint(-2.0F, 12.0F + par2, 0.0F);
        this.rightVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, par1 + 0.0006F);
        this.leftVillagerLeg = new ModelRenderer(this, 0, 22).setTextureSize(par3, par4);
        this.leftVillagerLeg.mirror = true;
        this.leftVillagerLeg.setRotationPoint(2.0F, 12.0F + par2, 0.0F);
        this.leftVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, par1 + 0.0002F);
        //this.brain = new ModelRenderer(this).setTextureSize(par3, par4);
       // this.brain.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
       // this.brain.setTextureOffset(32, 0).addBox(-4.0F, -16.0F, -4.0F, 8, 8, 8, par1 + 0.5F);




        float antennaOffset = -6.0F;

        antenna1 = new ModelRenderer(this).setTextureSize(par3, par4);
        antenna1.setRotationPoint(0.5F, antennaOffset + par2, 0.0F);
        antenna1.setTextureOffset(32, 48).addBox(4.0F, -0.5F, -0.5F, 4, 1, 1, par1 + 0.5F);



        ModelRenderer antennaTip1 = new ModelRenderer(this).setTextureSize(par3, par4);
        antennaTip1.setRotationPoint(8.0F, 0.0F, 0.0F);
        antennaTip1.setTextureOffset(32, 56).addBox(0.0F, -1.5F, -1.5F, 1, 3, 3, par1 + 0.5F);
        antennaTip1.rotateAngleX = (float) Math.PI / 4;
        antenna1.addChild(antennaTip1);
        antenna1.rotateAngleZ = (float) -Math.PI / 4;

        ModelRenderer antennaTip2 = new ModelRenderer(this).setTextureSize(par3, par4);
        antennaTip2.setRotationPoint(8.0F, 0.0F, 0.0F);
        antennaTip2.setTextureOffset(32, 56).addBox(0.0F, -1.5F, -1.5F, 1, 3, 3, par1 + 0.5F);
        antennaTip2.rotateAngleX = (float) Math.PI / 4;

        antenna2 = new ModelRenderer(this).setTextureSize(par3, par4);
        antenna2.setRotationPoint(-0.5F, antennaOffset + par2, 0.0F);
        antenna2.setTextureOffset(32, 48).addBox(4.0F, -0.5F, -0.5F, 4, 1, 1, par1 + 0.5F);
        antenna2.addChild(antennaTip2);
        antenna2.rotateAngleY = (float) Math.PI;
        antenna2.rotateAngleZ = (float) Math.PI / 4;




        villagerHead.addChild(antenna1);
        villagerHead.addChild(antenna2);

    }

}