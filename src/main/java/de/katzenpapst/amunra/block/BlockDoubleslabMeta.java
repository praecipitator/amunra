package de.katzenpapst.amunra.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.item.ItemSlabMulti;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockDoubleslabMeta extends BlockBasicMeta {

    protected final BlockSlabMeta slabMetablock;

    /**
     * I think this has to match the Slabs, meta-wise
     * @param name
     * @param mat
     */
    public BlockDoubleslabMeta(String name, Material mat, BlockSlabMeta slabMetablock) {
        super(name, mat, 8);
        this.slabMetablock = slabMetablock;
        slabMetablock.setDoubleslabMeta(this);
    }


    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        //int meta = world.getBlockMetadata(x, y, z);
        return slabMetablock.getPickBlock(target, world, x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

        int count = quantityDropped(metadata, fortune, world.rand);
        for(int i = 0; i < count; i++)
        {
            Item item = getItemDropped(metadata, world.rand, fortune);
            if (item != null)
            {
                ret.add(new ItemStack(item, 1, damageDropped(metadata)));
            }
        }
        return ret;

    }

    @Override
    public String getUnlocalizedSubBlockName(int meta) {
        return this.getSubBlock(meta).getUnlocalizedName()+".slab";
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
        return slabMetablock.getItemDropped(meta, random, fortune);
        // return Item.getItemFromBlock(slabMetablock);
    }

    @Override
    public int damageDropped(int meta)
    {
        return getDistinctionMeta(meta);
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z)
    {
        return getDistinctionMeta( world.getBlockMetadata(x, y, z) );
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
        return 2;
    }

    @Override
    public BlockMetaPair addSubBlock(int meta, SubBlock sb) {

        if(meta >= subBlocksArray.length || meta < 0) {
            throw new IllegalArgumentException("Meta "+meta+" must be <= "+(subBlocksArray.length-1)+" && >= 0");
        }

        if(subBlocksArray[meta] != null) {
            throw new IllegalArgumentException("Meta "+meta+" is already in use");
        }

        if(nameMetaMap.get(sb.getUnlocalizedName()) != null) {
            throw new IllegalArgumentException("Name "+sb.getUnlocalizedName()+" is already in use");
        }
        // sb.setParent(this);
        nameMetaMap.put(sb.getUnlocalizedName(), meta);
        subBlocksArray[meta] = sb;
        return new BlockMetaPair(this, (byte) meta);
    }

    public BlockMetaPair addSubBlock(int meta) {

        // find the basedOn block
        SubBlock sb = slabMetablock.getSubBlock(meta);

        return addSubBlock(meta, sb);
    }

    @Override
    public int getMetaByName(String name) {
        Integer i = nameMetaMap.get(name);
        if(i == null) {
            throw new IllegalArgumentException("Subblock "+name+" doesn't exist");
        }
        return i;
    }

    @Override
    public SubBlock getSubBlock(int meta) {

        return subBlocksArray[getDistinctionMeta(meta)];
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return getSubBlock(meta).getIcon(side, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        for(SubBlock sb: subBlocksArray) {
            if(sb != null) {
                sb.registerBlockIcons(par1IconRegister);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return null;
    }

    @Override
    public void register() {
        // try to checking what the slabMetablock has
        // slabMetablock.getAllSubBlocks()
        for(int i=0;i<slabMetablock.getAllSubBlocks().length;i++) {
            SubBlock sb = slabMetablock.getSubBlock(i);
            if(sb != null) {
                addSubBlock(i, sb);
            }
        }
        GameRegistry.registerBlock(this, ItemSlabMulti.class, this.getUnlocalizedName(), (Block)slabMetablock, (Block)this);

        for(int i=0;i<subBlocksArray.length;i++) {
            SubBlock sb = subBlocksArray[i];
            if(sb != null) {
                this.setHarvestLevel(sb.getHarvestTool(0), sb.getHarvestLevel(0), i);
            }
        }
    }
}
