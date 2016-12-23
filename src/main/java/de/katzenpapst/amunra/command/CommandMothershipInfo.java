package de.katzenpapst.amunra.command;

import java.util.HashMap;
import java.util.Map;

import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import de.katzenpapst.amunra.tick.TickHandlerServer;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

public class CommandMothershipInfo extends CommandBase {

    @Override
    public String getCommandName() {
        // TODO Auto-generated method stub
        return "mothership_info";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "/" + this.getCommandName() + " <name>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] params) {
        // no params
        HashMap<Integer, Mothership> data = TickHandlerServer.mothershipData.getMotherships();
        Mothership playerMS = null;

        if(sender.getEntityWorld().provider instanceof MothershipWorldProvider) {
            playerMS = (Mothership) ((MothershipWorldProvider)sender.getEntityWorld().provider).getCelestialBody();
        }


        if(data.size() <= 0) {
            sender.addChatMessage(new ChatComponentText("No registered motherships"));
        } else {
            sender.addChatMessage(new ChatComponentText(data.size()+" registered motherships"));


            for(Map.Entry<Integer, Mothership> entry: data.entrySet()) {
                Mothership ship = entry.getValue();
                int id = entry.getKey();
                StringBuilder sb = new StringBuilder();

                if(playerMS == ship) {
                    sb.append(">");
                } else {
                    sb.append(" ");
                }

                sb.append("#");
                sb.append(id);
                sb.append(", ID ");
                sb.append(ship.getID());
                sb.append(", '");
                sb.append(ship.getLocalizedName());
                sb.append("', DIM ");
                sb.append(ship.getDimensionID());
                sb.append(", ");
                if(ship.isInTransit()) {
                    sb.append("in transit to ");
                    sb.append(ship.getDestination().getName());
                } else {
                    sb.append("orbiting ");
                    sb.append(ship.getDestination().getName());
                }
                sender.addChatMessage(new ChatComponentText(sb.toString()));
            }
        }
    }

}
