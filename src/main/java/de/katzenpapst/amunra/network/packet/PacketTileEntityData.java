package de.katzenpapst.amunra.network.packet;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public class PacketTileEntityData extends Packet {

    public int x;
    public int y;
    public int z;
    public NBTTagCompound data;


    public PacketTileEntityData(int x, int y, int z, NBTTagCompound data) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.data = data;
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) throws IOException {
        x = packetBuffer.readInt();
        y = packetBuffer.readInt();
        z = packetBuffer.readInt();
        data = packetBuffer.readNBTTagCompoundFromBuffer();
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) throws IOException {
        packetBuffer.writeInt(x);
        packetBuffer.writeInt(y);
        packetBuffer.writeInt(z);
        packetBuffer.writeNBTTagCompoundToBuffer(data);
    }

    @Override
    public void processPacket(INetHandler handler) {


    }

}
