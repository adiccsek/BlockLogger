package hu.shiya.blockLogger.listeners;

import hu.shiya.blockLogger.services.BlockLogger;
import hu.shiya.blockLogger.services.Data;
import hu.shiya.blockLogger.services.SQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;


public class BlockListeners implements Listener {
    String type;
    String player;
    String block;
    Location location;
    long time;

    private final BlockLogger pluginInstance;
    private final SQL sqlInstance;
    public BlockListeners( final BlockLogger pluginInstance , final SQL sqlInstance ) {
        this.pluginInstance = pluginInstance;
        this.sqlInstance = sqlInstance;
    }

    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        player = event.getPlayer().getName();
        block = event.getBlock().getType().toString();
        location = event.getBlock().getLocation();
        time = System.currentTimeMillis();
        type = "break";



        Bukkit.getScheduler().runTaskAsynchronously( pluginInstance, () -> {
            Data savedData = new Data(player, block, location, time, type);
            sqlInstance.saveLoggedBlocksAsync(savedData);
        });
    }

    @EventHandler (ignoreCancelled = true)
    public void onBlockPlace( BlockPlaceEvent event ) {
        System.out.println("DEBUG" + event.getBlock().getType());
        player = event.getPlayer().getName();
        block = event.getBlock().getType().toString();
        location = event.getBlock().getLocation();
        time = System.currentTimeMillis();
        type = "place";

        Bukkit.getScheduler().runTaskAsynchronously( pluginInstance, () -> {
            Data savedData = new Data(player, block, location, time, type);
            sqlInstance.saveLoggedBlocksAsync(savedData);
        });
    }
}
