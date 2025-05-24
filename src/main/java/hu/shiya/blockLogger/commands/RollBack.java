package hu.shiya.blockLogger.commands;

import hu.shiya.blockLogger.BlockLogger;
import hu.shiya.blockLogger.Data;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RollBack implements CommandExecutor {



    private final BlockLogger pluginInstance;
    public RollBack( final BlockLogger pluginInstance ) {
        this.pluginInstance = pluginInstance;
    }

    public Location deserializeLocation(ConfigurationSection locSection) {
        if (locSection == null) return null;
        World world = Bukkit.getWorld(locSection.getString("world"));
        double x = locSection.getDouble("x");
        double y = locSection.getDouble("y");
        double z = locSection.getDouble("z");
        return new Location(world, x, y, z);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!commandSender.hasPermission("rollback-command")) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        String targetPlayer;
        long getTime;
        long currentTime;

        if (args.length != 2) {
            return true;
        }
        if ( commandSender instanceof Player player ) {
            try {
                targetPlayer = args[0];
                getTime = Long.parseLong(args[1]);

            } catch ( Exception e ) {
                player.sendMessage("The correct usage is: /rollback <name> <time>"); //placeholder later
                return true;
            }
            currentTime = System.currentTimeMillis() / 60000;
            long checkTime = currentTime - getTime;
            ConfigurationSection conf = pluginInstance.getConfig().getConfigurationSection( "logs" );
            for (String key : conf.getKeys(false)) {
                ConfigurationSection section = conf.getConfigurationSection(key);
                long time = section.getLong("time");
                String playerName = section.getString("playername");

                if ( time > checkTime ) {
                    if (!playerName.equals(targetPlayer)) {
                        player.sendMessage("The name does not match!");
                        return true;
                    }
                    Data actionData = new Data();
                    actionData.load(section);
                     if ("break".equals(actionData.getType())) {
                         actionData.getLocation().getBlock().setType(Material.valueOf(actionData.getBlock()));
                     } else {
                         actionData.getLocation().getBlock().setType(Material.AIR);
                     }
                }
            }
        }
        return true;
    }
}
