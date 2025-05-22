package hu.shiya.blockLogger.commands;

import hu.shiya.blockLogger.BlockLogger;
import hu.shiya.blockLogger.Data;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LocateCommand implements CommandExecutor {
    int radius;
        
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
                    Data data = new Data();
                    data.load( section );

                    if (currentLocation.getX() - radius <= data.getLocation().getX() && currentLocation.getX() + radius >= data.getLocation().getX() &&
                            currentLocation.getY() - radius <= data.getLocation().getY() && currentLocation.getY() + data.getLocation().getY() >= data.getLocation().getY() &&
                            currentLocation.getZ() - radius <= data.getLocation().getZ() && currentLocation.getZ() + radius >= data.getLocation().getZ() )
                    {
                        player.sendMessage(data.getPlayerName() + " " + data.getType() + " at " + data.getLocation().getX() + ", " + data.getLocation().getY() + ", " + data.getLocation().getBlockZ() + ", " + data
                                .getBlock());
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
