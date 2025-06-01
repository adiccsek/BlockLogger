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
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LocateCommand implements CommandExecutor {

    private final BlockLogger blockLogger;
    private final SQL sqlInstance;

    public LocateCommand(final BlockLogger plugin, final SQL sqlInstance) {
        this.blockLogger = plugin;
        this.sqlInstance = sqlInstance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull[] args) {

        List<String> output = new ArrayList<>();

        if (!commandSender.hasPermission("locate-command")) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("player", commandSender.getName());

            String rawMessage = blockLogger.getMessageManager().get( "messages.locate.no-permission-error" );
            commandSender.sendMessage(blockLogger.getPrefixUtil().getPrefix() + " " + ChatColor.RED + Placeholder.placeholder(rawMessage, placeholders));
            return true;
        }

        if (args.length > 2 || args.length == 0) {
            String message = blockLogger.getMessageManager().get( "messages.locate.arguments-error" );
            if (message == null) {
                message = ChatColor.RED + "Usage: /locate <radius> [player]";
            }
            commandSender.sendMessage( blockLogger.getPrefixUtil().getPrefix() + " " + ChatColor.RED + message);
            return true;
        }

        if (commandSender instanceof Player player) {
            Location currentLocation = player.getLocation();

            Bukkit.getScheduler().runTaskAsynchronously(blockLogger, () -> {
                int radius;
                String playerName = null;

                try {
                    radius = Integer.parseInt(args[0]);
                    if (args.length == 2) {
                        playerName = args[1];
                    }
                } catch (NumberFormatException e) {
                    String message = blockLogger.getMessageManager().get( "messages.locate.radius-error" );
                    player.sendMessage(blockLogger.getPrefixUtil().getPrefix() + " " +  ChatColor.RED + message);
                    return;
                }

                ArrayList<Data> loopDatas = sqlInstance.locateLogicPlayerAsync(playerName, currentLocation, radius);

                for (Data data : loopDatas) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    String rawMessage = blockLogger.getMessageManager().get( "messages.db.connection-false" );;

                    placeholders.put("player", data.getPlayerName());
                    placeholders.put("type", data.getType());
                    placeholders.put("block", String.valueOf(data.getBlock()));
                    placeholders.put("x", String.valueOf(data.getLocation().getBlockX()));
                    placeholders.put("y", String.valueOf(data.getLocation().getBlockY()));
                    placeholders.put("z", String.valueOf(data.getLocation().getBlockZ()));

                    Bukkit.getScheduler().runTask(blockLogger, () -> {
                        TextDisplay hologram = data.getLocation().getWorld().spawn(data.getLocation(), TextDisplay.class);

                        hologram.setText(ChatColor.RED + Placeholder.placeholder(rawMessage, placeholders));
                        hologram.setDefaultBackground(false);
                        hologram.setAlignment(TextDisplay.TextAlignment.CENTER);
                        hologram.setSeeThrough(true);
                        hologram.setShadowed(false);
                        hologram.setLineWidth(150);

                        AtomicInteger taskId = new AtomicInteger();

                        taskId.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(blockLogger, () -> {

                            if (!hologram.isValid()) {
                                Bukkit.getScheduler().cancelTask(taskId.get());
                                return;
                            }

                            hologram.teleport(data.getLocation());

                            Location holoLoc = hologram.getLocation();
                            Vector direction = player.getLocation().toVector().subtract(holoLoc.toVector());
                            holoLoc.setDirection(direction);
                            hologram.teleport(holoLoc);

                        }, 0L, 1L));

                        Bukkit.getScheduler().runTaskLater(blockLogger, () -> {
                            if (hologram.isValid()) hologram.remove();
                            Bukkit.getScheduler().cancelTask(taskId.get());
                        }, 6 * 20L);

                        output.add(Placeholder.placeholder( blockLogger.getPrefixUtil().getPrefix() + " " + ChatColor.RED + rawMessage, placeholders));
                    });
                }
            });
        }
        return true;
    }
}
