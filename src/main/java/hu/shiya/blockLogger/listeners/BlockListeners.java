package hu.shiya.blockLogger.listeners;

import hu.shiya.blockLogger.services.BlockLogger;
import hu.shiya.blockLogger.services.Data;
import hu.shiya.blockLogger.services.SQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;


public class BlockListeners implements Listener {
    private final BlockLogger pluginInstance;
    private final SQL sqlInstance;
    public BlockListeners( final BlockLogger pluginInstance , final SQL sqlInstance ) {
        this.pluginInstance = pluginInstance;
        this.sqlInstance = sqlInstance;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String playerName = event.getPlayer().getName();
        String blockType = event.getBlock().getType().toString();
        Location loc = event.getBlock().getLocation();
        long timestamp = System.currentTimeMillis();
        String actionType = "break";

        String gm = event.getPlayer().getGameMode().toString();
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        Collection<ItemStack> drops = event.getBlock().getDrops(tool);

        String dropType = "";
        int dropAmount = 0;
        for (ItemStack drop : drops) {
            dropType = drop.getType().toString();
            dropAmount = drop.getAmount();
        }

        String finalDropType = dropType;
        int finalDropAmount = dropAmount;
        Bukkit.getScheduler().runTaskAsynchronously(pluginInstance, () -> {
            Data savedData = new Data(playerName, blockType, loc, timestamp, actionType, gm, finalDropType, finalDropAmount);
            sqlInstance.saveLoggedBlocksAsync(savedData);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

        String playerName = event.getPlayer().getName();
        String blockType = event.getBlock().getType().toString();
        Location loc = event.getBlock().getLocation();
        long timestamp = System.currentTimeMillis();
        String actionType = "place";

        String gm = event.getPlayer().getGameMode().toString();
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        Block block = event.getBlock();

        Collection<ItemStack> drops = block.getDrops(tool);
        String dropType = "";
        int dropAmount = 0;


        for (ItemStack drop : drops) {
            dropType = drop.getType().toString();
            dropAmount = drop.getAmount();
        }

        String finalDropType = dropType;
        int finalDropAmount = dropAmount;
        Bukkit.getScheduler().runTaskAsynchronously(pluginInstance, () -> {
            Data savedData = new Data(playerName, blockType, loc, timestamp, actionType, gm, finalDropType, finalDropAmount);
            sqlInstance.saveLoggedBlocksAsync(savedData);
        });
    }
}
