package de.katzenpapst.amunra.helper;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerID {

    protected UUID userUUID;
    protected String userName;

    public PlayerID(UUID userUUID, String userName) {
        this.userUUID = userUUID;
        this.userName = userName;
    }

    public PlayerID(EntityPlayer player) {
        this.userUUID = player.getUniqueID();
        this.userName = player.getDisplayName();
    }

    public PlayerID(NBTTagCompound nbt) {
        String uuid = nbt.getString("uuid");
        this.userUUID = UUID.fromString(uuid);
        this.userName = nbt.getString("name");
    }

    public UUID getUUID() {
        return userUUID;
    }

    public String getName() {
        return userName;
    }

    public NBTTagCompound getNbt() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setString("uuid", userUUID.toString());
        nbt.setString("name", userName);

        return nbt;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof PlayerID)) {
            return false;
        }

        return ((PlayerID)other).userName.equals(userName) && ((PlayerID)other).userUUID.equals(userUUID);
    }

    public boolean isSameUser(EntityPlayer player) {
        return this.userUUID.equals(player.getUniqueID())/* && this.userName.equals(player.getDisplayName())*/;
    }

}
