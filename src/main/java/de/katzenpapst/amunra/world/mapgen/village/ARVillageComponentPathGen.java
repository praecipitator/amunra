package de.katzenpapst.amunra.world.mapgen.village;

import java.util.List;
import java.util.Random;

import de.katzenpapst.amunra.block.ARBlocks;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class ARVillageComponentPathGen extends ARVillageComponentRoadPiece {
	private int averageGroundLevel;
	
	private BlockMetaPair roadMaterial = ARBlocks.multiBlockDirt.getBlockMetaPair("basaltregolith");

    public ARVillageComponentPathGen()
    {
    }

    public ARVillageComponentPathGen(ARVillageComponentStartPiece startPiece, int type, Random par3Random, StructureBoundingBox structBB, int coordBaseMode)
    {
        super();
        init(startPiece, type, structBB, coordBaseMode);
        //this.coordBaseMode = coordBaseMode;
        //this.boundingBox = par4StructureBoundingBox;
        //this.averageGroundLevel = Math.max(par4StructureBoundingBox.getXSize(), par4StructureBoundingBox.getZSize());
    }
    
    protected void init(ARVillageComponentStartPiece startPiece,  int type, StructureBoundingBox structureBB, int coordBaseMode) {
    	super.init(startPiece,  type, structureBB, coordBaseMode);
    
    	this.averageGroundLevel = Math.max(structureBB.getXSize(), structureBB.getZSize());
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        boolean var4 = false;
        int var5;
        StructureComponent var6;

        for (var5 = par3Random.nextInt(5); var5 < this.averageGroundLevel - 8; var5 += 2 + par3Random.nextInt(5))
        {
            var6 = this.getNextComponentNN((ARVillageComponentStartPiece) par1StructureComponent, par2List, par3Random, 0, var5);

            if (var6 != null)
            {
                var5 += Math.max(var6.getBoundingBox().getXSize(), var6.getBoundingBox().getZSize());
                var4 = true;
            }
        }

        for (var5 = par3Random.nextInt(5); var5 < this.averageGroundLevel - 8; var5 += 2 + par3Random.nextInt(5))
        {
            var6 = this.getNextComponentPP((ARVillageComponentStartPiece) par1StructureComponent, par2List, par3Random, 0, var5);

            if (var6 != null)
            {
                var5 += Math.max(var6.getBoundingBox().getXSize(), var6.getBoundingBox().getZSize());
                var4 = true;
            }
        }

        if (var4 && par3Random.nextInt(3) > 0)
        {
            switch (this.coordBaseMode)
            {
            case 0:
                ARVillagePieces.getNextStructureComponentVillagePath((ARVillageComponentStartPiece) par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, 1, this.getComponentType());
                break;
            case 1:
                ARVillagePieces.getNextStructureComponentVillagePath((ARVillageComponentStartPiece) par1StructureComponent, par2List, par3Random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, this.getComponentType());
                break;
            case 2:
                ARVillagePieces.getNextStructureComponentVillagePath((ARVillageComponentStartPiece) par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, 1, this.getComponentType());
                break;
            case 3:
                ARVillagePieces.getNextStructureComponentVillagePath((ARVillageComponentStartPiece) par1StructureComponent, par2List, par3Random, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.minZ - 1, 2, this.getComponentType());
            }
        }

        if (var4 && par3Random.nextInt(3) > 0)
        {
            switch (this.coordBaseMode)
            {
            case 0:
                ARVillagePieces.getNextStructureComponentVillagePath((ARVillageComponentStartPiece) par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 2, 3, this.getComponentType());
                break;
            case 1:
                ARVillagePieces.getNextStructureComponentVillagePath((ARVillageComponentStartPiece) par1StructureComponent, par2List, par3Random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, this.getComponentType());
                break;
            case 2:
                ARVillagePieces.getNextStructureComponentVillagePath((ARVillageComponentStartPiece) par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, 3, this.getComponentType());
                break;
            case 3:
                ARVillagePieces.getNextStructureComponentVillagePath((ARVillageComponentStartPiece) par1StructureComponent, par2List, par3Random, this.boundingBox.maxX - 2, this.boundingBox.minY, this.boundingBox.maxZ + 1, 0, this.getComponentType());
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static StructureBoundingBox func_74933_a(ARVillageComponentStartPiece par0ComponentVillageStartPiece, List par1List, Random par2Random, int par3, int par4, int par5, int par6)
    {
        for (int var7 = 7 * MathHelper.getRandomIntegerInRange(par2Random, 3, 5); var7 >= 7; var7 -= 7)
        {
            final StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 3, 3, var7, par6);

            if (StructureComponent.findIntersecting(par1List, var8) == null)
            {
                return var8;
            }
        }

        return null;
    }

    /**
     * This seems to place the actual path
     * second Part of Structure generating, this for example places Spiderwebs,
     * Mob Spawners, it closes Mineshafts at the end, it adds Fences...
     */
    @Override
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {

        for (int x = this.boundingBox.minX; x <= this.boundingBox.maxX; ++x)
        {
            for (int z = this.boundingBox.minZ; z <= this.boundingBox.maxZ; ++z)
            {
                if (par3StructureBoundingBox.isVecInside(x, 64, z))
                {
                    final int y = par1World.getTopSolidOrLiquidBlock(x, z) - 1;
                    
                    // Block curBlock = par1World.getBlock(x, y, z);
                    // orig code was if curBlock is air or moonblock
                    //if(curBlock == Blocks.air || curBlock.)
                    // x, y, z, block, blockmeta, 3 (=1+2)
                    this.setBlockMetaPair(par1World, x, y, z, roadMaterial, 3);
                }
            }
        }

        return true;
    }
}
