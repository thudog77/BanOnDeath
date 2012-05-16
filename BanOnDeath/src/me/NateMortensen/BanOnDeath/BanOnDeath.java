package me.NateMortensen.BanOnDeath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Logger;
import me.NateMortensen.BanOnDeath.commands.BODCommand;
import me.NateMortensen.BanOnDeath.commands.BODCommandDispatcher;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * ***
 *
 * @author Nate Mortensen
 */
public class BanOnDeath extends JavaPlugin {

    BodListener listener = new BodListener(this);
    public FileConfiguration config;
    public Set<String> godded;
    public Boolean isOn;
//    public long banTimeoutMillis; //TODO THIS IS NEVER INITIALIZED!!! IT IS ALWAYS ZERO! FIX!!!
    public File file;
    public boolean logToFile;
//    boolean changed; //TODO this is not used
    public FileConfiguration players = null;
    public FileConfiguration tiers = null;
    private BODCommandDispatcher commandDispatcher;

    @Override
    public void onEnable() {
        players = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "players.yml"));
        tiers = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "tiers.yml"));
        config = getConfig();
        final Logger log = getLogger();
        getServer().getPluginManager().registerEvents(listener, this);
        godded = new HashSet<String>();

        //Ban length
        //Config variable initialization.
        YAPI.configCheck(config, "Kick Message", "You have failed");
        YAPI.configCheck(config, "WriteToFile", true);
        YAPI.configCheck(tiers, "default.unit", "minute");
        YAPI.configCheck(tiers, "default.amount_of_unit", 30);
        YAPI.configCheck(tiers, "default.lives", 1);
        if (tiers.contains("default")){
        	tiers.set("default.unit", "minute");
        	tiers.set("default.amount_of_unit", 30);
        	tiers.set("default.lives", 1);
        	tiers.set("default.resettime", 7);
        	YAPI.saveYaml(this, tiers, "tiers");
        }
        //End ban length

        commandDispatcher = new BODCommandDispatcher(this);
        PluginDescriptionFile pdf = this.getDescription();
        String name = pdf.getName();
        String version = pdf.getVersion();
        List<String> author = pdf.getAuthors();
        
    
        log.info(name +" v" + version + " by " + author.get(0) + " enabled!");
        saveConfig();
        if (config.getBoolean("WriteToFile")) {
            file = new File(getDataFolder() + "/banlist.cvs");
            if (!file.exists()) {
                try {
                    file.createNewFile(); 
                    PrintWriter writer = new PrintWriter(new FileWriter(file.getPath()));
                    writer.println("Username,Date,Unban Date,Cause");
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        YAPI.saveYaml(this, players, "Players");
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lives")) {
            if (sender instanceof Player) {
                sender.sendMessage("You have " + players.getInt(sender.getName().toLowerCase() + ".lives") + " lives remaining.");
            } else {
                sender.sendMessage("Last time I checked Consoles can't die, and don't have lives.  Might just be me though.");
                sender.sendMessage("If you're trying to get info on a player, use /bod info <playername> instead.");
            }
        } else if (cmd.getName().equalsIgnoreCase("bod")) {
            if (!commandDispatcher.dispatch(sender, args)) {
                showAvailableCommands(sender, cmd);
            }
        }
        return true;
    }
    public void clearInventory(final Player player){
    	PlayerInventory inventory = player.getInventory();
    	inventory.clear();
    	inventory.setArmorContents(null);
    }

    private void showAvailableCommands(CommandSender sender, Command cmd) {
        final List<String> availableCommands = new ArrayList<String>();
        for (final BODCommand command : commandDispatcher.getCommands()) {
            if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
                availableCommands.add(BODCommandDispatcher.getFullSyntax(command));
            }
        }
        if (availableCommands.isEmpty()) {
            sender.sendMessage(cmd.getPermissionMessage());
        } else {
            sender.sendMessage("Try one of the following commands:");
            sender.sendMessage(availableCommands.toArray(new String[availableCommands.size()]));
        }
    }
    public Boolean isOnline(String name){
    	final Player[] onlineplayers = getServer().getOnlinePlayers();
    	for (int i = 0; i < onlineplayers.length; i++){
    		if (onlineplayers[i].getName() == name){
    			return true;
    		}
    	}
    	return false;
    }
    public BODCommand getSubCommand(final String str) {
        return commandDispatcher.getCommand(str);
    }
    public long getLastBanTime(Player player){
    	return players.getLong(player.getName() + ".lastbantime");
    }
    public long getLastBanTime(String player){
    	return players.getLong(player + ".lastbantime");
    }


    public long getBanLength(String tier) {
        final String unit;
        final long numberof;
        if (tier == null) {
            unit = tiers.getString("default.unit");
            numberof = tiers.getLong("default.amount_of_unit");
        } else {
            unit = tiers.getString(tier + ".unit");
            numberof = tiers.getLong(tier + ".amount_of_unit");
        }
        if (unit.equals("minute")) {
            return numberof * 60000L;
        } else if (unit.equals("second")) {
            return numberof * 1000L;
        } else if (unit.equals("hour")) {
            return numberof * 3600000L;
        } else if (unit.equals("day")) {
            return numberof * 86400000L;
        } else {
            final Logger logger = getLogger();
            logger.info(tier + " has an error in its units, using the default value instead.");
            if (tier.equals("default")) {
                logger.info("[BanOnDeath][SEVERE]  Default tier's unit is not valid!");
                return 0;
            } else {
                return getBanLength("default");
            }
        }
    }

    public String getTier(final Player player) {
    	String[] thetiers = new String[tiers.getKeys(false).size()];
		for (int x = 0; x < tiers.getKeys(false).size(); x++){
			thetiers[x] = tiers.getKeys(false).iterator().next();
			tiers.getKeys(false).remove(tiers.getKeys(false).iterator().next());
		}
        for (int i = 0; i < thetiers.length; i++) {
            if (player.hasPermission("bod.tiers."+ thetiers[i])) {
                return thetiers[i];
            }
        }
        return "default";
    }

    public void resetLives(final Player player) {
        final int targetLives = tiers.getInt(getTier(player) + ".lives");
        if (targetLives == 0) {
            return;
        }
        final String playerName = player.getName();
        players.set(playerName + ".lives", targetLives);
        players.set(playerName + ".lastreset", System.currentTimeMillis());
    }


    private long getPlayerConfigLong(Player player, final String node) {
        return players.getLong(player.getName().toLowerCase() + "." + node);
    }

    private boolean hasPlayerConfig(Player player, final String node) {
        return players.contains(player.getName().toLowerCase() + "." + node);
    }

    public Boolean isBanned(Player player) {
        if (!hasPlayerConfig(player, "lastbantime")) {
            return false;
        }

        if (System.currentTimeMillis() - getPlayerConfigLong(player, "lastbantime") < getBanLength(getTier(player))) {
            return true;
        }
        return false;
    }

    public boolean needsReset(final Player player) {
        if (!hasPlayerConfig(player, "lastreset")) {
            return false;
        }
        if ((System.currentTimeMillis() - getPlayerConfigLong(player, "lastreset") < tiers.getInt(getTier(player) + ".resettime"))) {
            return false;
        }
        return true;
    }
    //UNUSED
//    public long getUnbanDate(final Player player) {
//        return miliseconds + getPlayerConfig(player, "lastbantime");
//    }
}
