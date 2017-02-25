package de.katzenpapst.amunra.mob.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelFirstBoss extends ModelBiped {

    //protected ModelRenderer frontmask;
    protected ModelRenderer headTopPart;
    protected ModelRenderer headSidePart1;
    protected ModelRenderer headSidePart2;
    protected ModelRenderer headTopPart2;
    protected ModelRenderer beard;

    public ModelFirstBoss() {
        //super(0.0F);
        super(0.0F, 0.0F, 64, 64);

        // other stuff
        addHelmet();
    }

    /*public ModelFirstBoss(float scaleOrSo) {
        super(scaleOrSo);
    }

    public ModelFirstBoss(float scaleOrSo, float someYoffset, int textureX, int textureY) {
        super(scaleOrSo, someYoffset, textureX, textureY);
    }*/

    protected void addHelmet() {
        textureWidth = 64;
        textureHeight = 64;

        headTopPart = new ModelRenderer(this, 0, 32);
        headSidePart1 = new ModelRenderer(this, 24, 32);
        headSidePart2 = new ModelRenderer(this, 24, 32);
        headTopPart2 = new ModelRenderer(this, 0, 40);
        beard = new ModelRenderer(this, 38, 32);

        headTopPart.addBox(-4F, -2F, -2F, 8, 4, 4);
        headTopPart.setRotationPoint(0F, -8.5F, -1F);
        headTopPart.setTextureSize(64, 64);
        headTopPart.mirror = true;
        setRotation(headTopPart, -0.5948578F, 0F, 0F);

        headSidePart1.addBox(-3F, -1F, -1F, 5, 11, 2);
        headSidePart1.setRotationPoint(-3F, -8F, 0F);
        headSidePart1.setTextureSize(64, 64);
        headSidePart1.mirror = true;
        setRotation(headSidePart1, 0F, 0F, 0.2974289F);
        headSidePart2.mirror = true;

        headSidePart2.addBox(-2F, -1F, -1F, 5, 11, 2);
        headSidePart2.setRotationPoint(3F, -8F, 0F);
        headSidePart2.setTextureSize(64, 64);
        headSidePart2.mirror = true;
        setRotation(headSidePart2, 0F, 0F, -0.2974216F);
        headSidePart2.mirror = false;

        headTopPart2.addBox(-5F, -1F, -1F, 10, 2, 2);
        headTopPart2.setRotationPoint(0F, -9F, 0.1F);
        headTopPart2.setTextureSize(64, 64);
        headTopPart2.mirror = true;
        setRotation(headTopPart2, 0F, 0F, 0F);

        beard.addBox(-1F, -1F, -0.5F, 2, 8, 1);
        beard.setRotationPoint(0F, 0F, -4F);
        beard.setTextureSize(64, 64);
        beard.mirror = true;
        setRotation(beard, -0.669215F, 0F, 0F);

        this.bipedHead.addChild(headTopPart);
        this.bipedHead.addChild(headTopPart2);
        this.bipedHead.addChild(headSidePart1);
        this.bipedHead.addChild(headSidePart2);
        this.bipedHead.addChild(beard);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
