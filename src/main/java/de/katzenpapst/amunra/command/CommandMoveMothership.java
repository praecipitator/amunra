package de.katzenpapst.amunra.command;

import de.katzenpapst.amunra.ShuttleTeleportHelper;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandMoveMothership extends CommandBase {

    @Override
    public String getCommandName() {
        return "mothership_move";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName() + " <name>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length < 1) {
            throw new WrongUsageException("Not enough arguments, usage: "+this.getCommandUsage(sender));
        }

        if(!(sender.getEntityWorld().provider instanceof MothershipWorldProvider)) {
            throw new WrongUsageException("You are not on a mothership");
        }

        String targetName = args[0];

        CelestialBody targetBody = ShuttleTeleportHelper.getReachableCelestialBodiesForName(targetName);
        if(targetBody == null) {
            throw new WrongUsageException("Found no body for "+targetName);
        }

        // TODO actually do the transition

    }

}
