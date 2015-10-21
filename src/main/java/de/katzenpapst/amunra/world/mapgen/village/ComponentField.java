package de.katzenpapst.amunra.world.mapgen.village;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import java.util.List;
import java.util.Random;

import de.katzenpapst.amunra.block.ARBlocks;


public class ComponentField extends ARVillageComponent{
    private int averageGroundLevel = -1;
    
    protected BlockMetaPair wallMaterial = ARBlocks.multiBlockRock.getBlockMetaPair("alucrate");
    protected Block solarBlock  = GCBlocks.solarPanel;

    public ComponentField() {
    	super();
    }

    public ComponentField(ARVillageComponentStartPiece par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
    {
        super(par1ComponentVillageStartPiece, par2);
        init(par1ComponentVillageStartPiece,  par2, par4StructureBoundingBox, par5);
    }
    
    protected void init(ARVillageComponentStartPiece startPiece, int type, StructureBoundingBox structureBB, int coordBaseMode) {
    	super.init(startPiece,  type, structureBB, coordBaseMode);
    	
    	
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt)
    {
        super.func_143012_a(nbt);

        nbt.setInteger("AvgGroundLevel", this.averageGroundLevel);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbt)
    {
        super.func_143011_b(nbt);

        this.averageGroundLevel = nbt.getInteger("AvgGroundLevel");
    }

    @SuppressWarnings("rawtypes")
    public static ComponentField createInstance(ARVillageComponentStartPiece startPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        final StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 13, 4, 9, par6);
        return StructureComponent.findIntersecting(par1List, var8) == null ? new ComponentField(startPiece,  par7, par2Random, var8, par6) : null;
    }

    @Override
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox structBB)
    {
        if (this.averageGroundLevel < 0)
        {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, structBB);

            if (this.averageGroundLevel < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 7 - 1, 0);
        }
        
        // now how the fuck does the meta stuff works
        // meh let's do trial&error
        // (world, block.getBlock(), block.getMetadata(), x, y, z, bb
        
        // this creates 12x4x8 of empty space above the field 
        // now try it height=5 and starting at the level
        this.fillWithBlocks(par1World, structBB,  0, 0, 0, 12, 5, 8, Blocks.air, Blocks.air, false);
        
        /*
        // try rotating these to +x
        int towardsEast = this.rotateMetadata(0, this.coordBaseMode);
        this.placeBlockAtCurrentPosition(par1World, solarBlock, towardsEast,  1, 0, 1, structBB);
        this.placeBlockAtCurrentPosition(par1World, solarBlock, towardsEast, 11, 0, 1, structBB);
        
        int towardsWest = this.rotateMetadata(1, this.coordBaseMode);
        this.placeBlockAtCurrentPosition(par1World, solarBlock, towardsWest,  1, 0, 7, structBB);
        this.placeBlockAtCurrentPosition(par1World, solarBlock, towardsWest, 11, 0, 7, structBB);
        
        */

        // arguments: (World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
        // maxZ, int placeBlock, int replaceBlock, boolean alwaysreplace)
        // seems like
        //														  x, y, z | x, y, z
        
        /*
        // these are 2x1x7 lines of dirt
        this.fillWithBlocks(par1World, par3StructureBoundingBox,  1, 0, 1,  2, 0, 7, Blocks.dirt, Blocks.dirt, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox,  4, 0, 1,  5, 0, 7, Blocks.dirt, Blocks.dirt, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox,  7, 0, 1,  8, 0, 7, Blocks.dirt, Blocks.dirt, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox, 10, 0, 1, 11, 0, 7, Blocks.dirt, Blocks.dirt, false);
        */
        // 1x9 line of wood on the x=0 line, border line
        this.fillWithBlocks(par1World, structBB,  0, 0, 0,  0, 0, 8, Blocks.planks, Blocks.planks, false);
        // 1x9 line of wood on the x=6 line, aka the central one
        this.fillWithBlocks(par1World, structBB,  6, 0, 0,  6, 0, 8, Blocks.planks, Blocks.planks, false);
        // 1x9 on x=12 the other border line
        this.fillWithBlocks(par1World, structBB, 12, 0, 0, 12, 0, 8, Blocks.planks, Blocks.planks, false);
        // the other two borders along the x lines
        this.fillWithBlocks(par1World, structBB,  1, 0, 0, 11, 0, 0, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(par1World, structBB,  1, 0, 8, 11, 0, 8, Blocks.planks, Blocks.planks, false);
        
        // marker
        this.fillWithBlocks(par1World, structBB,  0, 0, 0,  0, 0, 0, Blocks.obsidian, Blocks.obsidian, true);
        /*
        // water
        this.fillWithBlocks(par1World, par3StructureBoundingBox,  3, 0, 1,  3, 0, 7, Blocks.flowing_water, Blocks.flowing_water, false);
        this.fillWithBlocks(par1World, par3StructureBoundingBox,  9, 0, 1,  9, 0, 7, Blocks.flowing_water, Blocks.flowing_water, false);
        */
        int var4;
        /*
        for (var4 = 1; var4 <= 7; ++var4)
        {
            for (int i = 1; i < 12; i++)
            {
                if (i % 3 != 0)
                {
                    if (par2Random.nextInt(3) == 0)
                    {
                        this.placeBlockAtCurrentPosition(par1World, Blocks.sapling, MathHelper.getRandomIntegerInRange(par2Random, 0, 2), i, 1, var4, par3StructureBoundingBox);
                    }
                }
            }
        }
        */
        // I think this 
        for (var4 = 0; var4 < 9; ++var4)
        {
            for (int var5 = 0; var5 < 13; ++var5)
            {
                this.clearCurrentPositionBlocksUpwards(par1World, var5, 4, var4, structBB);
                // I think this fills the stuff below with stuff
                this.func_151554_b(par1World, wallMaterial.getBlock(), wallMaterial.getMetadata(), var5, -1, var4, structBB);
            }
        }

        return true;
    }
    
}




