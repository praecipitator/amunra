package de.katzenpapst.amunra.helper;

import java.util.UUID;

import de.katzenpapst.amunra.AmunRa;
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
        this.userName = player.getDisplayNameString();
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

        if(AmunRa.config.mothershipUserMatchUUID) {
            return ((PlayerID)other).userUUID.equals(userUUID);
        } else {
            return ((PlayerID)other).userName.equals(userName);
        }
    }

    @Override
    public int hashCode() {
        if(AmunRa.config.mothershipUserMatchUUID) {
            return userUUID.hashCode();
        } else {
            return userName.hashCode();
        }
    }

    public boolean isSameUser(EntityPlayer player) {
        if(AmunRa.config.mothershipUserMatchUUID) {
            return this.userUUID.equals(player.getUniqueID());
        } else {
            return this.userName.equals(player.getDisplayName());
        }
    }

}
