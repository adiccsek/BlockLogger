package hu.shiya.blockLogger.commands;

import hu.shiya.blockLogger.services.BlockLogger;
import hu.shiya.blockLogger.services.Data;
import hu.shiya.blockLogger.services.Placeholder;
import hu.shiya.blockLogger.services.SQL;
import hu.shiya.blockLogger.utils.FallBlockUtility;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.ChatPaginator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RollBack implements CommandExecutor {
    private final BlockLogger blockLogger;
    private final SQL sqlInstance;

    public RollBack(final BlockLogger blockLogger, final SQL sqlInstance) {
        this.blockLogger = blockLogger;
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

            String rawMessage = blockLogger.getMessageManager().get( "messages.rollback.no-permission-error" );
            commandSender.sendMessage(blockLogger.getPrefixUtil().getPrefix() + " " + ChatColor.RED + Placeholder.placeholder(rawMessage, placeholders));
            return true;
        }

        if (args.length != 2) {
            String message = blockLogger.getMessageManager().get( "messages.rollback.arguments-error" );
            commandSender.sendMessage(blockLogger.getPrefixUtil().getPrefix() + " " + ChatColor.RED + message);
            return true;
        }

        if (commandSender instanceof Player player) {
            try {
                targetPlayer = args[0];
                getTime = Long.parseLong(args[1]);
            } catch (Exception e) {
                String message = blockLogger.getMessageManager().get("messages.rollback.usage-error" );
                output.add(blockLogger.getPrefixUtil().getPrefix() + " " + ChatColor.RED +  message);
                return true;
            }

            currentTime = System.currentTimeMillis() / 60000;
            long checkTime = currentTime - getTime;

            Bukkit.getScheduler().runTaskAsynchronously(blockLogger, () -> {
                ArrayList<Data> loopDatas = sqlInstance.rollBackLogicAsync(checkTime, targetPlayer);

                for (Data data : loopDatas) {
                    String playerName = data.getPlayerName();
                    sqlInstance.handleRollBackAsync(checkTime, data);

                    if (!playerName.equals(targetPlayer)) {
                        String message = blockLogger.getMessageManager().get(  "messages.rollback.name-error" );;
                        output.add(blockLogger.getPrefixUtil().getPrefix() + " " + ChatColor.RED + message);
                        return;
                    }

                    Bukkit.getScheduler().runTask(blockLogger, () -> {
                        if (!"break".equals(data.getType())) {
                            Material material = Material.getMaterial(data.getBlock());
                            if (FallBlockUtility.isFallableBlock(material)) {
                                World world = data.getLocation().getWorld();
                                int x = data.getLocation().getBlockX();
                                int z = data.getLocation().getBlockZ();
                                int y = data.getLocation().getBlockY();
                                removeFallBlockOnXZColumn(world, x, z, y);
                                itemHandlingAdd(targetPlayer, player, data);
                            } else {
                                data.getLocation().getBlock().setType(Material.AIR);
                                itemHandlingAdd(targetPlayer, player, data);
                            }
                        } else {
                            data.getLocation().getBlock().setType(Material.valueOf(data.getBlock()));
                            itemHandlingTake(targetPlayer, player, data);
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
    public void removeFallBlockOnXZColumn(World world, int x, int z, int getY) {
            for (int y = getY; y >= 0; y--) {
                Location loc = new Location(world, x, y, z);
                Material blockType = loc.getBlock().getType();

                if (FallBlockUtility.isFallableBlock(blockType)) {
                    loc.getBlock().setType(Material.AIR);
                    return;
                }
            }
        }

    private void itemHandlingAdd(String targetPlayer, Player player, Data data) {
        if ("SURVIVAL".equals(data.getGameMode())) {
            Player target = Bukkit.getPlayer(targetPlayer);
            Inventory inventory = target.getInventory();
            ItemStack item = new ItemStack(Material.valueOf(data.getBlock()));

            inventory.addItem(item);
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("block", data.getBlock());
            placeholders.put("player", player.getName());

            String rawMessage = blockLogger.getMessageManager().get( "messages.rollback.no-permission-error" );
            player.sendMessage(blockLogger.getPrefixUtil().getPrefix() + " " + ChatColor.RED + Placeholder.placeholder(rawMessage, placeholders));
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
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("material", data.getBlock());
                placeholders.put("amount", String.valueOf(amountToTake - remaining));
                String rawMessage = blockLogger.getMessageManager().get( "messages.rollback.take-partially-correct" );

                player.sendMessage(blockLogger.getPrefixUtil().getPrefix() + " " + ChatColor.RED + Placeholder.placeholder(rawMessage, placeholders));
            } else {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("material", data.getBlock());
                placeholders.put("amount", String.valueOf(amountToTake));
                placeholders.put("player", targetPlayer);
                String rawMessage = blockLogger.getMessageManager().get( "messages.rollback.take-correct" );

                player.sendMessage(blockLogger.getPrefixUtil().getPrefix() + " " + ChatColor.RED + Placeholder.placeholder(rawMessage, placeholders));
            }
        }
    }
}