package de.katzenpapst.amunra.world.mapgen.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;







import cpw.mods.fml.common.FMLLog;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

public class ARVillageStart extends StructureStart {
	public ARVillageStart()
    {
		// seems like I need this... but how do I get the parent in here now?
		FMLLog.info("Something's calling this");
    }

	/**
	 * 
	 * @param parent
	 * @param x
	 * @param z	THAT might actually be a Z
	 * @param terrainType
	 */
    @SuppressWarnings("unchecked")
    public ARVillageStart(World par1World, Random par2Random, int x, int z, int terrainType)
    {
        super(x, z);
        

        final ArrayList<ARVillagePieceWeight> list = ARVillagePieces.getStructureVillageWeightedPieceList(par2Random, terrainType);
        final ARVillageComponentStartPiece var7 = new ARVillageComponentStartPiece(
        		par1World.getWorldChunkManager(), 0, par2Random, (x << 4) + 2, (z << 4) + 2, list, terrainType
		);
        this.components.add(var7);
        var7.buildComponent(var7, this.components, par2Random);
        final ArrayList<Object> var8 = var7.field_74930_j;
        final ArrayList<Object> var9 = var7.field_74932_i;
        int var10;

        while (!var8.isEmpty() || !var9.isEmpty())
        {
            StructureComponent var11;

            if (var8.isEmpty())
            {
                var10 = par2Random.nextInt(var9.size());
                var11 = (StructureComponent) var9.remove(var10);
                var11.buildComponent(var7, this.components, par2Random);
            }
            else
            {
                var10 = par2Random.nextInt(var8.size());
                var11 = (StructureComponent) var8.remove(var10);
                var11.buildComponent(var7, this.components, par2Random);
            }
        }

        this.updateBoundingBox();
        var10 = 0;
        final Iterator<StructureComponent> var13 = this.components.iterator();

        while (var13.hasNext())
        {
            final StructureComponent var12 = var13.next();

            if (!(var12 instanceof ARVillageComponentRoadPiece))
            {
                ++var10;
            }
        }
    }
    

    /**
     * currently only defined for Villages, returns true if Village has more
     * than 2 non-road components
     */
    @Override
    public boolean isSizeableStructure()
    {
        return true;
    }
}
