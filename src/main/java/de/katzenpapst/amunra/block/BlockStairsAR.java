package de.katzenpapst.amunra.block;

import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.helper.BlockMassHelper;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairsAR extends BlockStairs implements IMassiveBlock {

    BlockMetaPair sourceBlock;

    public BlockStairsAR(BlockMetaPair sourceBlock) {
        // protected constructor? WTF IS THIS SHIT?!!?!
        super(sourceBlock.getBlock(), sourceBlock.getMetadata());
        this.sourceBlock = sourceBlock;
    }


    @Override
    public String getUnlocalizedName()
    {
        IMetaBlock mBlock = (IMetaBlock)this.sourceBlock.getBlock();
        if(mBlock != null) {
            return "tile."+mBlock.getSubBlock(sourceBlock.getMetadata()).getUnlocalizedName()+".stairs";
        }
        return "tile."+this.sourceBlock.getBlock().getUnlocalizedName()+".stairs";
    }

    public void register() {
        GameRegistry.registerBlock(this, ItemBlock.class, this.getUnlocalizedName());


        this.setHarvestLevel(
                sourceBlock.getBlock().getHarvestTool(sourceBlock.getMetadata()),
                sourceBlock.getBlock().getHarvestLevel(sourceBlock.getMetadata())
                );

    }

    @SideOnly(Side.CLIENT)
    @Override
    public CreativeTabs getCreativeTabToDisplayOn()
    {
        return AmunRa.arTab;
    }

    @Override
    public float getMass(World w, int x, int y, int z, int meta) {
        float parentMass = BlockMassHelper.getBlockMass(w, sourceBlock.getBlock(), sourceBlock.getMetadata(), x, y, z);
        // 4/6 = 2/3, because stairs
        return parentMass*2.0F/3.0F;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z)
    {
        return getSourceBlock().getBlockHardness(world, x, y, z);
    }

    public Block getSourceBlock()
    {
        Block mainBlock = sourceBlock.getBlock();
        if(mainBlock instanceof IMetaBlock) {
            mainBlock = ((IMetaBlock)sourceBlock.getBlock()).getSubBlock(sourceBlock.getMetadata());
        }
        return mainBlock;
    }

    /**
     * Queries the class of tool required to harvest this block, if null is returned
     * we assume that anything can harvest this block.
     *
     * @param metadata
     * @return
     */
    @Override
    public String getHarvestTool(int metadata)
    {
        return sourceBlock.getBlock().getHarvestTool(metadata);
    }

    /**
     * Queries the harvest level of this item stack for the specifred tool class,
     * Returns -1 if this tool is not of the specified type
     *
     * @param stack This item stack instance
     * @return Harvest level, or -1 if not the specified tool type.
     */
    @Override
    public int getHarvestLevel(int metadata)
    {
        return sourceBlock.getBlock().getHarvestLevel(metadata);
    }

    /**
     * Checks if the specified tool type is efficient on this block,
     * meaning that it digs at full speed.
     *
     * @param type
     * @param metadata
     * @return
     */
    @Override
    public boolean isToolEffective(String type, int metadata)
    {
        return this.getHarvestTool(metadata).equals(type);
    }

    /**
     * Location sensitive version of getExplosionRestance
     *
     * @param par1Entity The entity that caused the explosion
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param explosionX Explosion source X Position
     * @param explosionY Explosion source X Position
     * @param explosionZ Explosion source X Position
     * @return The amount of the explosion absorbed.
     */
    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        return getSourceBlock().getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }


    /// block-dependent functions
    /**
     * Called when a player hits the block. Args: world, x, y, z, player
     */
    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player)
    {
        // I don't see any reason for this to be proxied to the source block, but meh
        getSourceBlock().onBlockClicked(world, x, y, z, player);
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {

        getSourceBlock().randomDisplayTick(world, x, y, z, rand);
    }

    /**
     * Called right before the block is destroyed by a player.  Args: world, x, y, z, metaData
     */
    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata)
    {
        getSourceBlock().onBlockDestroyedByPlayer(world, x, y, z, metadata);
    }

    /**
     * Returns how much this block can resist explosions from the passed in entity.
     */
    @Override
    public float getExplosionResistance(Entity ent)
    {
        return getSourceBlock().getExplosionResistance(ent);
    }

    /**
     * How many world ticks before ticking
     */
    @Override
    public int tickRate(World world)
    {
        return getSourceBlock().tickRate(world);
    }

    /**
     * Can add to the passed in vector for a movement vector to be applied to the entity. Args: x, y, z, entity, vec3d
     */
    @Override
    public void velocityToAddToEntity(World world, int x, int y, int z, Entity ent, Vec3 vec)
    {
        getSourceBlock().velocityToAddToEntity(world, x, y, z, ent, vec);
    }

    /**
     * How bright to render this block based on the light its receiving. Args: iBlockAccess, x, y, z
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z)
    {
        return getSourceBlock().getMixedBrightnessForBlock(world, x, y, z);
    }

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return getSourceBlock().getRenderBlockPass();
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return this.sourceBlock.getBlock().getIcon(side, this.sourceBlock.getMetadata());
    }

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return getSourceBlock().getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    /**
     * Returns if this block is collidable (only used by Fire)
     */
    @Override
    public boolean isCollidable()
    {
        return getSourceBlock().isCollidable();
    }

    /**
     * Returns whether this block is collideable based on the arguments passed in
     * @param par1 block metaData
     * @param par2 whether the player right-clicked while holding a boat
     */
    @Override
    public boolean canCollideCheck(int meta, boolean boatRightClick)
    {
        return getSourceBlock().canCollideCheck(meta, boatRightClick);
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return getSourceBlock().canPlaceBlockAt(world, x, y, z);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        this.onNeighborBlockChange(world, x, y, z, Blocks.air);
        getSourceBlock().onBlockAdded(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        getSourceBlock().breakBlock(world, x, y, z, block, meta);
    }

    /**
     * Called whenever an entity is walking on top of this block. Args: world, x, y, z, entity
     */
    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity ent)
    {
        this.getSourceBlock().onEntityWalking(world, x, y, z, ent);
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        this.getSourceBlock().updateTick(world, x, y, z, rand);
    }

    /**
     * Called upon block activation (right click on the block.)
     *
     * Activate the clicked on block, otherwise use the held item. Args: player, world, itemStack, x, y, z, side,
     * xOffset, yOffset, zOffset

     */
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset)
    {
        return this.getSourceBlock().onBlockActivated(world, x, y, z, player, 0, 0.0F, 0.0F, 0.0F);
    }

    /**
     * Called upon the block being destroyed by an explosion
     */
    @Override
    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion kaboom)
    {
        this.getSourceBlock().onBlockDestroyedByExplosion(world, x, y, z, kaboom);
    }

    @Override
    public MapColor getMapColor(int foo)
    {
        return this.sourceBlock.getBlock().getMapColor(sourceBlock.getMetadata());
    }
}
