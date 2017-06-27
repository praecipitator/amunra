package de.katzenpapst.amunra.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.collect.Lists;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandCelestialBodyInfo extends CommandBase {

    public CommandCelestialBodyInfo() {
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "find_celestial_body";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName() + " <name>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws WrongUsageException {
        if(args.length < 1) {
            throw new WrongUsageException("Not enough arguments, usage: "+this.getCommandUsage(sender));
        }

        List<CelestialBody> foundBodies = new ArrayList<>();

        String name = args[0].toLowerCase();
        List<CelestialBody> celestialBodyList = Lists.newArrayList();
        celestialBodyList.addAll(GalaxyRegistry.getRegisteredMoons().values());
        celestialBodyList.addAll(GalaxyRegistry.getRegisteredPlanets().values());

        // ClientProxyCore.mc.thePlayer
        // look here first
        for(CelestialBody body: celestialBodyList) {
            if(body.getLocalizedName().toLowerCase().contains(name)) {
                foundBodies.add(body);
            }
        }


        Collection<SolarSystem> systems = GalaxyRegistry.getRegisteredSolarSystems().values();
        for(SolarSystem sys: systems) {
            if(sys.getMainStar().getLocalizedName().toLowerCase().contains(name)) {
                foundBodies.add(sys.getMainStar());
            }
        }
        if(foundBodies.isEmpty()) {
//            GCCoreUtil.translate("gui.energyStorage.desc.0")
            //sender.addChatMessage(new ChatComponentTranslation("info.message.celestialbodysearch.none", name));
            sender.addChatMessage(new ChatComponentTranslation("info.message.celestialbodysearch.none", name));
        } else {
            sender.addChatMessage(new ChatComponentTranslation("info.message.celestialbodysearch.some", foundBodies.size(), name));

            for(CelestialBody body: foundBodies) {
                StringBuilder sb = new StringBuilder();

                sb.append(body.getLocalizedName());
                sb.append(" : ");
                sb.append(body.getName());

                sender.addChatMessage(new ChatComponentText(sb.toString()));
            }

        }




        /*sender.
        for(Planet p: planets) {
            p.getLocalizedName()
        }*/
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }
}
