package de.katzenpapst.amunra.client.renderer;

import org.lwjgl.opengl.GL11;

import team.chisel.ctmlib.Drawing;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.SubBlock;
import de.katzenpapst.amunra.block.ore.BlockOreMulti;

public class BlockRendererMultiOre implements ISimpleBlockRenderingHandler {


    public BlockRendererMultiOre() {
        AmunRa.multiOreRendererId = RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId,
            RenderBlocks renderer) {
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        // draw the background
        Drawing.drawBlock(block, metadata, renderer);

        // and then the overlay
        SubBlock sb = ((BlockOreMulti)block).getSubBlock(metadata);
        Drawing.drawBlock(sb, metadata, renderer);

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
            Block block, int modelId, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        SubBlock sb = ((BlockOreMulti)block).getSubBlock(meta);

        // block with the background texture
        renderer.renderStandardBlock(block, x, y, z);

        // block with the overlay
        renderer.renderStandardBlock(sb, x, y, z);

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return AmunRa.multiOreRendererId;
    }

}
