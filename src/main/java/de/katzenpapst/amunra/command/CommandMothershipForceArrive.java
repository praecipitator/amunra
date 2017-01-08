package de.katzenpapst.amunra.command;

import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandMothershipForceArrive extends CommandBase {

    public CommandMothershipForceArrive() {

    }

    @Override
    public String getCommandName() {
        return "mothership_force_arrival";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "/" + this.getCommandName();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(sender.getEntityWorld().provider instanceof MothershipWorldProvider) {
            MothershipWorldProvider msProvider = ((MothershipWorldProvider)sender.getEntityWorld().provider);

            if(!((Mothership)msProvider.getCelestialBody()).isInTransit()) {
                sender.addChatMessage(new ChatComponentText("Mothership not in transit"));
            } else {
                ((Mothership)msProvider.getCelestialBody()).forceArrival();
            }
        } else {
            sender.addChatMessage(new ChatComponentText("Not on a mothership"));
        }
    }

}
