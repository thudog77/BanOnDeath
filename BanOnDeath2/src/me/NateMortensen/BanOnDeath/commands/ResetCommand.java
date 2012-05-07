package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * 
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class ResetCommand implements BODCommand {

    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("You must specify a player to reset.");
            return;
        }
        /* After looking at the code and limitations in primarily Bukkit, it isn't possible to reset the lives of an offline player,
        as it isn't possible to get the permissions of an offline player. 
        - Nate
        */
        if (plugin.isOnline(args[0])){
        	plugin.resetLives(plugin.getServer().getPlayer(args[0]));
        	return;
        }
        sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "This player is not currently online, and as such their lives cannot be reset.");
    }

    public String getPermissionNode() {
        return "bod.reset";
    }

    public String[] getNames() {
        return new String[]{"reset"};
    }

    public String getSyntax() {
        return "<player>";
    }
}
