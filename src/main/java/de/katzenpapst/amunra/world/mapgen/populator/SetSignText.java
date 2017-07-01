package de.katzenpapst.amunra.world.mapgen.populator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class SetSignText extends AbstractPopulator {

    private String[] signText = new String[] { "", "", "", "" };

    public SetSignText(BlockPos pos, String text) {
        this(pos, text.split("\n", 4));
    }

    public SetSignText(BlockPos pos, String[] signText) {
        super(pos);

        for (int i = 0; i < signText.length; i++) {
            if (i >= 4) {
                break;
            }
            if (signText[i] != null) {
                this.signText[i] = signText[i];
            }
        }

    }

    @Override
    public boolean populate(World world) {
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() == Blocks.standing_sign || state.getBlock() == Blocks.wall_sign) {
            TileEntitySign sign = (TileEntitySign) world.getTileEntity(pos);

            if (sign != null) {

                for(int i=0;i < Math.min(4, signText.length);i++) {
                    if(signText[i] != null) {
                        sign.signText[i] = new ChatComponentText(signText[i]);
                    }
                }

                sign.markDirty();
                world.markBlockForUpdate(pos);
                return true;
            }
        }
        return false;
    }

}
