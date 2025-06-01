package hu.shiya.blockLogger.services;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LoadMessages {
    private final JavaPlugin plugin;
    private FileConfiguration messagesConfig;

    public LoadMessages( final JavaPlugin plugin ) {
        this.plugin = plugin;
        loadMessages();
    }
    public void loadMessages() {
        File file = new File( plugin.getDataFolder(), "messages.yml" );
        if ( !file.exists() ) {
            plugin.saveResource( "messages.yml", false );
        }
        messagesConfig = YamlConfiguration.loadConfiguration( file );
    }
    public String get(String path) {
        return ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(path, "§cMessage not found: " + path));
    }
}
