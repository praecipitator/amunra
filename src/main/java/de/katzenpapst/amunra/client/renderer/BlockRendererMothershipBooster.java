package de.katzenpapst.amunra.client.renderer;
/*
import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.machine.mothershipEngine.BlockMothershipBoosterMeta;
import de.katzenpapst.amunra.block.machine.mothershipEngine.MothershipEngineBoosterBase;
import de.katzenpapst.amunra.client.BlockRenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

public class BlockRendererMothershipBooster implements ISimpleBlockRenderingHandler {

    public BlockRendererMothershipBooster() {
        AmunRa.msBoosterRendererId = RenderingRegistry.getNextAvailableRenderId();
    }



    public void renderBooster(ResourceLocation loc) {
        final Tessellator tess = Tessellator.instance;

        GL11.glRotatef(270, 0, 1, 0);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Minecraft.getMinecraft().renderEngine.bindTexture(loc);

        BlockRenderHelper.renderFaceYNeg(tess, 0, 0.5, 0.25, 0.75, false);
        BlockRenderHelper.renderFaceYPos(tess, 0, 0.5, 0.25, 0.75, false);


        BlockRenderHelper.renderFaceZNeg(tess, 0, 0, 0.25, 0.25);
        BlockRenderHelper.renderFaceZPos(tess, 0, 0, 0.25, 0.25);
        BlockRenderHelper.renderFaceXNeg(tess, 0, 0, 0.25, 0.25);
        BlockRenderHelper.renderFaceXPos(tess, 0, 0, 0.25, 0.25);
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        if(block instanceof BlockMothershipBoosterMeta) {
            MothershipEngineBoosterBase sb = (MothershipEngineBoosterBase) ((BlockMothershipBoosterMeta)block).getSubBlock(metadata);
            ResourceLocation texture = sb.getBoosterTexture();
            renderBooster(texture);
        }
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        // this happens in the tileentity

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
*/