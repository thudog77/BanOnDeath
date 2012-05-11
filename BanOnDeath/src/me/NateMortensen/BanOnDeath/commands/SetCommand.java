package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.command.CommandSender;

/**
 * Command to set a player's lives.
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class SetCommand implements BODCommand {

    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Try " + BODCommandDispatcher.getFullSyntax(this) + " instead.");
            return;
        }
        final int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                throw new NumberFormatException("Amount of lives must be positive.");
            }
        } catch (final NumberFormatException e) {
            sender.sendMessage("The amount of lives to set must be positive. (Got \"" + args[1] + "\")");
            sender.sendMessage("Try /bod set <playername> <amount> instead.");
            return;
        }
        if (amount <= 0) {
            sender.sendMessage("That would leave the player without any lives.");
            sender.sendMessage("If you want to ban them, use " + BODCommandDispatcher.getFullSyntax(plugin.getSubCommand("ban")) + " instead.");
        } else {
            plugin.players.set(args[0].toLowerCase() + ".lives", amount);
            sender.sendMessage(args[0] + " now has " + amount + " lives.");
        }
    }

    public String getPermissionNode() {
        return "bod.set";
    }

    public String[] getNames() {
        return new String[]{"set", "setlives"};
    }

    public String getSyntax() {
        return "<player> <amount>";
    }
}
