package hu.shiya.blockLogger.commands;

import hu.shiya.blockLogger.BlockLogger;
import hu.shiya.blockLogger.Data;
import hu.shiya.blockLogger.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocateCommand implements CommandExecutor {
    private final BlockLogger pluginInstance;
    public LocateCommand( final BlockLogger plugin ) {
        this.pluginInstance = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {



        List<String> output = new ArrayList<String>();
        if (!commandSender.hasPermission("locate-command")) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("player" , commandSender.getName());

            String rawMessage = pluginInstance.getConfig().getString("messages.locate.no-permission-error");
            commandSender.sendMessage(ChatColor.RED + Placeholder.placeholder(rawMessage, placeholders));
            return true;
        }


        if (args.length > 2 || args.length == 0) {
            String message = pluginInstance.getConfig().getString("messages.locate.arguments-error");
            commandSender.sendMessage(message);
            return true;
        }
        if (commandSender instanceof Player player) {
            Location currentLocation = player.getLocation();
            ConfigurationSection conf = pluginInstance.getConfig().getConfigurationSection( "logs" );
            if (conf == null) {
                String message = pluginInstance.getConfig().getString("messages.locate.data-error");
                player.sendMessage(message);
                return true;
            }
                int radius = Integer.MIN_VALUE;
                try { radius = Integer.parseInt(args[0]); }
                catch (Exception e) {
                    player.sendMessage("Ez nem egy szám!");
                    return true;
                };

                for (String key : conf.getKeys(false)) {
                    ConfigurationSection section = conf.getConfigurationSection(key);
                    Data data = new Data();
                    data.load( section );

                    boolean withinRadius = isWithinRadius(currentLocation, data, radius);
                    boolean matchesPlayer = args.length == 2 && data.getPlayerName().equals(args[1]);
                        if ( withinRadius && ( args.length == 1 || matchesPlayer))
                        {
                            HashMap<String, String> placeholders = new HashMap<>();
                            String rawMessage = pluginInstance.getConfig().getString("messages.locate.locate-correct");

                            placeholders.put("player" , player.getName());
                            placeholders.put("type", data.getType());
                            placeholders.put("block", String.valueOf(data.getBlock()));
                            placeholders.put("x", String.valueOf(data.getLocation().getBlockX()));
                            placeholders.put("y", String.valueOf(data.getLocation().getBlockY()));
                            placeholders.put("z", String.valueOf(data.getLocation().getBlockZ()));
                            output.add(Placeholder.placeholder(rawMessage, placeholders));
//                            output.add(String.format("%s %s at (%d, %d, %d) : %s",
//                                    data.getPlayerName(),
//                                    data.getType(),
//                                    data.getLocation().getBlockX(),
//                                    data.getLocation().getBlockY(),
//                                    data.getLocation().getBlockZ(),
//                                    data.getBlock()));
                        }
                }

            if (!output.isEmpty()) {
                for (String text : output) {
                    player.sendMessage( text );
                }} else {
                String message = pluginInstance.getConfig().getString("messages.locate.data-error");
                player.sendMessage(message);
                return true;
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
