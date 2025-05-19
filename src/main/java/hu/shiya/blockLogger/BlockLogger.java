package hu.shiya.blockLogger;

import hu.shiya.blockLogger.listeners.BlockListeners;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockLogger extends JavaPlugin {

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(new BlockListeners( this ), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
