package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to put a player into/out of god mode. Sort of out of place in a
 * plugin like this.
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class GodCommand implements BODCommand {

    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You have to be a player to god yourself.");
                return;
            }

            //Toggling godmode on yourself:
            final String playerToGod = sender.getName();
            if (toggleGodMode(plugin, playerToGod)) {
                sender.sendMessage("You are now invincible.");
            } else {
                sender.sendMessage("You have been un-godded");
            }
        } else {
            //Toggling godmode on a specified player:
            final String playerToGod = args[0];
            if (toggleGodMode(plugin, playerToGod)) {
                sender.sendMessage(playerToGod + " has been godded.");
            } else {
                sender.sendMessage(playerToGod + " has been un-godded");
            }
        }
    }

    private boolean toggleGodMode(final BanOnDeath plugin, final String player) {
        final String playerLower = player.toLowerCase();
        if (plugin.godded.contains(playerLower)) {
            plugin.godded.remove(playerLower);
            return false;
        } else {
            plugin.godded.add(playerLower);
            return true;
        }
    }

    public String getPermissionNode() {
        return "bod.god";
    }

    public String[] getNames() {
        return new String[]{"god", "ungod"};
    }

    public String getSyntax() {
        return "<player>";
    }
}
