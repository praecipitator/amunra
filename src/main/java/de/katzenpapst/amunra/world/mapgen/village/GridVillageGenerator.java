package de.katzenpapst.amunra.world.mapgen.village;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.util.MathHelper;
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
		int rangeShift = 5;
		int range = 1 << rangeShift;
		int superchunkX = chunkX >> rangeShift;
		int superchunkZ = chunkZ >> rangeShift;

		int chunkStartX = superchunkX << rangeShift;
		int chunkStartZ = superchunkZ << rangeShift;
		int chunkEndX = chunkStartX+range-1;
		int chunkEndZ = chunkStartZ+range-1;
		// this square of chunk coords superchunkX,superchunkX+range-1 and superchunkZ,superchunkZ+range-1
		// now could contain a village
		this.rand.setSeed(this.worldObj.getSeed() ^ this.getSalt() ^ superchunkX ^ superchunkZ);

		int actualVillageX = MathHelper.getRandomIntegerInRange(this.rand, chunkStartX, chunkEndX);
		int actualVillageZ = MathHelper.getRandomIntegerInRange(this.rand, chunkStartZ, chunkEndZ);

		return (chunkX == actualVillageX && chunkZ == actualVillageZ);


		/*
		final long randX = chunkX * getSalt();
        final long randZ = chunkZ * getSalt();
        this.rand.setSeed(randX ^ randZ ^ this.worldObj.getSeed());
		return this.rand.nextInt(700) == 0;*/
	}

	@Override
	protected BaseStructureStart createNewStructure(int xChunkCoord, int zChunkCoord) {

		Random rand4structure = new Random(this.worldObj.getSeed() ^ this.getSalt() ^ xChunkCoord ^ zChunkCoord);

		GridVillageStart start = new GridVillageStart(this.worldObj, xChunkCoord, zChunkCoord, rand4structure);
		ArrayList compList = new ArrayList();
			// now prepare the actual component list
		for(ComponentEntry entry: components) {
			try {
				// generate the minimum amount
				GridVillageComponent cmp = null;
				int nrGenerated = 0;
				boolean shouldGenerateMore = true;

				// now generate the extra
				while(shouldGenerateMore) {
					shouldGenerateMore = false;
					if(entry.minAmount > 0 && nrGenerated < entry.minAmount) {
						shouldGenerateMore = true;
					} else {
						if(rand4structure.nextFloat() < entry.probability) {
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
