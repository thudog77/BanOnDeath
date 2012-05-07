package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.command.CommandSender;

/**
 * Command to remove lives from a player.
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class TakeCommand implements BODCommand {

    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Try " + BODCommandDispatcher.getFullSyntax(this) + " instead.");
            return;
        }
        final int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                throw new NumberFormatException("Amount of lives taken must be positive");
            }
        } catch (final NumberFormatException e) {
            sender.sendMessage("The amount of lives taken must be positive. (Got \"" + args[1] + "\")");
            sender.sendMessage("Try " + BODCommandDispatcher.getFullSyntax(this) + " instead.");
            return;
        }
        final String origPlayerName = args[0];
        final String lowerPlayerName = origPlayerName.toLowerCase();
        final String livesPath = lowerPlayerName + ".lives";
        final int oldLives = plugin.playersConfig.getInt(livesPath);
        final int newLives = oldLives - amount;
        if (newLives <= 0) {
            sender.sendMessage("That would leave the player with less than 0 lives.  If you want to ban them, use " + BODCommandDispatcher.getFullSyntax(plugin.getSubCommand("ban")));
            sender.sendMessage("Currently, that player has " + oldLives + (oldLives == 1 ? " life" : " lives") + " remaining.");
        } else {
            plugin.playersConfig.set(livesPath, newLives);
            sender.sendMessage(origPlayerName + " has been taken down from " + oldLives + " to " + amount + (amount == 1 ? " life" : " lives") + ".");
        }
    }

    public String getPermissionNode() {
        return "bod.take";
    }

    public String[] getNames() {
        return new String[]{"take", "takelives"};
    }

    public String getSyntax() {
        return "<playername> <amount>";
    }
}
