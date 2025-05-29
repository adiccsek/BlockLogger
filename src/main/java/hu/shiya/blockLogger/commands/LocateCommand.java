package hu.shiya.blockLogger.commands;

import hu.shiya.blockLogger.services.BlockLogger;
import hu.shiya.blockLogger.services.Data;
import hu.shiya.blockLogger.services.Placeholder;
import hu.shiya.blockLogger.services.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class LocateCommand implements CommandExecutor {
    private final BlockLogger pluginInstance;
    private final SQL sqlInstance;
    public LocateCommand( final BlockLogger plugin , final SQL sqlInstance ) {
        this.pluginInstance = plugin;
        this.sqlInstance = sqlInstance;
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

                Bukkit.getScheduler().runTaskAsynchronously( pluginInstance, () -> {
                    int radius = Integer.MIN_VALUE;
                    try { radius = Integer.parseInt(args[0]); }
                    catch (Exception e) {
                        player.sendMessage("Ez nem egy szám!");
                    };

                    if (args.length == 2) {
                        ArrayList<Data> loopDatas = sqlInstance.locateLogicPlayerAsync(player.getName(), currentLocation, radius);
                        for (Data data : loopDatas) {
                                HashMap<String, String> placeholders = new HashMap<>();
                                String rawMessage = pluginInstance.getConfig().getString("messages.locate.locate-correct");

                                placeholders.put("player" , player.getName());
                                placeholders.put("type", data.getType());
                                placeholders.put("block", String.valueOf(data.getBlock()));
                                placeholders.put("x", String.valueOf(data.getLocation().getBlockX()));
                                placeholders.put("y", String.valueOf(data.getLocation().getBlockY()));
                                placeholders.put("z", String.valueOf(data.getLocation().getBlockZ()));

                                Bukkit.getScheduler().runTask(pluginInstance, () -> {
                                    TextDisplay hologram = data.getLocation().getWorld().spawn(data.getLocation(),TextDisplay.class);

                                    hologram.setText(ChatColor.RED + Placeholder.placeholder(rawMessage, placeholders));
                                    hologram.setDefaultBackground(false);
                                    hologram.setAlignment(TextDisplay.TextAlignment.CENTER);
                                    hologram.setSeeThrough(true);
                                    hologram.setShadowed(false);
                                    hologram.setLineWidth(150);


                                    AtomicInteger taskId = new AtomicInteger();

                                    taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(pluginInstance, () -> {
                                        if (!hologram.isValid()) {
                                            Bukkit.getScheduler().cancelTask(taskId.get());
                                            return;
                                        }

                                        hologram.teleport(data.getLocation());

                                        Location holoLoc = hologram.getLocation();
                                        Vector direction = player.getLocation().toVector()
                                                .subtract(holoLoc.toVector());
                                        holoLoc.setDirection(direction);
                                        hologram.teleport(holoLoc);
                                    }, 0L, 1L));


                                    Bukkit.getScheduler().runTaskLater(pluginInstance, () -> {
                                        if (hologram.isValid()) hologram.remove();
                                        Bukkit.getScheduler().cancelTask(taskId.get());
                                    }, 6 * 20L);

                                    output.add(Placeholder.placeholder(rawMessage, placeholders));
                                });
                        }
                        } else if (args.length == 1) {
                        ArrayList<Data> loopDatas = sqlInstance.locateLogicAsync( currentLocation, radius );
                        for (Data data : loopDatas) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            String rawMessage = pluginInstance.getConfig().getString("messages.locate.locate-correct");

                            placeholders.put("player" , player.getName());
                            placeholders.put("type", data.getType());
                            placeholders.put("block", String.valueOf(data.getBlock()));
                            placeholders.put("x", String.valueOf(data.getLocation().getBlockX()));
                            placeholders.put("y", String.valueOf(data.getLocation().getBlockY()));
                            placeholders.put("z", String.valueOf(data.getLocation().getBlockZ()));

                            Bukkit.getScheduler().runTask(pluginInstance, () -> {
                                TextDisplay hologram = data.getLocation().getWorld().spawn(data.getLocation(),TextDisplay.class);

                                hologram.setText(ChatColor.RED + Placeholder.placeholder(rawMessage, placeholders));
                                hologram.setDefaultBackground(false);
                                hologram.setAlignment(TextDisplay.TextAlignment.CENTER);
                                hologram.setSeeThrough(true);
                                hologram.setShadowed(false);
                                hologram.setLineWidth(150);


                                AtomicInteger taskId = new AtomicInteger();

                                taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(pluginInstance, () -> {
                                    if (!hologram.isValid()) {
                                        Bukkit.getScheduler().cancelTask(taskId.get());
                                        return;
                                    }

                                    hologram.teleport(data.getLocation());

                                    Location holoLoc = hologram.getLocation();
                                    Vector direction = player.getLocation().toVector()
                                            .subtract(holoLoc.toVector());
                                    holoLoc.setDirection(direction);
                                    hologram.teleport(holoLoc);
                                }, 0L, 1L));


                                Bukkit.getScheduler().runTaskLater(pluginInstance, () -> {
                                    if (hologram.isValid()) hologram.remove();
                                    Bukkit.getScheduler().cancelTask(taskId.get());
                                }, 6 * 20L);

                                output.add(Placeholder.placeholder(rawMessage, placeholders));
                            });
                    }}});

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
