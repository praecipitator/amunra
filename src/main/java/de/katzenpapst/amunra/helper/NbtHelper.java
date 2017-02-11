package de.katzenpapst.amunra.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;

public class NbtHelper {

    public static NBTTagCompound getAsNBT(AxisAlignedBB aabb)
    {
        NBTTagCompound result = new NBTTagCompound();
        writeToNBT(result, aabb);
        return result;
    }

    public static void writeToNBT(NBTTagCompound nbt, AxisAlignedBB aabb)
    {
        nbt.setDouble("minX", aabb.minX);
        nbt.setDouble("minY", aabb.minY);
        nbt.setDouble("minZ", aabb.minZ);

        nbt.setDouble("maxX", aabb.maxX);
        nbt.setDouble("maxY", aabb.maxY);
        nbt.setDouble("maxZ", aabb.maxZ);
    }

    public static AxisAlignedBB readAABB(NBTTagCompound nbt)
    {
        return AxisAlignedBB.getBoundingBox(
                nbt.getDouble("minX"),
                nbt.getDouble("minY"),
                nbt.getDouble("minZ"),
                nbt.getDouble("maxX"),
                nbt.getDouble("maxY"),
                nbt.getDouble("maxZ")
                );

    }

    public static ItemStack[] readInventory(NBTTagCompound nbt, int inventorySize)
    {
        final NBTTagList itemTag = nbt.getTagList("Items", 10);
        ItemStack[] result = new ItemStack[inventorySize];

        for (int i = 0; i < itemTag.tagCount(); ++i)
        {
            final NBTTagCompound stackNbt = itemTag.getCompoundTagAt(i);
            final int slotNr = stackNbt.getByte("Slot") & 255;

            if (slotNr < inventorySize)
            {
                result[slotNr] = ItemStack.loadItemStackFromNBT(stackNbt);
            }
        }
        return result;
    }

    public static void writeInventory(NBTTagCompound nbt, ItemStack[] inventory)
    {
        final NBTTagList list = new NBTTagList();
        int length = inventory.length;


        for (int i = 0; i < length; ++i)
        {
            if (inventory[i] != null)
            {
                final NBTTagCompound stackNbt = new NBTTagCompound();
                stackNbt.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(stackNbt);
                list.appendTag(stackNbt);
            }
        }

        nbt.setTag("Items", list);
    }

}
