package de.katzenpapst.amunra.entity.spaceship;

import java.util.HashMap;
import java.util.Map.Entry;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.helper.ShuttleTeleportHelper;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/**
 * This is my version of EntityCelestialFake, for special stuff
 * @author katzenpapst
 *
 */
public class EntityShuttleFake extends EntityCelestialFake {

    private String cachedDimList = null;

    public EntityShuttleFake(World world) {
        super(world);
    }

    public EntityShuttleFake(World world, float yOffset) {
        super(world, yOffset);
    }

    public EntityShuttleFake(EntityPlayerMP player, float yOffset) {
        super(player, yOffset);
    }

    public EntityShuttleFake(World world, double x, double y, double z, float yOffset) {
        super(world, x, y, z, yOffset);
    }

    @Override
    public void onUpdate()
    {
        // stuff
        if(!this.worldObj.isRemote) {
            if(ticks % 40 == 0) {
                if(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayerMP) {
                    //System.out.println("would send");
                    // try packet spam
                    EntityPlayerMP player = (EntityPlayerMP)this.riddenByEntity;

                    if(ticks % 160 == 0 || cachedDimList == null) {
                        //System.out.println("would update&send");
                        cachedDimList = getDimList(player);
                    }

                    AmunRa.packetPipeline.sendTo(new PacketSimpleAR(EnumSimplePacket.C_OPEN_SHUTTLE_GUI, player.getGameProfile().getName(), cachedDimList), player);
                }
            }
        }
        super.onUpdate();
    }

    private String getDimList(EntityPlayerMP player) {
        HashMap<String, Integer> map = ShuttleTeleportHelper.getArrayOfPossibleDimensions(player);
        String dimensionList = "";
        int count = 0;
        for (Entry<String, Integer> entry : map.entrySet())
        {
            dimensionList = dimensionList.concat(entry.getKey() + (count < map.entrySet().size() - 1 ? "?" : ""));
            count++;
        }
        return dimensionList;
    }
}
