package de.katzenpapst.amunra.world.mapgen.village;

import java.util.List;
import java.util.Random;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class ARVillageComponentHouse extends ARVillageComponent {
	private int averageGroundLevel = -1;

	protected BlockMetaPair wallMaterial;
	protected BlockMetaPair floorMaterial;

	/**
	 * 
	 * @param startPiece
	 * @param mainObj
	 * @param type
	 * @param par3Random
	 * @param structureBB
	 * @param coordBaseMode
	 */
    public ARVillageComponentHouse(ARVillageComponentStartPiece startPiece, ARVillage mainObj, int type, Random par3Random, StructureBoundingBox structureBB, int coordBaseMode)
    {
        // super(startPiece, mainObj, type);
        // this.coordBaseMode = coordBaseMode;
        //this.boundingBox = structureBB;
        this.init(startPiece, mainObj, type, structureBB, coordBaseMode);
        //wallMaterial = this.getMainVillageObject().getWallMaterial();
        //floorMaterial = this.getMainVillageObject().getFloorMaterial();
    }
    
    public ARVillageComponentHouse() {
    	// I need the PUBLIC blank constructor for the init stuff
    }
    
    @Override
    protected void init(ARVillageComponentStartPiece startPiece, ARVillage mainObj, int type, StructureBoundingBox structureBB, int coordBaseMode) {
    	
    	super.init(startPiece, mainObj, type, structureBB, coordBaseMode);
    	
    	wallMaterial = this.getMainVillageObject().getWallMaterial();
        floorMaterial = this.getMainVillageObject().getFloorMaterial();
    	
    }

    /**
     *  result = ARVillageComponentHouse.createInstance(startPiece, par2List, par3Random, x, y, z, par7, type);
     * @param startPiece
     * @param list
     * @param rand
     * @param x
     * @param y
     * @param z
     * @param coordBaseMode
     * @param type
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static ARVillageComponentHouse createInstance(ARVillageComponentStartPiece startPiece, List list, Random rand, int x, int y, int z, int coordBaseMode, int type)
    {
        final StructureBoundingBox structureBB = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 17, 9, 17, coordBaseMode);
        return StructureComponent.findIntersecting(list, structureBB) == null ? new ARVillageComponentHouse(startPiece, startPiece.getMainVillageObject(), type, rand, structureBB, coordBaseMode) : null;
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

    /**
     * second Part of Structure generating, this for example places Spiderwebs,
     * Mob Spawners, it closes Mineshafts at the end, it adds Fences...
     */
    @Override
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        if (this.averageGroundLevel < 0)
        {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

            if (this.averageGroundLevel < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 9 - 1, 0);
        }

        this.fillWithAir(par1World, par3StructureBoundingBox, 3, 0, 3, 13, 9, 13);
        this.fillWithAir(par1World, par3StructureBoundingBox, 5, 0, 2, 11, 9, 14);
        this.fillWithAir(par1World, par3StructureBoundingBox, 2, 0, 5, 14, 9, 11);

        for (int i = 3; i <= 13; i++)
        {
            for (int j = 3; j <= 13; j++)
            {
                this.placeBlockAtCurrentPosition(par1World, floorMaterial, i, 0, j, par3StructureBoundingBox);
            }
        }

        for (int i = 5; i <= 11; i++)
        {
            for (int j = 2; j <= 14; j++)
            {
                this.placeBlockAtCurrentPosition(par1World, floorMaterial, i, 0, j, par3StructureBoundingBox);
            }
        }

        for (int i = 2; i <= 14; i++)
        {
            for (int j = 5; j <= 11; j++)
            {
                this.placeBlockAtCurrentPosition(par1World, floorMaterial, i, 0, j, par3StructureBoundingBox);
            }
        }

        int yLevel = 0;

        for (yLevel = -8; yLevel < 4; yLevel++)
        {
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 3, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 4, par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 5, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 6, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 7, par3StructureBoundingBox);
            
            if(yLevel <= 1) {            	
            	this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 8, par3StructureBoundingBox);
            } else {            	
            	this.placeBlockAtCurrentPosition(par1World, Blocks.air, 4, 1, yLevel, 8, par3StructureBoundingBox);
            }
            
            
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 9, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 10, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 11, par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 12, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 13, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 14, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 14, par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 15, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 15, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 15, par3StructureBoundingBox);
            
            // figure out what this does
            if(yLevel <= 1) {
            	this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 15, par3StructureBoundingBox);            	
            } else {            	
            	// in original, it had meta 4 for air. was this meant?
            	this.placeBlockAtCurrentPosition(par1World, Blocks.air, 0, 8, yLevel, 15, par3StructureBoundingBox);
            }
            
            
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 15, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 15, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 15, par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 14, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 14, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 13, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 12, par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 11, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 10, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 9, par3StructureBoundingBox);
            
            if(yLevel <= 1) {            	
            	this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 8, par3StructureBoundingBox);
            } else {
            	this.placeBlockAtCurrentPosition(par1World, Blocks.air, 4, 15, yLevel, 8, par3StructureBoundingBox);
            }
            
            
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 7, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 6, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 5, par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 4, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 3, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 2, par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 1, par3StructureBoundingBox);
            
            if(yLevel <= 1 ) {            	
            	this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 1, par3StructureBoundingBox);
            } else {            	
            	this.placeBlockAtCurrentPosition(par1World, Blocks.air, 4, 8, yLevel, 1, par3StructureBoundingBox);
            }
            
            
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 1, par3StructureBoundingBox);
        }

        yLevel = 4;

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 4, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 11, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 14, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 15, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 12, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 5, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 2, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 1, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 8, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 14, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 8, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, GCBlocks.glowstoneTorch, 0, 2, yLevel, 8, par3StructureBoundingBox);

        yLevel = 5;

        // corner 1
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 5, par3StructureBoundingBox);

        // side 1
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 1, yLevel, 10, par3StructureBoundingBox);

        // corner 2
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 14, par3StructureBoundingBox);

        // side 2
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 15, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 15, par3StructureBoundingBox);

        // corner 3
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 11, par3StructureBoundingBox);

        // side 3
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 15, yLevel, 6, par3StructureBoundingBox);

        // corner 4
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 2, par3StructureBoundingBox);

        // side 4
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 1, par3StructureBoundingBox);

        yLevel = 6;

        // corner 1
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 4, par3StructureBoundingBox);

        // side 1
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 11, par3StructureBoundingBox);

        // corner 2
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 13, par3StructureBoundingBox);

        // side 2
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 14, par3StructureBoundingBox);

        // corner 3
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 12, par3StructureBoundingBox);

        // side 3
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 5, par3StructureBoundingBox);

        // corner 4
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 3, par3StructureBoundingBox);

        // side 4
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 2, par3StructureBoundingBox);

        yLevel = 7;

        // corner 1
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 6, par3StructureBoundingBox);

        // side 1
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 2, yLevel, 9, par3StructureBoundingBox);

        // corner 2
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 13, par3StructureBoundingBox);

        // side 2
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 14, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 14, par3StructureBoundingBox);

        // corner 3
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 10, par3StructureBoundingBox);

        // side 3
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 14, yLevel, 7, par3StructureBoundingBox);

        // corner 4
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 3, par3StructureBoundingBox);

        // side 4
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 2, par3StructureBoundingBox);

        yLevel = 8;

        // corner 1
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 6, par3StructureBoundingBox);

        // side 1
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 3, yLevel, 9, par3StructureBoundingBox);

        // corner 2
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 10, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 6, yLevel, 12, par3StructureBoundingBox);

        // side 2
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 13, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 13, par3StructureBoundingBox);

        // corner 3
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 10, par3StructureBoundingBox);

        // side 3
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 9, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 13, yLevel, 7, par3StructureBoundingBox);

        // corner 4
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 6, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 10, yLevel, 4, par3StructureBoundingBox);

        // side 4
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 3, par3StructureBoundingBox);

        // extras
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 5, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 5, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 11, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 11, yLevel, 5, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 4, yLevel, 9, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 12, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 12, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 9, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 8, yLevel, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 7, yLevel, 4, par3StructureBoundingBox);

        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 7, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 8, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, wallMaterial, 12, yLevel, 9, par3StructureBoundingBox);

        yLevel = 9;

        for (int i = 5; i <= 11; i++)
        {
            for (int j = 5; j <= 11; j++)
            {
                if (!(j == 5 && i == 5 || j == 5 && i == 11 || j == 11 && i == 5 || j == 11 && i == 11))
                {
                    if (i >= 7 && i <= 9 && j >= 7 && j <= 9)
                    {
                    	// this seems to be the roof
                        this.placeBlockAtCurrentPosition(par1World, Blocks.glass, 0, i, yLevel, j, par3StructureBoundingBox);
                    }
                    else
                    {
                        this.placeBlockAtCurrentPosition(par1World, wallMaterial, i, yLevel, j, par3StructureBoundingBox);
                    }
                }
            }
        }

        this.spawnVillagers(par1World, par3StructureBoundingBox, 6, 5, 6, 4);
        return true;
    }
}
