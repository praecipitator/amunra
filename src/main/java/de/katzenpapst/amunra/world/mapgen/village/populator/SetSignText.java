package de.katzenpapst.amunra.world.mapgen.village.populator;

import net.minecraft.block.BlockSign;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;


public class SetSignText extends AbstractPopulator {
	
	public SetSignText(int x, int y, int z, String text) {
		this(x, y, z, text.split("\n", 4));
	}
	
	public SetSignText(int x, int y, int z, String[] signText) {
		super(x, y, z);
		
		for(int i=0;i<signText.length;i++) {
			if(i >=4 ){
				break;
			}
			if(signText[i] != null) {
				this.signText[i] = signText[i]; 
			}
		}
		
	}
	
	private String[] signText = new String[] {"", "", "", ""};


	@Override
	public boolean populate(World world) {
		world.setBlock(x, y, z, Blocks.standing_sign, 0, 2);
		
		
        TileEntitySign sign = (TileEntitySign) world.getTileEntity(x, y, z);

        if (sign != null)
        {
    		sign.signText = this.signText;
    		sign.markDirty();
    		world.markBlockForUpdate(x, y, z);
        }


		return true;
	}

}
