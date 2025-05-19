package hu.shiya.blockLogger.listeners;

import hu.shiya.blockLogger.BlockLogger;
import hu.shiya.blockLogger.Data;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;


public class BlockListeners implements Listener {
    private final BlockLogger pluginInstance;
    public BlockListeners( final BlockLogger pluginInstance ) {
        this.pluginInstance = pluginInstance;
    }
    public int generateKey() {
        for(int i = 0; i < Integer.MAX_VALUE; ++i) {
            if(pluginInstance.getConfig().getString("logs." + i + ".type", null) == null) {
                return i;
            }
        }
        return 0;
    }

    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        String player = event.getPlayer().getName(); //NAME
        String block = event.getBlock().getType().toString(); // BLOCK
        Location location = event.getBlock().getLocation(); // LOCATION
        long time = System.currentTimeMillis(); //TIME
        String type = "break";

        Data savedDatas = new Data(player, block, location, time, type);
        int key = generateKey();

        pluginInstance.getConfig().set("logs." + key + ".type", savedDatas.Type);
        pluginInstance.getConfig().set("logs." + key + ".playername", savedDatas.PlayerName);
        pluginInstance.getConfig().set("logs." + key + ".block", savedDatas.Block);
        pluginInstance.getConfig().set("logs."  + key + ".location.world", savedDatas.Location.getWorld().getName());
        pluginInstance.getConfig().set("logs."  + key + ".location.x", savedDatas.Location.getBlockX());
        pluginInstance.getConfig().set("logs."  + key + ".location.y", savedDatas.Location.getBlockY());
        pluginInstance.getConfig().set("logs."  + key + ".location.z", savedDatas.Location.getBlockZ());
        pluginInstance.getConfig().set("logs."  + key + ".time", savedDatas.Time);
        pluginInstance.saveConfig();
    }
}
