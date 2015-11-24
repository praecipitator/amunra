package de.katzenpapst.amunra.world.mapgen.pyramid;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.util.MathHelper;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.world.mapgen.BaseStructureStart;
import de.katzenpapst.amunra.world.mapgen.StructureGenerator;



public class PyramidGenerator extends StructureGenerator {

	protected BlockMetaPair wallMaterial = ARBlocks.blockAluCrate;
	protected BlockMetaPair floorMaterial = ARBlocks.blockSmoothBasalt;
	protected BlockMetaPair fillMaterial = ARBlocks.blockBasaltBrick;

	protected ArrayList<SubComponentData> components = new ArrayList<SubComponentData>();
	protected ArrayList<SubComponentData> potentialMainRooms = new ArrayList<SubComponentData>();

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
	}

	public void addComponentType(Class<? extends PyramidRoom> clazz, float probability) {
		addComponentType(clazz, probability, 0, 0);
	}

	public void addComponentType(Class<? extends PyramidRoom> clazz, float probability, int minAmount, int maxAmount) {
		SubComponentData entry = new SubComponentData(clazz, probability, minAmount, maxAmount);
		components.add(entry);
	}

	public void addMainRoomType(Class<? extends PyramidRoom> clazz, float probability) {
		SubComponentData entry = new SubComponentData(clazz, probability, 0, 0);
		potentialMainRooms.add(entry);
	}

	@Override
	protected BaseStructureStart createNewStructure(int xChunkCoord,
			int zChunkCoord) {
		Pyramid p =  new Pyramid(this.worldObj, xChunkCoord, zChunkCoord, this.rand);
		p.setFillMaterial(fillMaterial);
		p.setFloorMaterial(floorMaterial);
		p.setWallMaterial(wallMaterial);

		Random rand4structure = new Random(this.worldObj.getSeed() ^ this.getSalt() ^ xChunkCoord ^ zChunkCoord);


		ArrayList compList = generateSubComponents(components, rand4structure, 12);

		p.setSmallRooms(compList);

		p.setMainRoom((PyramidRoom) this.generateOneComponent(potentialMainRooms, rand4structure));
		//p.setMainRoom(new PyramidRoom());

		return p;
	}

	@Override
	public String getName() {
		return "Pyramid";
	}

	@Override
	protected long getSalt() {
		return 549865610521L;
	}

	public BlockMetaPair getWallMaterial() {
		return wallMaterial;
	}

	public void setWallMaterial(BlockMetaPair wallMaterial) {
		this.wallMaterial = wallMaterial;
	}

	public BlockMetaPair getFloorMaterial() {
		return floorMaterial;
	}

	public void setFloorMaterial(BlockMetaPair floorMaterial) {
		this.floorMaterial = floorMaterial;
	}

	public BlockMetaPair getFillMaterial() {
		return fillMaterial;
	}

	public void setFillMaterial(BlockMetaPair fillMaterial) {
		this.fillMaterial = fillMaterial;
	}



}
