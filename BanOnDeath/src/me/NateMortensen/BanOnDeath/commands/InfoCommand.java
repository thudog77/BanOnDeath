/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.NateMortensen.BanOnDeath.commands;

import java.util.Date;
import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.command.CommandSender;

/**
 * Command to get information on a player. Shows unban date if applicable, or
 * lives remaining and if applicable.
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class InfoCommand implements BODCommand {

    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("You must specify a player to get info on.");
            return;
        }
        final String targetPlayerName = args[0].toLowerCase();
        if (!plugin.players.contains(targetPlayerName)) {
            sender.sendMessage("Specified player is not currently banned, has never been banned, nor has any lives.");
            return;
        }
        final long bantime = plugin.banTimeoutMillis;
        if (System.currentTimeMillis() - plugin.players.getLong(targetPlayerName + ".lastbantime") < bantime) {
            //long currenttime = System.currentTimeMillis();
            final long timebanned = plugin.players.getLong(targetPlayerName.toLowerCase() + ".lastbantime");
            Date date = new Date(timebanned + bantime);
            //long timeremaining = (bantime - (currenttime -timebanned))/60000;
            sender.sendMessage(targetPlayerName + " Will be unbanned on:  " + date.toString());
        } else if (plugin.players.contains(targetPlayerName + ".lives")) {
            sender.sendMessage(targetPlayerName + " has " + plugin.players.getInt(targetPlayerName.toLowerCase() + ".lives") + " lives remaining");
        } else {
            sender.sendMessage("Specified player is not currently banned, nor has any lives.");
        }
    }

    public String getPermissionNode() {
        return "bod.info";
    }

    public String[] getNames() {
        return new String[]{"info"};
    }

    public String getSyntax() {
        return "<player>";
    }
}
