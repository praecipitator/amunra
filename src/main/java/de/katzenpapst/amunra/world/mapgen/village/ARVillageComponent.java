package de.katzenpapst.amunra.world.mapgen.village;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.entities.EntityAlienVillager;
//import micdoodle8.mods.galacticraft.core.world.gen.MapGenVillageMoon;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public abstract class ARVillageComponent extends StructureComponent {
	static
    {
        try
        {
        	ARVillage.initiateStructures();
        }
        catch (Throwable e)
        {

        }
    }
	protected ARVillage mainVillageObject;

    private int villagersSpawned;
    protected ARVillageComponentStartPiece startPiece;

    /*public ARVillageComponent()
    {
    }*/
    
    public static int rotateMetadata(int unrotated, int coordMode) {
        // 0 -> direct
        // 1 -> rotate by 90° CCW, aka N turns to W, W to S, etc
        // 2 -> Z is flipped aka mirror at x
        // 3 -> coordflip, swap N <--> O and S <--> W
    	/* now I think: 
    	 * 0 -> S, 
    	 * 1 -> N, 
    	 * 2 -> O, 
    	 * 3 -> W
    	 */
    	switch(coordMode) {
    	case 0:
    		return unrotated;
    	case 1:
    		switch(unrotated) {
    		case 0:
    			return 2;
    		case 1:
    			return 3;
    		case 2:
    			return 1;
    		case 3:
    			return 0; 
    		}
    		break;
    	case 2:
    		switch(unrotated) {
    		case 0:
    			return 1;
    		case 1: 
    			return 0;
    		case 2:
    		case 3:
    			return unrotated;
    		}
    		break;
    	case 3:
    		switch(unrotated) {
    		case 0:
    			return 3;
    		case 1:
    			return 2;
    		case 2:
    			return 1;
    		case 3:
    			return 0; 
    		}

    	}
    	return unrotated;
    }

    protected ARVillageComponent(ARVillageComponentStartPiece startPiece, ARVillage mainObj, int type)
    {
        super(type);
        this.startPiece = startPiece;
        mainVillageObject = mainObj;
    }
    
    public ARVillageComponent() {
    	// empty constructors are OK after all, but the init will have to be called afterwards. this is for construction by class name
    }
    
    protected void init(ARVillageComponentStartPiece startPiece, ARVillage mainObj, int type, StructureBoundingBox structureBB, int coordBaseMode) {
    	this.startPiece = startPiece;
    	this.mainVillageObject = mainObj;
    	this.componentType = type;
    	this.coordBaseMode = coordBaseMode;
        this.boundingBox = structureBB;
    	
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setInteger("VCount", this.villagersSpawned);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbttagcompound)
    {
        this.villagersSpawned = nbttagcompound.getInteger("VCount");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected StructureComponent getNextComponentNN(ARVillageComponentStartPiece par1ComponentVillageStartPiece, List par2List, Random par3Random, int offsetY, int lengthMaybe)
    {
        switch (this.coordBaseMode)
        {
        case 0:
            return ARVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + offsetY, this.boundingBox.minZ + lengthMaybe, 1, this.getComponentType());
        case 1:
            return ARVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + lengthMaybe, this.boundingBox.minY + offsetY, this.boundingBox.minZ - 1, 2, this.getComponentType());
        case 2:
            return ARVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + offsetY, this.boundingBox.minZ + lengthMaybe, 1, this.getComponentType());
        case 3:
            return ARVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + lengthMaybe, this.boundingBox.minY + offsetY, this.boundingBox.minZ - 1, 2, this.getComponentType());
        default:
            return null;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected StructureComponent getNextComponentPP(ARVillageComponentStartPiece par1ComponentVillageStartPiece, List par2List, Random par3Random, int offsetY, int par5)
    {
        switch (this.coordBaseMode)
        {
        case 0:
            return ARVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + offsetY, this.boundingBox.minZ + par5, 3, this.getComponentType());
        case 1:
            return ARVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + offsetY, this.boundingBox.maxZ + 1, 0, this.getComponentType());
        case 2:
            return ARVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY + offsetY, this.boundingBox.minZ + par5, 3, this.getComponentType());
        case 3:
            return ARVillagePieces.getNextStructureComponent(par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX + par5, this.boundingBox.minY + offsetY, this.boundingBox.maxZ + 1, 0, this.getComponentType());
        default:
            return null;
        }
    }

    protected int getAverageGroundLevel(World par1World, StructureBoundingBox par2StructureBoundingBox)
    {
        int var3 = 0;
        int var4 = 0;

        for (int var5 = this.boundingBox.minZ; var5 <= this.boundingBox.maxZ; ++var5)
        {
            for (int var6 = this.boundingBox.minX; var6 <= this.boundingBox.maxX; ++var6)
            {
                if (par2StructureBoundingBox.isVecInside(var6, 64, var5))
                {
                    var3 += Math.max(par1World.getTopSolidOrLiquidBlock(var6, var5), par1World.provider.getAverageGroundLevel());
                    ++var4;
                }
            }
        }

        if (var4 == 0)
        {
            return -1;
        }
        else
        {
            return var3 / var4;
        }
    }

    protected static boolean canVillageGoDeeper(StructureBoundingBox par0StructureBoundingBox)
    {
        return par0StructureBoundingBox != null && par0StructureBoundingBox.minY > 10;
    }

    protected void spawnVillagers(World par1World, StructureBoundingBox par2StructureBoundingBox, int par3, int par4, int par5, int numVillagers)
    {
        if (this.villagersSpawned < numVillagers)
        {
            for (int i = this.villagersSpawned; i < numVillagers; ++i)
            {
                int var8 = this.getXWithOffset(par3 + i, par5);
                final int var9 = this.getYWithOffset(par4);
                int var10 = this.getZWithOffset(par3 + i, par5);

                var8 += par1World.rand.nextInt(3) - 1;
                var10 += par1World.rand.nextInt(3) - 1;

                if (!par2StructureBoundingBox.isVecInside(var8, var9, var10))
                {
                    break;
                }

                ++this.villagersSpawned;
                
               EntityCreature villager = createVillager(par1World);
               villager.onSpawnWithEgg(null);// NO IDEA
               villager.setLocationAndAngles(var8 + 0.5D, var9, var10 + 0.5D, 0.0F, 0.0F);
               par1World.spawnEntityInWorld(villager);
                //final EntityAlienVillager var11 = new EntityAlienVillager(par1World);
                //var11.setLocationAndAngles(var8 + 0.5D, var9, var10 + 0.5D, 0.0F, 0.0F);
                //par1World.spawnEntityInWorld(var11);
            }
        }
    }
    
    protected ARVillage getMainVillageObject() {
    	return mainVillageObject;
    }
    
    protected EntityCreature createVillager(World world) {
    	Class villagerClass = getMainVillageObject().getVillagerEntityClass();
    	try {
    		EntityCreature villager = (EntityCreature) villagerClass.getConstructor(World.class).newInstance(world);
    		
    		
    		return villager;
			
		} catch (Exception e) {
			FMLLog.info("Failed instantiating villager "+villagerClass.getCanonicalName());
			e.printStackTrace();
		}
    	return null;
    }

    protected int getVillagerType(int par1)
    {
        return 0;
    }

    /**
     * This seems to do a block translation
     * 
     * @param par1
     * @param par2
     * @return
     */
    protected Block getBiomeSpecificBlock(Block par1, int par2)
    {
        return par1;
    }

    protected int getBiomeSpecificBlockMetadata(Block par1, int par2)
    {
        return par2;
    }
    
    protected void placeBlockAtCurrentPosition(World world, BlockMetaPair block, int x, int y, int z, StructureBoundingBox bb) {
    	placeBlockAtCurrentPosition(world, block.getBlock(), block.getMetadata(), x, y, z, bb);
    }
/*
    @Override
    protected void placeBlockAtCurrentPosition(World par1World, Block block2place, int blockMeta, int par4, int par5, int par6, StructureBoundingBox par7StructureBoundingBox)
    {
        final Block specificBlock = this.getBiomeSpecificBlock(block2place, blockMeta);
        final int specificMeta = this.getBiomeSpecificBlockMetadata(block2place, blockMeta);
        super.placeBlockAtCurrentPosition(par1World, specificBlock, specificMeta, par4, par5, par6, par7StructureBoundingBox);
    }*/

    /**
     * 
     * /
    @Override
    protected void fillWithBlocks(World par1World, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block block1, Block block2, boolean alwaysReplace)
    {
        final Block blockPlaceBlock = this.getBiomeSpecificBlock(block1, 0);
        final int blockPlaceMeta = this.getBiomeSpecificBlockMetadata(block1, 0);
        final Block blockReplaceBlock = this.getBiomeSpecificBlock(block2, 0);
        final int blockReplaceMeta = this.getBiomeSpecificBlockMetadata(block2, 0);
        
        super.fillWithMetadataBlocks(par1World, structBB, minX, minY, minZ, maxX, maxY, maxZ, blockPlaceBlock, blockPlaceMeta, blockReplaceBlock, blockReplaceMeta, alwaysReplace);
    }*/
    
    protected void fillWithBlocks(World par1World, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockMetaPair blockPlace, BlockMetaPair blockReplace, boolean alwaysReplace)
    {
        super.fillWithMetadataBlocks(par1World, structBB, minX, minY, minZ, maxX, maxY, maxZ, blockPlace.getBlock(), blockPlace.getMetadata(), blockReplace.getBlock(), blockReplace.getMetadata(), alwaysReplace);
    }

    /**
     * Fill with blocks??
     */
    @Override
    protected void func_151554_b(World par1World, Block par2, int par3, int par4, int par5, int par6, StructureBoundingBox par7StructureBoundingBox)
    {
        final Block var8 = this.getBiomeSpecificBlock(par2, par3);
        final int var9 = this.getBiomeSpecificBlockMetadata(par2, par3);
        super.func_151554_b(par1World, var8, var9, par4, par5, par6, par7StructureBoundingBox);
    }
    
    /**
     * Wrapper for world.setBlock()
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @param bmp		the BlockMetaPair
     * @param flags		Flag 1 will cause a block update. 
     * 					Flag 2 will send the change to clients (you almost always want this). 
     * 					Flag 4 prevents the block from being re-rendered, if this is a client world. 
     * 					Flags can be added together.
     * 					3 is a good flag for worldgen stuff
     */
    protected void setBlockMetaPair(World world, int x, int y, int z, BlockMetaPair bmp, int flags) {
    	world.setBlock(x, y, z, bmp.getBlock(), bmp.getMetadata(), flags);
    }
}

