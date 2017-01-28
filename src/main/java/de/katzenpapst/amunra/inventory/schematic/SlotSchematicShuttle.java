package de.katzenpapst.amunra.inventory.schematic;

import de.katzenpapst.amunra.item.ItemDamagePair;
import de.katzenpapst.amunra.vec.Vector3int;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSchematicShuttle extends Slot {

    //protected final int index;
    protected final Vector3int pos;
    protected final EntityPlayer player;
    // protected final ItemStack validItem;
    protected final ItemDamagePair[] validItem;

    public SlotSchematicShuttle(IInventory craftMatrix, int slotIndex, int xDisplay, int yDisplay, Vector3int sparkPosition, EntityPlayer player)
    {
        this(craftMatrix, slotIndex, xDisplay, yDisplay, sparkPosition, player, new ItemDamagePair[]{});
    }

    public SlotSchematicShuttle(IInventory craftMatrix, int slotIndex, int xDisplay, int yDisplay, Vector3int sparkPosition, EntityPlayer player, ItemDamagePair... validItems)
    {
        super(craftMatrix, slotIndex, xDisplay, yDisplay);
        //this.index = slotIndex;
        // these coords are only for sparks, I think
        this.pos = sparkPosition;
        this.player = player;
        this.validItem = validItems;
    }


    @Override
    public void onSlotChanged()
    {
        if (this.player instanceof EntityPlayerMP)
        {
            //final Object[] toSend = { this.x, this.y, this.z };

            for (int playerNumber = 0; playerNumber < this.player.worldObj.playerEntities.size(); ++playerNumber)
            {
                final EntityPlayerMP curPlayer = (EntityPlayerMP) this.player.worldObj.playerEntities.get(playerNumber);

                if (curPlayer.dimension == this.player.worldObj.provider.dimensionId)
                {
                    final double distX = this.pos.x - curPlayer.posX;
                    final double distY = this.pos.y - curPlayer.posY;
                    final double distZ = this.pos.z - curPlayer.posZ;

                    if (distX * distX + distY * distY + distZ * distZ < 20 * 20)
                    {
                        GalacticraftCore.packetPipeline.sendTo(new PacketSimple(EnumSimplePacket.C_SPAWN_SPARK_PARTICLES, new Object[] { this.pos.x, this.pos.y, this.pos.z }), curPlayer);
                    }
                }
            }
        }
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        if(this.validItem.length == 0) {
            return true; // all are valid
        }

        for(ItemDamagePair item: validItem) {
            if(item.isSameItem(par1ItemStack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as
     * getInventoryStackLimit(), but 1 in the case of armor slots)
     */
    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }
}

