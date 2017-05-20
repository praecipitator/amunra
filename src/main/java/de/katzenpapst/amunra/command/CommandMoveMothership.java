package de.katzenpapst.amunra.command;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandMoveMothership extends CommandBase {

    @Override
    public String getCommandName() {
        return "mothership_move";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName() + " <name> [<travel time>]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        long travelTime = 100;

        if(!(sender.getEntityWorld().provider instanceof MothershipWorldProvider)) {
            throw new WrongUsageException("You are not on a mothership");
        }

        if(args.length < 1) {
            throw new WrongUsageException("Not enough arguments, usage: "+this.getCommandUsage(sender));
        }

        Mothership mShip = (Mothership) ((MothershipWorldProvider)sender.getEntityWorld().provider).getCelestialBody();
        String targetName = args[0];

        if(args.length >= 2) {
            travelTime = Integer.parseInt(args[1]);
            if(travelTime < 1) {
                throw new WrongUsageException("Travel time must be at least 1!");
            }

        }

        CelestialBody targetBody = Mothership.findBodyByString(targetName);
        if(targetBody == null) {
            throw new WrongUsageException("Found no body for "+targetName);
        }

        // apparently this happens on the server side
        if(mShip.getWorldProviderServer().startTransit(targetBody, true)) {
            AmunRa.packetPipeline.sendToAll(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.C_MOTHERSHIP_TRANSIT_STARTED, mShip.getID(), Mothership.getOrbitableBodyName(targetBody), travelTime));
        } else {
            throw new WrongUsageException("Starting transit failed");
        }

    }

}
