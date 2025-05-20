package hu.shiya.blockLogger.listeners;

import hu.shiya.blockLogger.BlockLogger;
import hu.shiya.blockLogger.Data;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.SQLOutput;


public class BlockListeners implements Listener {
    String type;
    String player;
    String block;
    Location location;
    long time;

    private final BlockLogger pluginInstance;
    public BlockListeners( final BlockLogger pluginInstance ) {
        this.pluginInstance = pluginInstance;
    }
    public int generateKey() {
        for(int i = 0; i < Integer.MAX_VALUE; ++i) {
            if(pluginInstance.getConfig().getString("logs." + i, null) == null) {
                return i;
            }
        }
        return 0;
    }

    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        player = event.getPlayer().getName();
        block = event.getBlock().getType().toString();
        location = event.getBlock().getLocation();
        time = System.currentTimeMillis();
        type = "break";

        Data savedData = new Data(player, block, location, time, type);
        int key = generateKey();
        ConfigurationSection conf = pluginInstance.getConfig().getConfigurationSection("logs." + key);
        if (conf == null) { conf = pluginInstance.getConfig().createSection("logs." + key); }
        savedData.save(conf);
        pluginInstance.saveConfig();
    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockPlace( BlockPlaceEvent event ) {
        System.out.println("DEBUG" + event.getBlock().getType());
        player = event.getPlayer().getName();
        block = event.getBlock().getType().toString();
        location = event.getBlock().getLocation();
        time = System.currentTimeMillis();
        type = "place";

        Data savedData = new Data(player, block, location, time, type);
        int key = generateKey();
        System.out.println("DEBUG2" + event.getBlock().getType());
        ConfigurationSection conf = pluginInstance.getConfig().getConfigurationSection("logs." + key);
        if (conf == null) { conf = pluginInstance.getConfig().createSection("logs." + key); }
        savedData.save(conf);
        pluginInstance.saveConfig();
        System.out.println("DEBUG3" + event.getBlock().getType());
    }
}
