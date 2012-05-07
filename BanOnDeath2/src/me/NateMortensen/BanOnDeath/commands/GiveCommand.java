package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.command.CommandSender;

/**
 * Command to give a player lives.
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class GiveCommand implements BODCommand {

    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Try " + BODCommandDispatcher.getFullSyntax(this) + " instead.");
            return;
        }
        final int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0)
            {
                throw new NumberFormatException("Amount must be positive.");
            }
        } catch (final NumberFormatException e) {
            sender.sendMessage("The amount of lives given must be positive. (Got \"" + args[1] + "\")");
            sender.sendMessage("Try " + BODCommandDispatcher.getFullSyntax(this) + " instead.");
            return;
        }
        final String livesPath = args[0].toLowerCase() + ".lives";
        if (!plugin.playersConfig.contains(livesPath)) {
            sender.sendMessage("That player doesn't seem to exist or is currently banned.");
        } else {
            plugin.playersConfig.set(livesPath, plugin.playersConfig.getInt(livesPath) + amount);
        }
    }

    public String getPermissionNode() {
        return "bod.give";
    }

    public String[] getNames() {
        return new String[]{"give", "givelives", "addlives"};
    }

    public String getSyntax() {
        return "<player> <lives to give>";
    }
}
