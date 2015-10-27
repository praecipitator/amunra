package de.katzenpapst.amunra.world.mapgen.village;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.core.blocks.BlockSolar;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.world.mapgen.village.populator.SetSignText;
import de.katzenpapst.amunra.world.mapgen.village.populator.TouchSolarPanel;

public class SolarField extends GridVillageComponent {

	
	@Override
	public boolean generateChunk(int chunkX, int chunkZ, Block[] blocks, byte[] metas) {
		
		// now, how to get the height?
		StructureBoundingBox chunkBB = new StructureBoundingBox(chunkX*16, chunkZ*16, chunkX*16+15, chunkZ*16+15);
		int fallbackGround = this.parent.getGroundLevel();
		if(groundLevel == -1) {
			groundLevel = getAverageGroundLevel(blocks, metas, getStructureBoundingBox(), chunkBB, fallbackGround);
			if(groundLevel == -1) {
				groundLevel = fallbackGround; // but this shouldn't even happen...
			}
		}
		
		StructureBoundingBox myBB = this.getStructureBoundingBox();
		BlockMetaPair mat = this.parent.getWallMaterial();
		BlockMetaPair floor = this.parent.getFloorMaterial();
		BlockMetaPair padding = this.parent.getFillMaterial();
		
		// draw floor first
		int startX = 0;
		int stopX = myBB.getXSize();
		int startZ = 0;
		int stopZ = myBB.getZSize();
		
	
		
		
		int xCenter = (int)Math.ceil((stopX-startX)/2+startX);
		int zCenter = (int)Math.ceil((stopZ-startZ)/2+startZ);
		
		int aluWireMetadata = AmunRa.instance.confAdvancedVillageMachines?1:0;
		
		
		for(int x = startX; x < stopX; x++) {
			for(int z = startZ; z < stopZ; z++) {
				
				//int x = this.translateX(rawX, rawZ);
				//int z = this.translateZ(rawX, rawZ);
				
				
				
				int highestGroundBlock = getHighestSolidBlockInBB(blocks, metas, chunkX, chunkZ, x, z);
				if(highestGroundBlock == -1) {
					continue; // that should mean that we aren't in the right chunk
				}
				
				// now fill
				for(int y=highestGroundBlock-1;y<groundLevel; y++) {
					//padding
					placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, y, z, padding);
				}
				// floor
				placeBlockRel2BB(blocks, metas,chunkX, chunkZ, x, groundLevel-1, z, floor);
				
				
				// clear stuff
				for(int y=groundLevel; y < 255; y++) {
					placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, y, z, Blocks.air, 0);
				}
				
				// place stuff?
				if(x == startX+2 || x == stopX-3) {
					if(z == startZ+2) {
						// place collectors, facing towards +z
						/*if(placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel, z, GCBlocks.solarPanel, this.rotateStandardMetadata(0, this.coordMode))) {
							
							this.parent.addPopulator(new TouchSolarPanel(getXWithOffset(x, z), groundLevel, getZWithOffset(x, z)));
						}*/
						placeSolarPanel(blocks, metas, chunkX, chunkZ, x, groundLevel, z, 0);
						
					} else if(z == stopZ-3) {
						// place collectors, facing towards -z
						placeSolarPanel(blocks, metas, chunkX, chunkZ, x, groundLevel, z, 1);
						/*if(placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel, z, GCBlocks.solarPanel, this.rotateStandardMetadata(1, this.coordMode))) {
							this.parent.addPopulator(new TouchSolarPanel(getXWithOffset(x, z), groundLevel, getZWithOffset(x, z)));
						}*/
					} else if(z > startZ+2 && z < stopZ-3) {
						placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel, z, GCBlocks.aluminumWire, aluWireMetadata);
						
					}
				} else if(z == zCenter && x > startX+2 && x < stopX-3) {	
					placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel, z, GCBlocks.aluminumWire, aluWireMetadata);
					
				} else if(x == startX+1 && z == zCenter){
					// ok now how to rotate it?
					// I think the first 2 bits are the orientation
					int storageMetadata = this.rotateStandardMetadata(2, this.coordMode);
					if(AmunRa.instance.confAdvancedVillageMachines) {
						storageMetadata = storageMetadata | 8;
					}
					placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, groundLevel, z, GCBlocks.machineTiered, storageMetadata);
				}
				
			}
		}
		
		
		return true;
		
	}
	
	private void placeSolarPanel(Block[] blocks, byte[] metas, int chunkX, int chunkZ, int x, int y, int z, int meta) {
		int rotationMetadata = this.rotateStandardMetadata(meta, this.coordMode);
		if(AmunRa.instance.confAdvancedVillageMachines) {
			rotationMetadata = rotationMetadata | BlockSolar.ADVANCED_METADATA;
		}
		if(placeBlockRel2BB(blocks, metas, chunkX, chunkZ, x, y, z, GCBlocks.solarPanel, rotationMetadata)) {
			
			this.parent.addPopulator(new TouchSolarPanel(getXWithOffset(x, z), groundLevel, getZWithOffset(x, z)));
		}
	}
	
	
}
