package de.katzenpapst.amunra.world.mapgen.village;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import de.katzenpapst.amunra.world.mapgen.BaseStructureStart;
import de.katzenpapst.amunra.world.mapgen.StructureGenerator;

public class GridVillageGenerator extends StructureGenerator {

	protected class ComponentEntry {
		public Class<? extends GridVillageComponent> clazz;
		public float probability;
		public int minAmount;
		public int maxAmount;

		public ComponentEntry(Class<? extends GridVillageComponent> clazz, float probability, int minAmount, int maxAmount) {
			this.clazz = clazz;
			this.probability = probability;
			this.minAmount = minAmount;
			this.maxAmount = maxAmount;
		}
	}

	protected ArrayList<ComponentEntry> components;

	protected int gridSize = 32;

	protected HashMap<Long, GridVillageStart> structureMap = new HashMap<Long, GridVillageStart>(); //Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(p_151538_2_, p_151538_3_)

	public void addComponentType(Class<? extends GridVillageComponent> clazz, float probability) {
		addComponentType(clazz, probability, 0, 0);
	}

	public void addComponentType(Class<? extends GridVillageComponent> clazz, float probability, int minAmount, int maxAmount) {
		ComponentEntry entry = new ComponentEntry(clazz, probability, minAmount, maxAmount);
		components.add(entry);
	}

	public GridVillageGenerator() {
		components = new ArrayList<ComponentEntry>();
	}


	@Override
	protected boolean canGenerateHere(int chunkX, int chunkZ, Random rand) {
		this.rand.setSeed(this.worldObj.getSeed());

		final long randX = chunkX * getSalt();
        final long randZ = chunkZ * getSalt();
        this.rand.setSeed(randX ^ randZ ^ this.worldObj.getSeed());
		return this.rand.nextInt(700) == 0;
	}

	@Override
	protected BaseStructureStart createNewStructure(int xChunkCoord,
			int zChunkCoord) {
		GridVillageStart start = new GridVillageStart(this.worldObj, xChunkCoord, zChunkCoord, this.rand);
		ArrayList compList = new ArrayList();
			// now prepare the actual component list
		for(ComponentEntry entry: components) {
			try {
				// generate the minimum amount
				GridVillageComponent cmp = null;
				int nrGenerated = 0;
				boolean shouldGenerateMore = true;
				/*
				for(int i=0;i<entry.minAmount;i++) {
					cmp = entry.clazz.getConstructor().newInstance();
					start.addComponent(cmp);
					nrGenerated = i;
				}*/
				// now generate the extra
				while(shouldGenerateMore) {
					shouldGenerateMore = false;
					if(entry.minAmount > 0 && nrGenerated < entry.minAmount) {
						shouldGenerateMore = true;
					} else {
						if(this.rand.nextFloat() < entry.probability) {
							shouldGenerateMore =  true;
						}
					}

					if(shouldGenerateMore) {
						cmp = entry.clazz.getConstructor().newInstance();
						compList.add(cmp);
						// start.addComponent(cmp);
						nrGenerated++;
					}
					if(nrGenerated >= entry.maxAmount) {
						break;
					}
				}

			} catch (Throwable e) {
				FMLLog.info("Instantiating "+entry.clazz.getCanonicalName()+" failed");
				e.printStackTrace();
			}
		}
		start.setComponents(compList);
		return start;
	}

	@Override
	public String getName() {
		return "GridVillage";
	}

	@Override
	protected long getSalt() {
		return 1098540180186541L;
	}





}
