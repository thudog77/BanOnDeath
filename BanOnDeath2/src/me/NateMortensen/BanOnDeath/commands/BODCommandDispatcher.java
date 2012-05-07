package me.NateMortensen.BanOnDeath.commands;

import java.util.LinkedHashMap;
import java.util.Map;
import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Ban-On-Death commands.
 *
 * @author Score_Under
 */
public final class BODCommandDispatcher {

    private static final BODCommand[] commandArr = {new BanCommand(), new GiveCommand(),
        new GodCommand(), new InfoCommand(), new ResetCommand(),
        new SetCommand(), new TakeCommand(), new UnbanCommand(),};
    private final Map<String, BODCommand> commands = new LinkedHashMap<String, BODCommand>();
    private final BanOnDeath plugin;

    public BODCommandDispatcher(final BanOnDeath plugin) {
        this.plugin = plugin;

        for (final BODCommand command : commandArr) {
            for (final String name : command.getNames()) {
                this.commands.put(name.toLowerCase(), command);
            }
        }
    }

    public BODCommand getCommand(final String str) {
        return commands.get(str.toLowerCase());
    }

    public boolean dispatch(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            return false;
        }
        final BODCommand command = getCommand(args[0].toLowerCase());
        if (command == null) {
            return false;
        }
        final String permissionNode = command.getPermissionNode();
        if (permissionNode != null && !sender.hasPermission(permissionNode)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use that command.");
            return true;
        }
        final int numArgs = args.length - 1;
        final String[] newArgs = new String[numArgs];
        System.arraycopy(args, 1, newArgs, 0, numArgs);

        command.execute(plugin, sender, newArgs);
        return true;
    }

    public BODCommand[] getCommands() {
        return commandArr.clone();
    }

    public static String getFullSyntax(BODCommand c) {
        final String syntax = c.getSyntax();
        if (syntax == null) {
            return ChatColor.AQUA + "/bod " + c.getNames()[0];
        } else {
            return ChatColor.AQUA + "/bod " + c.getNames()[0] + " " + ChatColor.BLUE + syntax;
        }
    }
}
