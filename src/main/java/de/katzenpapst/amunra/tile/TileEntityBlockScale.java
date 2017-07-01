package de.katzenpapst.amunra.tile;

import de.katzenpapst.amunra.helper.BlockMassHelper;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;

public class TileEntityBlockScale extends TileEntity implements ITickable {

    protected long ticks = 0;
    protected float massToDisplay = 0;
    protected BlockMetaPair lastFoundBlock = null;

    public TileEntityBlockScale() {

    }

    public int getRotationMeta() {
        // fuck
        /*
        Block b = worldObj.getBlock(xCoord, yCoord, zCoord);
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        if(b instanceof IMetaBlock) {
            return ((IMetaBlock)b).getRotationMeta(meta);
        }*/
        return 0;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound data = new NBTTagCompound();
        this.writeToNBT(data);
        return new S35PacketUpdateTileEntity(this.getPos(), 2, data);
    }

    @Override
    public void onDataPacket(NetworkManager netManager, S35PacketUpdateTileEntity packet)
    {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setFloat("mass", this.massToDisplay);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.massToDisplay = nbt.getFloat("mass");
    }

    @Override
    public void update() {
        if(this.worldObj.isRemote) {
            return;
        }
        this.ticks++;

        if(ticks % 80 == 0) {
            doUpdate();
        }
    }

    public void doUpdate() {
        BlockPos above = getPos().up();
        IBlockState state = worldObj.getBlockState(above);

        Block b = state.getBlock();//this.worldObj.getBlock(xCoord, yCoord+1, zCoord);
        int meta = b.getMetaFromState(state);//this.worldObj.getBlockMetadata(xCoord, yCoord+1, zCoord);

        if(lastFoundBlock != null && lastFoundBlock.getBlock() == b && lastFoundBlock.getMetadata() == meta) {
            // nothing changed
            return;
        }

        lastFoundBlock = new BlockMetaPair(b, (byte) meta);


        // mass
        massToDisplay = BlockMassHelper.getBlockMass(worldObj, b, meta, above);
        this.worldObj.markBlockForUpdate(this.getPos());
        //this.markDirty();
    }

    public float getCurrentMass() {
        return massToDisplay;
    }

}
