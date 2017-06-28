package de.katzenpapst.amunra.block.machine.mothershipEngine;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class MothershipEngineJetRocket extends MothershipEngineJetBase {


    protected ItemDamagePair item = null;

    public MothershipEngineJetRocket(String name, String texture, String iconTexture) {
        super(name, texture, iconTexture);
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        super.registerBlockIcons(par1IconRegister);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return this.blockIcon;
    }


    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityMothershipEngineJet();
    }


    @Override
    protected ItemDamagePair getItem() {
        if(item == null) {
            item = ARItems.jetItem;
        }
        return item;
    }

}
