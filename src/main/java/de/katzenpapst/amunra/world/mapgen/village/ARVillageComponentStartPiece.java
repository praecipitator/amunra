package de.katzenpapst.amunra.world.mapgen.village;

import java.util.ArrayList;
import java.util.Random;






import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.WorldChunkManager;

public class ARVillageComponentStartPiece extends ARVillageComponentWell
{
    public WorldChunkManager worldChunkMngr;
    public int terrainType;
    public ARVillagePieceWeight structVillagePieceWeight;
    public ArrayList<ARVillagePieceWeight> structureVillageWeightedPieceList;
    public ArrayList<Object> field_74932_i = new ArrayList<Object>();
    public ArrayList<Object> field_74930_j = new ArrayList<Object>();

    
    public ARVillageComponentStartPiece()
    {
    	super();
    }

    /**
     * 
     * @param mainObj
     * @param par1WorldChunkManager
     * @param par2
     * @param par3Random
     * @param x
     * @param z
     * @param par6ArrayList
     * @param par7
     */
    public ARVillageComponentStartPiece(ARVillage mainObj, WorldChunkManager par1WorldChunkManager, int par2, Random par3Random, int x, int z, ArrayList<ARVillagePieceWeight> par6ArrayList, int par7)
    {
    	// ok I see the problem, I need to pass "this" to the super constructor, which makes no sense
        super((ARVillageComponentStartPiece) null, mainObj, 0, par3Random, x, z);
        
        this.worldChunkMngr = par1WorldChunkManager;
        this.structureVillageWeightedPieceList = par6ArrayList;
        this.terrainType = par7;
        this.startPiece = this;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt)
    {
        super.func_143012_a(nbt);

        nbt.setInteger("TerrainType", this.terrainType);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbt)
    {
        super.func_143011_b(nbt);

        this.terrainType = nbt.getInteger("TerrainType");
    }

    public WorldChunkManager getWorldChunkManager()
    {
        return this.worldChunkMngr;
    }
    
}
