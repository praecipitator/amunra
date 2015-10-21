package de.katzenpapst.amunra.world.mapgen.village;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class ARVillagePieces {
	public static ArrayList<ARVillagePieceWeight> getStructureVillageWeightedPieceList(Random par0Random, int par1)
    {
        final ArrayList<ARVillagePieceWeight> var2 = new ArrayList<ARVillagePieceWeight>();
        //var2.add(new ARVillagePieceWeight(StructureComponentVillageWoodHut.class, 5, MathHelper.getRandomIntegerInRange(par0Random, 2 + par1, 5 + par1 * 3)));
        var2.add(new ARVillagePieceWeight(ComponentField.class, 5, MathHelper.getRandomIntegerInRange(par0Random, 3 + par1, 5 + par1)));
        var2.add(new ARVillagePieceWeight(ARVillageComponentHouse.class, 5, MathHelper.getRandomIntegerInRange(par0Random, 3 + par1, 8 + par1 * 2)));

        final Iterator<ARVillagePieceWeight> var3 = var2.iterator();

        while (var3.hasNext())
        {
            if (var3.next().villagePiecesLimit == 0)
            {
                var3.remove();
            }
        }

        return var2;
    }

	/**
	 * This seems to check whenever there are pieces which could be added, returning the sum of the weight
	 * of the pieces which can be added if yes, or -1 if no
	 * 
	 * @param par0List
	 * @return
	 */
    private static int canGenerateMore(List<ARVillagePieceWeight> par0List)
    {
        boolean canGenerateMore = false;
        int var2 = 0;
        ARVillagePieceWeight curPieceWeight;

        for (final Iterator<ARVillagePieceWeight> var3 = par0List.iterator(); var3.hasNext(); var2 += curPieceWeight.villagePieceWeight)
        {
            curPieceWeight = var3.next();

            if (curPieceWeight.villagePiecesLimit > 0 && curPieceWeight.villagePiecesSpawned < curPieceWeight.villagePiecesLimit)
            {
                canGenerateMore = true;
            }
        }

        return canGenerateMore ? var2 : -1;
    }

    /**
     * 
     * @param startPiece
     * @param pieceWeight
     * @param par2List
     * @param par3Random
     * @param x
     * @param y
     * @param z
     * @param coordBaseMode
     * @param type
     * @return
     */
    private static ARVillageComponent generateComponent(ARVillageComponentStartPiece startPiece, ARVillagePieceWeight pieceWeight, List<StructureComponent> par2List, Random par3Random, int x, int y, int z, int coordBaseMode, int type)
    {
    	final Class<? extends ARVillageComponent> componentClass = pieceWeight.villagePieceClass;
    	
    	try {
    		StructureBoundingBox structureBB = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 17, 9, 17, coordBaseMode);
			Constructor <? extends ARVillageComponent> ctr = componentClass.getConstructor();
    		ARVillageComponent component = ctr.newInstance();
			component.init(startPiece, type, structureBB, coordBaseMode);
    		return component;
			
		} catch (Exception e) {

	        FMLLog.info("Failed instantiating component "+componentClass.getCanonicalName());
			e.printStackTrace();
		}
    	return null;
    	
    	
    }

    /**
     * 
     * @param curStartPiece
     * @param par1List
     * @param rand
     * @param x	those numbers are coordinates of some kind
     * @param y
     * @param z
     * @param coordBaseMode
     * @param type
     * @return
     */
    private static ARVillageComponent getNextVillageComponent(ARVillageComponentStartPiece curStartPiece, List<StructureComponent> par1List, Random rand, int x, int y, int z, int coordBaseMode, int type)
    {
        final int extraPiecesWeight = ARVillagePieces.canGenerateMore(curStartPiece.structureVillageWeightedPieceList);

        if (extraPiecesWeight <= 0)
        {
            return null;
        }
        else
        {
            int i = 0;

            while (i < 5)
            {
                ++i;
                int curWeight = rand.nextInt(extraPiecesWeight);
                final Iterator<ARVillagePieceWeight> piecesIterator = curStartPiece.structureVillageWeightedPieceList.iterator();

                while (piecesIterator.hasNext())
                {
                    final ARVillagePieceWeight curPiece = piecesIterator.next();
                    curWeight -= curPiece.villagePieceWeight;

                    if (curWeight < 0)
                    {
                        if (!curPiece.canSpawnMoreVillagePiecesOfType(type) || curPiece == curStartPiece.structVillagePieceWeight && curStartPiece.structureVillageWeightedPieceList.size() > 1)
                        {
                            break;
                        }

                        final ARVillageComponent var13 = ARVillagePieces.generateComponent(curStartPiece, curPiece, par1List, rand, x, y, z, coordBaseMode, type);

                        if (var13 != null)
                        {
                            ++curPiece.villagePiecesSpawned;
                            curStartPiece.structVillagePieceWeight = curPiece;

                            if (!curPiece.canSpawnMoreVillagePieces())
                            {
                                curStartPiece.structureVillageWeightedPieceList.remove(curPiece);
                            }

                            return var13;
                        }
                    }
                }
            }
        	/*
            final StructureBoundingBox var14 = StructureComponentVillageTorch.func_74904_a(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6);

            if (var14 != null)
            {
                return new StructureComponentVillageTorch(par0ComponentVillageStartPiece, par7, par2Random, var14, par6);
            }
            else
            {
                return null;
            }*/
            return null; // ?
        }
    }

    /**
     * attempts to find a next Structure Component to be spawned, private
     * Village function
     * 
     * ComponentType might be a misnomer after all
     */
    private static StructureComponent getNextVillageStructureComponent(ARVillageComponentStartPiece startPiece, List<StructureComponent> list, Random rand, 
    		int x, int y, int z, int coordBaseMode, int componentType)
    {
        if (componentType > 50)
        {
            return null;
        }
        else if (Math.abs(x - startPiece.getBoundingBox().minX) <= 112 && Math.abs(z - startPiece.getBoundingBox().minZ) <= 112)
        {
            final ARVillageComponent var8 = ARVillagePieces.getNextVillageComponent(startPiece, list, rand, x, y, z, coordBaseMode, componentType + 1);

            if (var8 != null)
            {
                list.add(var8);
                startPiece.field_74932_i.add(var8);
                return var8;
            }

            return null;
        }
        else
        {
            return null;
        }
    }

    private static StructureComponent getNextComponentVillagePath(ARVillageComponentStartPiece startPiece, List<StructureComponent> par1List, Random par2Random, int par3, int par4, int par5, int par6, int par7)
    {
        if (par7 > 3 + startPiece.terrainType)
        {
            return null;
        }
        else if (Math.abs(par3 - startPiece.getBoundingBox().minX) <= 112 && Math.abs(par5 - startPiece.getBoundingBox().minZ) <= 112)
        {
            final StructureBoundingBox var8 = ARVillageComponentPathGen.func_74933_a(startPiece, par1List, par2Random, par3, par4, par5, par6);

            if (var8 != null && var8.minY > 10)
            {
                final ARVillageComponentPathGen var9 = new ARVillageComponentPathGen(startPiece, par7, par2Random, var8, par6);

                par1List.add(var9);
                startPiece.field_74930_j.add(var9);
                return var9;
            }

            return null;
        }
        else
        {
            return null;
        }
    }

    /**
     * attempts to find a next Structure Component to be spawned
     * par1ComponentVillageStartPiece, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.minY + offsetY, this.boundingBox.minZ + lengthMaybe, 1, this.getComponentType());
     */
    static StructureComponent getNextStructureComponent(ARVillageComponentStartPiece startPiece, List<StructureComponent> par1List, Random rand, int x, int y, int z, int coordBaseMode, int componentType)
    {
        return ARVillagePieces.getNextVillageStructureComponent(startPiece, par1List, rand, x, y, z, coordBaseMode, componentType);
    }

    static StructureComponent getNextStructureComponentVillagePath(ARVillageComponentStartPiece startPiece, List<StructureComponent> par1List, Random rand, int x, int y, int z, int coordBaseMode, int componentType)
    {
        return ARVillagePieces.getNextComponentVillagePath(startPiece, par1List, rand, x, y, z, coordBaseMode, componentType);
    }
}
