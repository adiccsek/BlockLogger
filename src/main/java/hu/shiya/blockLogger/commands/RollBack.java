package hu.shiya.blockLogger.commands;

import hu.shiya.blockLogger.services.BlockLogger;
import hu.shiya.blockLogger.services.Data;
import hu.shiya.blockLogger.services.Placeholder;
import hu.shiya.blockLogger.services.SQL;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RollBack implements CommandExecutor {
    private final BlockLogger pluginInstance;
    private final SQL sqlInstance;

    public RollBack(final BlockLogger pluginInstance, final SQL sqlInstance) {
        this.pluginInstance = pluginInstance;
        this.sqlInstance = sqlInstance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull[] args) {
        String targetPlayer;
        long getTime;
        long currentTime;
        List<String> output = new ArrayList<>();

        if (!commandSender.hasPermission("rollback-command")) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("player", commandSender.getName());

            String rawMessage = pluginInstance.getConfig().getString("messages.rollback.no-permission-error");
            commandSender.sendMessage(ChatColor.RED + Placeholder.placeholder(rawMessage, placeholders));
            return true;
        }

        if (args.length != 2) {
            String message = pluginInstance.getConfig().getString("messages.rollback.arguments-error");
            commandSender.sendMessage(message);
            return true;
        }

        if (commandSender instanceof Player player) {
            try {
                targetPlayer = args[0];
                getTime = Long.parseLong(args[1]);
            } catch (Exception e) {
                String message = pluginInstance.getConfig().getString("messages.rollback.usage-error");
                output.add(message);
                return true;
            }

            currentTime = System.currentTimeMillis() / 60000;
            long checkTime = currentTime - getTime;

            Bukkit.getScheduler().runTaskAsynchronously(pluginInstance, () -> {
                ArrayList<Data> loopDatas = sqlInstance.rollBackLogicAsync(checkTime, targetPlayer);

                for (Data data : loopDatas) {
                    String playerName = data.getPlayerName();
                    sqlInstance.handleRollBackAsync(checkTime, data);

                    if (!playerName.equals(targetPlayer)) {
                        String message = pluginInstance.getConfig().getString("messages.rollback.name-error");
                        output.add(message);
                        return;
                    }

                    Bukkit.getScheduler().runTask(pluginInstance, () -> {
                        if ("break".equals(data.getType())) {
                            data.getLocation().getBlock().setType(Material.valueOf(data.getBlock()));
                            itemHandlingTake(targetPlayer, player, data);
                        } else {
                            data.getLocation().getBlock().setType(Material.AIR);
                            itemHandlingAdd(targetPlayer, player, data);
                        }
                    });
                }
            });
            for (String message : output) {
                player.sendMessage (message);
            }
            return true;
        }
        return true;
    }

    private void itemHandlingAdd(String targetPlayer, Player player, Data data) {
        if ("SURVIVAL".equals(data.getGameMode())) {
            Player target = Bukkit.getPlayer(targetPlayer);
            Inventory inventory = target.getInventory();
            ItemStack item = new ItemStack(Material.valueOf(data.getBlock()));

            inventory.addItem(item);
            player.sendMessage("You have added " + data.getBlock() + " to the inventory of: " + targetPlayer);
        }
    }
    private void itemHandlingTake(String targetPlayer, Player player, Data data) {
        if ("SURVIVAL".equals(data.getGameMode())) {
            Player target = Bukkit.getPlayer(targetPlayer);
            if (target == null) {
                player.sendMessage("Target player not found.");
                return;
            }

            Inventory inventory = target.getInventory();
            Material material = Material.valueOf(data.getRollBlock());
            int amountToTake = data.getRollAmount();
            int remaining = amountToTake;

            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack currentItem = inventory.getItem(i);

                if (currentItem != null && currentItem.getType() == material) {
                    int stackAmount = currentItem.getAmount();

                    if (stackAmount <= remaining) {
                        inventory.setItem(i, null);
                        remaining -= stackAmount;
                    } else {
                        currentItem.setAmount(stackAmount - remaining);
                        remaining = 0;
                        break;
                    }

                    if (remaining <= 0) break;
                }
            }

            if (remaining > 0) {
                player.sendMessage("Only partially removed " + (amountToTake - remaining) + "x " + material + ".");
            } else {
                player.sendMessage("You have taken " + amountToTake + "x " + material + " from " + targetPlayer + ".");
            }
        }
    }
}