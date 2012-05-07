package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to ban a player as if they had been killed in this plugin.
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class BanCommand implements BODCommand {

    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("You must specify a player to ban.");
            return;
        }
        final String playerToLookup = args[0];
        plugin.playersConfig.set(playerToLookup.toLowerCase() + ".lastbantime", System.currentTimeMillis());
        final OfflinePlayer oplayer = plugin.getServer().getOfflinePlayer(playerToLookup);
        if (oplayer.isOnline()) {
            final Player player = oplayer.getPlayer();
            if (player.hasPermission("bod.noban") || player.isOp()) {
                sender.sendMessage("Unable to temprarily ban player due to permissions.");
            } else {
                player.kickPlayer("You were temporarily banned by an administrator.");
            }
        }
        sender.sendMessage("You banned " + playerToLookup + "."); // It's funny because you can't get permissions on offline players so you can't check them for ban immunity
    }

    public String getPermissionNode() {
        return "bod.ban";
    }

    public String[] getNames() {
        return new String[]{"ban"};
    }

    public String getSyntax() {
        return "<player>";
    }
}
