package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.command.CommandSender;

/**
 * Unban a player from this plugin.
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class UnbanCommand implements BODCommand {

    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("You must specify a player to unban.");
            return;
        }
        final String targetPlayerName = args[0].toLowerCase();
        if (!plugin.players.contains(targetPlayerName)) {
            sender.sendMessage("Player appears to be non-existent.  Check the spelling. ");
            return;
        }

        plugin.players.set(targetPlayerName + ".lastbantime", 0);
        sender.sendMessage(targetPlayerName + " has been unbanned.");
    }

    public String getPermissionNode() {
        return "bod.unban";
    }

    public String[] getNames() {
        return new String[]{"unban", "pardon"};
    }

    public String getSyntax() {
        return "<player>";
    }
}
