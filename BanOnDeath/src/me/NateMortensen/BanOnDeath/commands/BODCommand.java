package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.command.CommandSender;

/**
 * Ban-On-Death subcommand.
 *
 * @author Score_Under
 */
public interface BODCommand {

    /**
     * @return the permission node required to execute this command, or null if
     * none
     */
    String getPermissionNode();

    /**
     * @return an array containing all possible aliases of this command
     */
    String[] getNames();
    
    /**
     * @return the proper syntax for the arguments of this command.
     */
    String getSyntax();

    /**
     * Execute this command.
     *
     * @param plugin the plugin instance
     * @param sender the sender of the command
     * @param args the arguments of the subcommand
     */
    void execute(BanOnDeath plugin, CommandSender sender, String[] args);
}
