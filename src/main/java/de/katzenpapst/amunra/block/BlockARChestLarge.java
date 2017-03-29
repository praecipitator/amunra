package de.katzenpapst.amunra.block;

import de.katzenpapst.amunra.tile.TileEntityARChestLarge;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BlockARChestLarge extends BlockARChest {

    /*public BlockARChestLarge(
            Material material,
            String blockName,
            ResourceLocation smallChestTexture,
            ResourceLocation bigChestTexture,
            String fallbackTexture) {
        super(material, blockName, smallChestTexture, bigChestTexture, fallbackTexture);
    }*/

    public BlockARChestLarge(
            Material material,
            String blockName,
            ResourceLocation smallChestTexture,
            String fallbackTexture) {
        super(material, blockName, smallChestTexture, fallbackTexture);

        this.canDoublechest = false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityARChestLarge();
    }

}
