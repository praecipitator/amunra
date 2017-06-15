package de.katzenpapst.amunra.block.machine;

import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public abstract class AbstractBlockMothershipRestricted extends SubBlockMachine {

    public AbstractBlockMothershipRestricted(String name, String texture) {
        super(name, texture);
        // TODO Auto-generated constructor stub
    }

    public AbstractBlockMothershipRestricted(String name, String texture, String tool, int harvestLevel) {
        super(name, texture, tool, harvestLevel);
        // TODO Auto-generated constructor stub
    }

    public AbstractBlockMothershipRestricted(
            String name,
            String texture,
            String tool,
            int harvestLevel,
            float hardness,
            float resistance) {
        super(name, texture, tool, harvestLevel, hardness, resistance);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        if(world.provider instanceof MothershipWorldProvider) {

            if( ((MothershipWorldProvider)world.provider).isPlayerUsagePermitted(entityPlayer) ) {
                openGui(world, x, y, z, entityPlayer);

                return true;
            } else {
                if(world.isRemote) {
                    entityPlayer.addChatMessage(new ChatComponentTranslation("gui.message.mothership.chat.wrongUser"));
                }
                return false;
            }
        }
        if(world.isRemote) {
            entityPlayer.addChatMessage(new ChatComponentTranslation("gui.message.mothership.chat.notOnShip"));
        }

        return false;
    }

    protected abstract void openGui(World world, int x, int y, int z, EntityPlayer entityPlayer);

}
