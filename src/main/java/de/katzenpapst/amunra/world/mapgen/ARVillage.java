package de.katzenpapst.amunra.world.mapgen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;
import micdoodle8.mods.galacticraft.core.world.gen.BiomeGenBaseMoon;
/*import micdoodle8.mods.galacticraft.core.world.gen.MapGenVillageMoon;
import micdoodle8.mods.galacticraft.core.world.gen.StructureComponentVillageField;
import micdoodle8.mods.galacticraft.core.world.gen.StructureComponentVillageField2;
import micdoodle8.mods.galacticraft.core.world.gen.StructureComponentVillageHouse;
import micdoodle8.mods.galacticraft.core.world.gen.StructureComponentVillagePathGen;
import micdoodle8.mods.galacticraft.core.world.gen.StructureComponentVillageRoadPiece;
import micdoodle8.mods.galacticraft.core.world.gen.StructureComponentVillageStartPiece;
import micdoodle8.mods.galacticraft.core.world.gen.StructureComponentVillageTorch;
import micdoodle8.mods.galacticraft.core.world.gen.StructureComponentVillageWoodHut;
import micdoodle8.mods.galacticraft.core.world.gen.StructureVillagePieceWeightMoon;
import micdoodle8.mods.galacticraft.core.world.gen.StructureVillageStartMoon;*/

/**
 * This goes into the chunkprovider, see ChunkProviderMoon
 * @author Alex
 *
 */
public class ARVillage extends MapGenStructure {
	
	/*
	 * CLASS MAPPING
	 * MapGenVillageMoon 					x-> ARVillage							
	 * StructureVillageStartMoon 			x-> ARVillageStart
	 * StructureComponentVillage			x-> ARVillageComponent
	 * StructureComponentVillageWell		x-> ARVillageComponentWell
	 * StructureComponentVillageHouse 		x-> ARVillageComponentHouse
	 * StructureComponentVillageRoadPiece 	x-> ARVillageComponentRoadPiece
	 * StructureComponentVillagePathGen 	x-> ARVillageComponentPathGen
	 * StructureComponentVillageStartPiece 	x-> ARVillageComponentStartPiece
	 * StructureVillagePieceWeightMoon		x-> ARVillagePieceWeight
	 * StructureVillagePiecesMoon			x-> ARVillagePieces
	 * 
	 */
	
	public static List<BiomeGenBase> villageSpawnBiomes = Arrays.asList(new BiomeGenBase[] { BiomeGenBaseMoon.moonFlat });
    private final int terrainType;
    private static boolean initialized;

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

    public static void initiateStructures() throws Throwable
    {
        if (!ARVillage.initialized)
        {
            MapGenStructureIO.registerStructure(ARVillageStart.class, "TestVillage");
            //MapGenStructureIO.func_143031_a(StructureComponentVillageField.class, "MoonField1");
            //MapGenStructureIO.func_143031_a(StructureComponentVillageField2.class, "MoonField2");
            MapGenStructureIO.func_143031_a(ARVillageComponentHouse.class, "TestHouse");
            MapGenStructureIO.func_143031_a(ARVillageComponentRoadPiece.class, "TestRoadPiece");
            MapGenStructureIO.func_143031_a(ARVillageComponentPathGen.class, "TestPath");
            //MapGenStructureIO.func_143031_a(StructureComponentVillageTorch.class, "MoonTorch");
            MapGenStructureIO.func_143031_a(ARVillageComponentStartPiece.class, "TestWell");
            //MapGenStructureIO.func_143031_a(StructureComponentVillageWoodHut.class, "MoonWoodHut");
        }

        ARVillage.initialized = true;
    }

    public ARVillage()
    {
        this.terrainType = 0;
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int i, int j)
    {
        final byte numChunks = 32;
        final byte offsetChunks = 4;
        final int oldi = i;
        final int oldj = j;

        if (i < 0)
        {
            i -= numChunks - 1;
        }

        if (j < 0)
        {
            j -= numChunks - 1;
        }

        int randX = i / numChunks;
        int randZ = j / numChunks;
        final Random var7 = this.worldObj.setRandomSeed(i, j, 10387312);
        randX *= numChunks;
        randZ *= numChunks;
        randX += var7.nextInt(numChunks - offsetChunks);
        randZ += var7.nextInt(numChunks - offsetChunks);

        return oldi == randX && oldj == randZ;

    }

    @Override
    protected StructureStart getStructureStart(int par1, int par2)
    {
        FMLLog.info("Generating Test Village at x" + par1 * 16 + " z" + par2 * 16);
        return new ARVillageStart(this.worldObj, this.rand, par1, par2, this.terrainType);
    }

    @Override
    public String func_143025_a()
    {
        return "TestVillage";
    }
}
