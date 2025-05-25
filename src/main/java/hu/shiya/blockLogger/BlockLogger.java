package hu.shiya.blockLogger;

import hu.shiya.blockLogger.commands.LocateCommand;
import hu.shiya.blockLogger.commands.RollBack;
import hu.shiya.blockLogger.listeners.BlockListeners;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockLogger extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new BlockListeners( this ), this);
        this.getCommand("rollback").setExecutor( new RollBack( this ) );
        this.getCommand("locate").setExecutor( new LocateCommand( this ) );
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
