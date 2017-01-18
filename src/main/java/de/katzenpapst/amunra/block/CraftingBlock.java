package de.katzenpapst.amunra.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class CraftingBlock extends SubBlock {

    @SideOnly(Side.CLIENT)
    protected IIcon blockIconBottom;
    @SideOnly(Side.CLIENT)
    protected IIcon blockIconSide;

    public CraftingBlock(String name) {
        //super
        super(name, "amunra:crafter", "pickaxe", 1, 5.0F, 5.0F);
        this.setStepSound(Block.soundTypeMetal);

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        blockIcon = par1IconRegister.registerIcon(getTextureName());
        blockIconSide = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_side");
        blockIconBottom = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine");

    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        switch(side) {
        case 0:
            return blockIconBottom;
        case 1:
            return blockIcon;
        default:
            return blockIconSide;
        }
    }

    /**
     *
     *
     * @param world The World Object.
     * @param x     , y, z The coordinate of the block.
     * @param side  The side the player clicked on.
     * @param hitX  , hitY, hitZ The position the player clicked on relative to
     *              the block.
     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {

        //onBlockActivated

        if (world.isRemote)
        {
            return true;
        }
        else
        {
            entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_CRAFTING, world, x, y, z);
            return true;
        }
    }

}
// blockRegistry.addObject(58, "crafting_table", (new BlockWorkbench()).setHardness(2.5F).setStepSound(soundTypeWood).setBlockName("workbench").setBlockTextureName("crafting_table"));