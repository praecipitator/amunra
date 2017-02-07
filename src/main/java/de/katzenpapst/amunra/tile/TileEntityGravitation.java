package de.katzenpapst.amunra.tile;

import java.util.List;

import de.katzenpapst.amunra.AmunRa;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityGravitation extends TileEntity {

    protected double range = 5.5;

    protected Vector3 gravityVector;

    public TileEntityGravitation() {
        gravityVector =  new Vector3(0.0, -0.03999999910593033D, 0.0);
    }

    @Override
    public void updateEntity() {

        // now try stuff
        Vector3 center = new Vector3(xCoord+0.5D, yCoord+0.5D, zCoord+0.5D);

        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(center.x - range, center.y - 0.5, center.z - range, center.x + range, center.y + range, center.z + range);

        if(!worldObj.isRemote) {
            final List<?> list = this.worldObj.getEntitiesWithinAABB(Entity.class, box);

            for(Object e: list) {
                Entity ent = (Entity)e;
                /*if(ent.motionY <= 0) {
                    ent.motionY = 0.01;
                }
                ent.motionY *= 0.91;*/
                //ent.motionY -= 0.03999999910593033D;

                ent.addVelocity(gravityVector.x, gravityVector.y, gravityVector.z);
                /*if(ent instanceof EntityPlayerMP) {
                    GCPlayerStats stats = GCPlayerStats.get((EntityPlayerMP)ent);
                    stats.
                }*/
            }
        } else {
            // player stuff has to be done on client
            final List<?> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, box);
            for(Object e: list) {
                EntityPlayer p = (EntityPlayer)e;
                AmunRa.proxy.handlePlayerArtificalGravity(p, gravityVector);
            }
        }
    }

}
