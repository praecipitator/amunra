package de.katzenpapst.amunra.client.renderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineBooster;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import team.chisel.ctmlib.Drawing;

public class BlockRendererMothershipBooster implements ISimpleBlockRenderingHandler {

    public BlockRendererMothershipBooster() {
        AmunRa.msBoosterRendererId = RenderingRegistry.getNextAvailableRenderId();

    }

    public static void renderTheDamnBlock(RenderBlocks var0, Block var1, int var2) {
        final Tessellator var3 = Tessellator.instance;

        var0.setRenderBounds(0F, 0F, 0F, 1F, 1F, 1F);
        var3.startDrawingQuads();
        var3.setNormal(0.0F, -0.8F, 0.0F);
        var0.renderFaceYNeg(var1, 0.0D, 0.0D, 0.0D, var1.getIcon(0, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0F, 0.8F, 0.0F);
        var0.renderFaceYPos(var1, 0.0D, 0.0D, 0.0D, var1.getIcon(1, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0F, 0.0F, -0.8F);
        var0.renderFaceXPos(var1, 0.0D, 0.0D, 0.0D, var1.getIcon(2, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.0F, 0.0F, 0.8F);
        var0.renderFaceXNeg(var1, 0.0D, 0.0D, 0.0D, var1.getIcon(3, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(-0.8F, 0.0F, 0.0F);
        var0.renderFaceZNeg(var1, 0.0D, 0.0D, 0.0D, var1.getIcon(4, var2));
        var3.draw();
        var3.startDrawingQuads();
        var3.setNormal(0.8F, 0.0F, 0.0F);
        var0.renderFaceZPos(var1, 0.0D, 0.0D, 0.0D, var1.getIcon(5, var2));
        var3.draw();
    }

    public static void renderInvNormalBlock(RenderBlocks var0, Block var1, int var2)
    {
        GL11.glRotatef(270, 0, 1, 0);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        renderTheDamnBlock(var0, var1, var2);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        renderInvNormalBlock(renderer, block, metadata);
    }

    @Override
    public boolean
            renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        // now this is kinda hacky
        //TileEntityMothershipEngineBooster myTile = (TileEntityMothershipEngineBooster) world.getTileEntity(x, y, z);
        // renderer.renderStandardBlock(block, x, y, z);
        // okay, I tried to make
        // int meta = world.getBlockMetadata(x, y, z);
        //renderTheDamnBlock(renderer, block, meta);
        // Drawing.drawBlock(block, meta, renderer);

        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return AmunRa.msBoosterRendererId;
    }

}
