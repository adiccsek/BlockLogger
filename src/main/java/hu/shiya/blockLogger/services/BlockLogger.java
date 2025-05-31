package hu.shiya.blockLogger.services;

import hu.shiya.blockLogger.commands.LocateCommand;
import hu.shiya.blockLogger.commands.RollBack;
import hu.shiya.blockLogger.commands.WriteToFileCommand;
import hu.shiya.blockLogger.listeners.BlockListeners;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlockLogger extends JavaPlugin {
    private final SQL sqlInstance;
    public BlockLogger() {
        sqlInstance = new SQL( this, this.getConfig() );
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        String host = getConfig().getString("database.host");
        String userName = getConfig().getString("database.username");
        String password = getConfig().getString("database.password");
        String databaseName = getConfig().getString("database.databaseName");


        sqlInstance.handleDatabaseAsync(host, password, userName, databaseName);

        getServer().getPluginManager().registerEvents( new BlockListeners(this , sqlInstance), this);
        this.getCommand("rollback").setExecutor( new RollBack( this , sqlInstance ) );
        this.getCommand("locate").setExecutor( new LocateCommand( this , sqlInstance) );
        this.getCommand("write").setExecutor( new WriteToFileCommand( this , sqlInstance));
    }

    @Override
    public void onDisable() {
        sqlInstance.disableDatabaseAsync();
    }
}
