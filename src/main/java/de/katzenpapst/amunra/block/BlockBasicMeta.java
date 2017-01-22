package de.katzenpapst.amunra.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.block.bush.SubBlockBush;
import de.katzenpapst.amunra.block.helper.BlockMassHelper;
import de.katzenpapst.amunra.item.ItemBlockMulti;
import micdoodle8.mods.galacticraft.api.block.IDetectableResource;
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock;
import micdoodle8.mods.galacticraft.api.block.IPlantableBlock;
import micdoodle8.mods.galacticraft.api.block.ITerraformableBlock;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBasicMeta extends Block implements IMetaBlock, IDetectableResource, IPlantableBlock, ITerraformableBlock, IMassiveBlock, IPartialSealableBlock {

    //protected ArrayList<SubBlock> subBlocks = null;
    protected SubBlock[] subBlocksArray;
    protected HashMap<String, Integer> nameMetaMap = null;

    /**
     * If true, the baseblock's name will be prefixed to the subblock's name for localisation
     */
    protected boolean prefixOwnBlockName = false;

    protected String blockNameFU;

    public BlockBasicMeta(String name, Material mat, int numSubBlocks) {
        super(mat);	// todo replace this
        subBlocksArray = new SubBlock[numSubBlocks];
        blockNameFU = name;
        // subBlocks = new ArrayList<SubBlock>(initialCapacity);
        nameMetaMap = new HashMap<String, Integer>();
        setBlockName(name);
    }

    public BlockBasicMeta setPrefixOwnBlockName(boolean set) {
        prefixOwnBlockName = set;
        return this;
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        if (!world.isRemote)
        {
            int metadata = world.getBlockMetadata(x, y, z);
            getSubBlock(metadata).updateTick(world, x, y, z, rand);
        }
    }

    public BlockBasicMeta(String name, Material mat) {
        this(name, mat, 16);
    }

    @Override
    public int getMetaByName(String name) {
        Integer i = nameMetaMap.get(name);
        if(i == null) {
            throw new IllegalArgumentException("Subblock "+name+" doesn't exist in "+blockNameFU);
        }
        return i;
    }

    @Override
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

    @Override
    public SubBlock getSubBlock(int meta) {
        meta = getDistinctionMeta(meta);
        return subBlocksArray[meta];
    }

    @Override
    public int getNumPossibleSubBlocks() {
        return subBlocksArray.length;
    }

    /**
     * Registers the block with the GameRegistry and sets the harvestlevels for all subblocks
     */
    @Override
    public void register() {
        GameRegistry.registerBlock(this, ItemBlockMulti.class, this.getUnlocalizedName());

        for(int i=0;i<subBlocksArray.length;i++) {
            SubBlock sb = subBlocksArray[i];
            if(sb != null) {

                this.setHarvestLevel(sb.getHarvestTool(0), sb.getHarvestLevel(0), i);
            }
        }
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
        return AmunRa.arTab;
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        int metadata = world.getBlockMetadata(x, y, z);

        return getSubBlock(metadata).getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);


        return getSubBlock(meta).getBlockHardness(world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        /*Face 0 (Bottom Face) 	Face 1 (Top Face) 	Face 2 (Northern Face) 	Face 3 (Southern Face) 	Face 4 (Western Face) 	Face 5 (Eastern Face)*/
        return getSubBlock(meta).getIcon(side, meta);
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

    @Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
        SubBlock sb = getSubBlock(meta);
        if(sb.dropsSelf()) {
            return 1;
        }
        return sb.quantityDropped(meta, fortune, random);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for(int i = 0; i < this.subBlocksArray.length; i++) {
            if(subBlocksArray[i] != null) {
                par3List.add(new ItemStack(par1, 1, i));
            }
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int meta)
    {
        SubBlock sb = getSubBlock(meta);
        return sb.createTileEntity(world, meta);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (getSubBlock(meta) != null)
        {
            return new ItemStack(Item.getItemFromBlock(this), 1, getDistinctionMeta(meta));
        }

        return super.getPickBlock(target, world, x, y, z);
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess par1World, int x, int y, int z)
    {
        int meta = par1World.getBlockMetadata(x, y, z);

        return this.getSubBlock(meta).getBlocksMovement(par1World, x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        int meta = world.getBlockMetadata(x, y, z);
        return this.getSubBlock(meta).onBlockActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune) {
        return this.getSubBlock(metadata).getExpDrop(world, 0, fortune);
    }

    @Override
    public boolean isTerraformable(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return this.getSubBlock(meta).isTerraformable(world, x, y, z);
    }

    @Override
    public int requiredLiquidBlocksNearby() {
        return 4; // I can't actually return the value of the subblock
    }

    @Override
    public boolean isPlantable(int metadata) {
        return getSubBlock(metadata).isPlantable(0);
    }

    @Override
    public boolean isValueable(int metadata) {
        return getSubBlock(metadata).isValueable(0);
    }

    @Override
    public Material getMaterial() {
        return this.blockMaterial;
    }

    @Override
    public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable)
    {
        Block block = plantable.getPlant(world, x, y + 1, z);
        int blockMeta = plantable.getPlantMetadata(world, x, y + 1, z);
        EnumPlantType plantType = plantable.getPlantType(world, x, y + 1, z);



        if (plantable instanceof SubBlockBush)
        {
            return ((SubBlockBush) plantable).canPlaceOn(block, blockMeta, 0);
        }

        return super.canSustainPlant(world, x, y, z, direction, plantable);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        return this.getSubBlock(meta).getLightValue();
    }

    /**
     * This returns a complete list of items dropped from this block.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param metadata Current metadata
     * @param fortune Breakers fortune level
     * @return A ArrayList containing all items this block drops
     */
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        SubBlock sb = this.getSubBlock(metadata);
        if(sb.dropsSelf()) {
            return super.getDrops(world, x, y, z, metadata, fortune);
        }
        return sb.getDrops(world, x, y, z, 0, fortune);

    }

    @Override
    public String getUnlocalizedSubBlockName(int meta) {
        if(prefixOwnBlockName) {
            return this.blockNameFU+"."+this.getSubBlock(meta).getUnlocalizedName();
        }
        return this.getSubBlock(meta).getUnlocalizedName();
    }

    @Override
    public int onBlockPlaced(World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
    {
        return this.getSubBlock(meta).onBlockPlaced(w, x, y, z, side, hitX, hitY, hitZ, meta);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    @Override
    public void onNeighborBlockChange(World w, int x, int y, int z, Block nb) {
        int meta = w.getBlockMetadata(x, y, z);
        this.getSubBlock(meta).onNeighborBlockChange(w, x, y, z, nb);
        super.onNeighborBlockChange(w, x, y, z, nb);
    }

    @Override
    public float getMass(World w, int x, int y, int z, int meta) {
        SubBlock sb = this.getSubBlock(meta);
        if(!(sb instanceof IMassiveBlock)) {
            return BlockMassHelper.guessBlockMass(w, sb, meta, x, y, z);
        }
        return ((IMassiveBlock)sb).getMass(w, x, y, z, meta);
    }

    @Override
    public boolean isSealed(World world, int x, int y, int z, ForgeDirection direction) {
        int meta = world.getBlockMetadata(x, y, z);
        SubBlock sb = this.getSubBlock(meta);
        if(sb instanceof IPartialSealableBlock) {
            return ((IPartialSealableBlock)sb).isSealed(world, x, y, z, direction);
        }

        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block b, int meta)
    {
        this.getSubBlock(meta).breakBlock(world, x, y, z, b, meta);
    }
}
