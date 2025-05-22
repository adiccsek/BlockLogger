package hu.shiya.blockLogger.commands;

import hu.shiya.blockLogger.BlockLogger;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LocateCommand implements CommandExecutor {
    int radius;
    String playerName;
    String blockType;
    String type;
    int x;
    int y;
    int z;


    private final BlockLogger pluginInstance;
    public LocateCommand( final BlockLogger plugin ) {
        this.pluginInstance = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length != 1) {
            commandSender.sendMessage("Please use one argument (int)");
            return true;
        }
        //locate 5
        // shiya_ break GrassBlock Location
        // Phoenix place Oak_Wood Location

        if (commandSender instanceof Player player) {
            Location currentLocation = player.getLocation();
            ConfigurationSection conf = pluginInstance.getConfig().getConfigurationSection( "logs" );
            if (conf == null) {
                player.sendMessage(" no log data found ");
                return true;
            }
            try {

                radius = Integer.parseInt(args[0]);
                for (String key : conf.getKeys(false)) {
                    ConfigurationSection section = conf.getConfigurationSection(key);
                    x = section.getInt("location.x");
                    y = section.getInt("location.y");
                    z = section.getInt("location.z");
                    playerName = section.getString("playername");
                    blockType = section.getString("block");
                    type = section.getString("type");

                    if (currentLocation.getX() - radius <= x && currentLocation.getX() + radius >= x &&
                            currentLocation.getY() - radius <= y && currentLocation.getY() + radius >= y &&
                            currentLocation.getZ() - radius <= z && currentLocation.getZ() + radius >= z )
                    {
                        player.sendMessage(playerName + " " + type + " at " + x + ", " + y + ", " + z + ", " + blockType);
                    } else {
                        player.sendMessage("No data was found with this range!");
                        return true;
                    }
                }
            } catch ( final NumberFormatException e ) {
                player.sendMessage("Only Numbers are accepted as arguments");
            }
        }
        return true;
    }
}
