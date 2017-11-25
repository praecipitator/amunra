package de.katzenpapst.amunra.client.renderer;
/*
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import de.katzenpapst.amunra.block.BlockARChest;
import de.katzenpapst.amunra.tile.TileEntityARChest;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class RenderARChest extends TileEntitySpecialRenderer {

//    private static final ResourceLocation treasureChestTexture = new ResourceLocation(MarsModule.ASSET_PREFIX, "textures/model/treasure.png");
  //  private static final ResourceLocation treasureLargeChestTexture = new ResourceLocation(MarsModule.ASSET_PREFIX, "textures/model/treasurelarge.png");

    private final ModelChest chestModel = new ModelChest();
    private final ModelLargeChest largeChestModel = new ModelLargeChest();

    public RenderARChest() {
        // TODO Auto-generated constructor stub
    }

    public void renderARChestAt(TileEntityARChest chest, double x, double par4, double par6, float par8)
    {
        int chestMetadata;

        final Block chestBlock = chest.getBlockType();
        if(!(chestBlock instanceof BlockARChest)) {
            return;
        }

        if (!chest.hasWorldObj())
        {
            chestMetadata = 0;
        }
        else
        {
            chestMetadata = chest.getBlockMetadata();

            if (chestBlock != null && chestMetadata == 0)
            {
                ((BlockARChest) chestBlock).unifyAdjacentChests(chest.getWorldObj(), chest.xCoord, chest.yCoord, chest.zCoord);
                chestMetadata = chest.getBlockMetadata();
            }

            chest.checkForAdjacentChests();
        }

        if (chest.adjacentChestZNeg == null && chest.adjacentChestXNeg == null)
        {
            ModelChest smallModel = null;
            ModelLargeChest largeModel = null;

            if (chest.adjacentChestXPos == null && chest.adjacentChestZPos == null)
            {
                smallModel = this.chestModel;
                this.bindTexture(((BlockARChest)chestBlock).getSmallTexture());
            }
            else
            {
                largeModel = this.largeChestModel;
                this.bindTexture(((BlockARChest)chestBlock).getLargeTexture());
            }

            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((float) x, (float) par4 + 1.0F, (float) par6 + 1.0F);
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
            short var11 = 0;

            if (chestMetadata == 2)
            {
                var11 = 180;
            }

            if (chestMetadata == 3)
            {
                var11 = 0;
            }

            if (chestMetadata == 4)
            {
                var11 = 90;
            }

            if (chestMetadata == 5)
            {
                var11 = -90;
            }

            if (chestMetadata == 2 && chest.adjacentChestXPos != null)
            {
                GL11.glTranslatef(1.0F, 0.0F, 0.0F);
            }

            if (chestMetadata == 5 && chest.adjacentChestZPos != null)
            {
                GL11.glTranslatef(0.0F, 0.0F, -1.0F);
            }

            GL11.glRotatef(var11, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            float var12 = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * par8;

            float var13;

            if (chest.adjacentChestZNeg != null)
            {
                var13 = chest.adjacentChestZNeg.prevLidAngle + (chest.adjacentChestZNeg.lidAngle - chest.adjacentChestZNeg.prevLidAngle) * par8;

                if (var13 > var12)
                {
                    var12 = var13;
                }
            }

            if (chest.adjacentChestXNeg != null)
            {
                var13 = chest.adjacentChestXNeg.prevLidAngle + (chest.adjacentChestXNeg.lidAngle - chest.adjacentChestXNeg.prevLidAngle) * par8;

                if (var13 > var12)
                {
                    var12 = var13;
                }
            }

            var12 = 1.0F - var12;
            var12 = 1.0F - var12 * var12 * var12;

            if (smallModel != null)
            {
                smallModel.chestLid.rotateAngleX = -(var12 * (float) Math.PI / 4.0F);
                smallModel.renderAll();
            }

            if (largeModel != null)
            {
                largeModel.chestLid.rotateAngleX = -(var12 * (float) Math.PI / 4.0F);
                largeModel.renderAll();
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void renderTileEntityAt(
            TileEntity tileEntity,
            double x,
            double y,
            double z,
            float ticks) {

        this.renderARChestAt((TileEntityARChest)tileEntity, x, y, z, ticks);

    }

}
*/