package de.katzenpapst.amunra.world.mapgen.newVillage;

import net.minecraft.block.Block;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class GridVillageComponent {
	
	// final public static GridVillageComponent dummyComponent = new GridVillageComponent();
	
	protected GridVillageStart parent = null;
	private StructureBoundingBox structBB;
	
	public void setStructureBoundingBox(StructureBoundingBox structBB) {
		this.structBB = structBB;
	}
	
	public StructureBoundingBox getStructureBoundingBox() {
		return this.structBB;
	}
	
	protected boolean generateChunk(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta) {		
		return true;
	}
	
	protected void setParent(GridVillageStart parent) {
		this.parent = parent;
	}
	
	/**
	 * Rotates the metadata of a solar collector
	 * @param unrotated
	 * @param coordMode
	 * @return
	 */
	public static int rotateSolarMetadata(int unrotated, int coordMode) {
        // 0 -> direct
        // 1 -> rotate by 90° CCW, aka N turns to W, W to S, etc
        // 2 -> Z is flipped aka mirror at x
        // 3 -> coordflip, swap N <--> O and S <--> W
    	/* now I think: 
    	 * 0 -> S, 
    	 * 1 -> N, 
    	 * 2 -> O, 
    	 * 3 -> W
    	 */
    	switch(coordMode) {
    	case 0:
    		return unrotated;
    	case 1:
    		switch(unrotated) {
    		case 0:
    			return 2;
    		case 1:
    			return 3;
    		case 2:
    			return 1;
    		case 3:
    			return 0; 
    		}
    		break;
    	case 2:
    		switch(unrotated) {
    		case 0:
    			return 1;
    		case 1: 
    			return 0;
    		case 2:
    		case 3:
    			return unrotated;
    		}
    		break;
    	case 3:
    		switch(unrotated) {
    		case 0:
    			return 3;
    		case 1:
    			return 2;
    		case 2:
    			return 1;
    		case 3:
    			return 0; 
    		}

    	}
    	return unrotated;
    }
}
