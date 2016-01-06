package de.katzenpapst.amunra.client.renderer;

import org.lwjgl.opengl.GL11;

import team.chisel.ctmlib.Drawing;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.ore.BlockOreMulti;

public class RendererMultiOre implements ISimpleBlockRenderingHandler {


	public RendererMultiOre() {
		AmunRa.multiOreRendererId = RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		// draw the background
		Drawing.drawBlock(block, ((BlockOreMulti)block).getActualBlockIcon(), renderer);


		// and then the overlay
		Drawing.drawBlock(block, metadata, renderer);

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		renderer.setOverrideBlockTexture(((BlockOreMulti)block).getActualBlockIcon());
		renderer.renderStandardBlock(block, x, y, z);
		renderer.clearOverrideBlockTexture();
		renderer.renderStandardBlock(block, x, y, z);
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
