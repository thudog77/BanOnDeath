package me.NateMortensen.BanOnDeath;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * ***
 *
 * @author Nate Mortensen
 *
 */
public class YAPI {

    public static void saveYaml(JavaPlugin p, FileConfiguration file, String name) {
        if (file == null) {
            return;
        }
        try {
            file.save(new File(p.getDataFolder(), name + ".yml"));
        } catch (IOException ex) {
            p.getLogger().log(Level.SEVERE, "Could not save " + name + ".yml to " + p.getDataFolder().getName() + name + ".yml", ex);
        }
    }

    public static void configCheck(FileConfiguration file, String path, Object defaultvalue) {
        if (!(file.contains(path))) {
            file.set(path, defaultvalue);
        }
    }
}
