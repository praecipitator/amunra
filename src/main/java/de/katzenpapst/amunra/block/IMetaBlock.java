package de.katzenpapst.amunra.block;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;

public interface IMetaBlock {
    /**
     *
     * @param meta
     * @param sb
     * @return
     */
    public BlockMetaPair addSubBlock(int meta, SubBlock sb);
    /*
     *
    public BlockMetaPair addSubBlock(int meta, SubBlock sb) {
        if(meta >= subBlocksArray.length || meta < 0) {
            throw new IllegalArgumentException("Meta "+meta+" must be <= "+(subBlocksArray.length-1)+" && >= 0");
        }
        if(subBlocksArray[meta] != null) {
            throw new IllegalArgumentException("Meta "+meta+" is already in use in "+blockNameFU);
        }
        if(nameMetaMap.get(sb.getUnlocalizedName()) != null) {
            throw new IllegalArgumentException("Name "+sb.getUnlocalizedName()+" is already in use in "+blockNameFU);
        }
        sb.setParent(this);
        nameMetaMap.put(sb.getUnlocalizedName(), meta);
        subBlocksArray[meta] = sb;
        return new BlockMetaPair(this, (byte) meta);
    }
     */

    public int getMetaByName(String name);
    /*
    public int getMetaByName(String name) {
        Integer i = nameMetaMap.get(name);
        if(i == null) {
            throw new IllegalArgumentException("Subblock "+name+" doesn't exist in "+blockNameFU);
        }
        return i;
    }
    */

    public SubBlock getSubBlock(int meta);
    /*
    public SubBlock getSubBlock(int meta) {
        meta = getDistinctionMeta(meta);
        return subBlocksArray[meta];
    }
     */

    public String getUnlocalizedSubBlockName(int meta);
    /*
    public String getUnlocalizedSubBlockName(int meta) {
        if(prefixOwnBlockName) {
            return this.blockNameFU+"."+this.getSubBlock(meta).getUnlocalizedName();
        }
        return this.getSubBlock(meta).getUnlocalizedName();
    }
     */

    public void register();
    /*
    public void register() {
        GameRegistry.registerBlock(this, ItemBlockMulti.class, this.getUnlocalizedName());

        for(int i=0;i<subBlocksArray.length;i++) {
            SubBlock sb = subBlocksArray[i];
            if(sb != null) {

                this.setHarvestLevel(sb.getHarvestTool(0), sb.getHarvestLevel(0), i);
            }
        }
    }
     */

    /**
     * This should return subBlocksArray.length
     * @return
     */
    public int getNumPossibleSubBlocks();

    /**
     * This should take a metadata, and return only the part of it which is used for subblock distinction,
     * aka, strip off things like rotational information
     *
     * @param meta
     * @return
     */
    default public int getDistinctionMeta(int meta) {
        int numSubBlocks = this.getNumPossibleSubBlocks();
        if(numSubBlocks < 4) {
            return meta & 1;
        }
        if(numSubBlocks < 8) {
            return meta & 3;
        }
        if(numSubBlocks < 16) {
            return meta & 7;
        }
        return meta;
    }

    /**
     * Gets the rotation meta, downshifted if needed
     * @param meta
     * @return
     */
    default public int getRotationMeta(int meta) {
        return (meta & 12) >> 2;
    }

    /**
     *
     * @param name
     * @return
     */
    default public BlockMetaPair getBlockMetaPair(String name) {
        return new BlockMetaPair((Block) this, (byte) getMetaByName(name));
    }

    /**
     * Adds rotationmeta to some other metadata
     * @param baseMeta
     * @param rotationMeta
     * @return
     */
    default public int addRotationMeta(int baseMeta, int rotationMeta) {
        return baseMeta | (rotationMeta << 2);
    }

    /////////////////////// COPYPASTE THIS INTO IMPLEMENTATIONS ////////////////////////////////////////
    /*
     *
    @Override
    public void onNeighborBlockChange(World w, int x, int y, int z, Block nb) {
        int meta = w.getBlockMetadata(x, y, z);
        this.getSubBlock(meta).onNeighborBlockChange(w, x, y, z, nb);
        super.onNeighborBlockChange(w, x, y, z, nb);
    }
    @Override
    public int onBlockPlaced(World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
    {
        return this.getSubBlock(meta).onBlockPlaced(w, x, y, z, side, hitX, hitY, hitZ, meta);
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
        return this.getSubBlock(metadata).getExpDrop(world, 0, fortune);
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
        SubBlock sb = getSubBlock(meta);
        if(sb.dropsSelf()) {
            return 1;
        }
        return sb.quantityDropped(meta, fortune, random);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
        SubBlock sb = getSubBlock(meta);


        if(sb.dropsSelf()) {
            return Item.getItemFromBlock(this);
        }
        return sb.getItemDropped(0, random, fortune);
    }

    @Override
    public int damageDropped(int meta)
    {
        SubBlock sb = getSubBlock(meta);
        if(sb.dropsSelf()) {
            return getDistinctionMeta(meta);
        }
        return sb.damageDropped(0);
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z)
    {
        return getDistinctionMeta( world.getBlockMetadata(x, y, z) );
    }

     */
}
