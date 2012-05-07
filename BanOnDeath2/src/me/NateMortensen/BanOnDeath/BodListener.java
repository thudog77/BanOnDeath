package me.NateMortensen.BanOnDeath;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * ***
 *
 * @author Nate Mortensen
 */
public class BodListener implements Listener {

    private static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final BanOnDeath plugin;
    private final Set<String> playersPendingList = new HashSet<String>();

    public BodListener(final BanOnDeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event instanceof PlayerDeathEvent) {
            final Player player = (Player) event.getEntity();
            if (player.hasPermission("bod.noban") || player.isOp()) {
                return;
            }
            final String playerLivesPath = player.getName().toLowerCase() + ".lives";
            final int playerLives = plugin.playersConfig.getInt(playerLivesPath);
            //Lives check
            if (playerLives > 0) {
                plugin.playersConfig.set(playerLivesPath, playerLives - 1);
                player.sendMessage("You have " + playerLives + " lives remaining.");
                return;
            }
            final long now = System.currentTimeMillis();
            // Player ban code goes below.
            if (plugin.getConfig().getBoolean("Run Command Instead")) {
                playersPendingList.add(player.getName());
                return;
            } else if (!plugin.getConfig().getBoolean("Run Command Instead")) {
                plugin.playersConfig.set(player.getName().toLowerCase() + ".lastbantime", now);
                player.getInventory().clear();
                player.kickPlayer(plugin.config.getString("Kick Message"));
            }
            if (plugin.logToFile == false) {
                final Date nowDate = new Date(now);
                final Date unbanDate = new Date(now + plugin.getBanLength(plugin.getTier(player)));
                try {
                    PrintWriter pw = new PrintWriter(new FileWriter(plugin.file.getPath(), true));
                    pw.println(player.getName() + ", "
                            + dateFormatter.format(nowDate) + ", "
                            + dateFormatter.format(unbanDate) + ", "
                            + ((PlayerDeathEvent) event).getDeathMessage());
                    pw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void onEntitySpawn(final PlayerRespawnEvent event) {
        final String playerName = event.getPlayer().getName();
        if (playersPendingList.contains(playerName)) {
            playersPendingList.remove(playerName);

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                public void run() {
                    for (String e : plugin.getConfig().getStringList("Command-To-Run-On-Death")) {
                        if (e.contains("{name}")) {
                            e = e.replaceAll("\\{name\\}", playerName);
                        }
                        System.out.println("Now executing " + e);
                        plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), e);
                    }
                }
            }, 1L);
        }
    }

    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (plugin.godded.contains(player.getName())) {
                event.setDamage(0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (plugin.isBanned(event.getPlayer())) {
            Date date = new Date(plugin.getBanLength(plugin.getTier(event.getPlayer())) + plugin.playersConfig.getLong(event.getPlayer().getName().toLowerCase() + ".lastbantime"));
            final String kickMsg = "Rejoin on: " + dateFormatter.format(date);
            event.setKickMessage(kickMsg); //Workaround for esoteric bug
            event.disallow(Result.KICK_BANNED, kickMsg);
            return;
        }

        if (!(plugin.playersConfig.contains(event.getPlayer().getName().toLowerCase() + ".lives"))) {
            plugin.resetLives(event.getPlayer());
            return;
        }

        final Player player = event.getPlayer();
        if (plugin.needsReset(player)) {
            plugin.resetLives(player);
        }
    }
}