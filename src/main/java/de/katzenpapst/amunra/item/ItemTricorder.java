package de.katzenpapst.amunra.item;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.katzenpapst.amunra.helper.GuiHelper;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class ItemTricorder extends SubItem {

    public ItemTricorder(String name, String assetName) {
        super(name, assetName);
        // TODO Auto-generated constructor stub
    }

    public ItemTricorder(String name, String assetName, String info) {
        super(name, assetName, info);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(!world.isRemote) {
            return stack;
        }
        // god, sometimes I hate java...
        DecimalFormat twoDForm = new DecimalFormat("#.##");

        float gravity = 1;
        float thermalLevel = 0;
        double solarLevel = 1;
        int dayLength = -1;
        List<String> atmospheres = new ArrayList<String>();
        // do stuff
        if(world.provider instanceof IGalacticraftWorldProvider) {
            gravity = ((IGalacticraftWorldProvider)world.provider).getGravity();
            // convert
            gravity = 1.0F-gravity/0.08F;
            thermalLevel = ((IGalacticraftWorldProvider)world.provider).getThermalLevelModifier();
            //solarLevel = ((IGalacticraftWorldProvider)world.provider).getSolarSize()
            // dayLength = ((IGalacticraftWorldProvider)world.provider).get
            // ((IGalacticraftWorldProvider)world.provider).isGasPresent(gas)
        }

        if(world.provider instanceof ISolarLevel) {
            solarLevel = ((ISolarLevel)world.provider).getSolarEnergyMultiplier();
        }


        if(world.provider instanceof WorldProviderSpace) {
            dayLength = (int) ((WorldProviderSpace)world.provider).getDayLength();
            CelestialBody curBody = ((WorldProviderSpace)world.provider).getCelestialBody();
            for(IAtmosphericGas gas: curBody.atmosphere) {
                atmospheres.add(GuiHelper.getGasName(gas));
            }
        } else{
            if(world.provider.dimensionId == 0) {
                dayLength = 24000;
                for(IAtmosphericGas gas: GalacticraftCore.planetOverworld.atmosphere) {
                    atmospheres.add(GuiHelper.getGasName(gas));
                }
            }
        }

        gravity *= 9.81F;


        // output stuff
        player.addChatComponentMessage(new ChatComponentTranslation("item.baseItem.tricorder.message.gravity", twoDForm.format(gravity)));
        player.addChatComponentMessage(new ChatComponentTranslation("item.baseItem.tricorder.message.temperature", twoDForm.format(thermalLevel)));
        player.addChatComponentMessage(new ChatComponentTranslation("item.baseItem.tricorder.message.solar", twoDForm.format(solarLevel)));
        if(dayLength == -1) {
            player.addChatComponentMessage(new ChatComponentTranslation("item.baseItem.tricorder.message.daylength", new ChatComponentTranslation("item.baseItem.tricorder.message.unknown")));
        } else {
            player.addChatComponentMessage(new ChatComponentTranslation("item.baseItem.tricorder.message.daylength", GuiHelper.formatTime(dayLength, false)));
        }

        if(atmospheres.isEmpty()) {
            player.addChatComponentMessage(new ChatComponentTranslation("item.baseItem.tricorder.message.atmosphere", new ChatComponentTranslation("item.baseItem.tricorder.message.none")));
        } else {
            StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for(String str: atmospheres) {
                if(!isFirst) {
                    builder.append(", ");
                }
                isFirst = false;
                builder.append(str);
            }
            player.addChatComponentMessage(new ChatComponentTranslation("item.baseItem.tricorder.message.atmosphere", builder.toString()));
        }

        //



        /*public float getGravity() {
            return 0.08F * (1-getRelativeGravity());

            val = 0.08F * (1-x);
            1-x = val/0,08
            x = 1-val/0,08
        }*/

        //player.addChatComponentMessage(p_146105_1_);
        return stack;
    }

}
