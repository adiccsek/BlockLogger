package hu.shiya.blockLogger.commands;

import hu.shiya.blockLogger.BlockLogger;
import hu.shiya.blockLogger.Data;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LocateCommand implements CommandExecutor {
    private final BlockLogger pluginInstance;
    public LocateCommand( final BlockLogger plugin ) {
        this.pluginInstance = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!commandSender.hasPermission("locate-command")) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        int radius;

        List<String> output = new ArrayList<String>();
        if (args.length > 2 || args.length == 0) {
            commandSender.sendMessage("Please use a correct number of arguments");
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

                    boolean withinRadius = isWithinRadius(currentLocation, data, radius);
                    boolean matchesPlayer = args.length == 2 && data.getPlayerName().equals(args[1]);
                        if ( withinRadius && ( args.length == 1 || matchesPlayer))
                        {
                            output.add(String.format("%s %s at (%d, %d, %d) : %s",
                                    data.getPlayerName(),
                                    data.getType(),
                                    data.getLocation().getBlockX(),
                                    data.getLocation().getBlockY(),
                                    data.getLocation().getBlockZ(),
                                    data.getBlock()));
                        }
                }
            } catch ( final NumberFormatException e ) {
                player.sendMessage( e.getMessage() );
            }
            if (!output.isEmpty()) {
                for (String text : output) {
                    player.sendMessage( text );
                }} else {
                player.sendMessage( "no log-data was found with this argument!" );
            }
        }
        return true;
    }

    private static boolean isWithinRadius(Location currentLocation, Data data, int radius) {
        int px = currentLocation.getBlockX();
        int py = currentLocation.getBlockY();
        int pz = currentLocation.getBlockZ();

        int dx = data.getLocation().getBlockX();
        int dy = data.getLocation().getBlockY();
        int dz = data.getLocation().getBlockZ();
        boolean withinRadius =
                px - radius <= dx && px + radius >= dx &&
                py - radius <= dy && py + radius >= dy &&
                pz - radius <= dz && pz + radius >= dz;
        return withinRadius;
    }
}
