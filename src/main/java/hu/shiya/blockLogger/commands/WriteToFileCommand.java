package hu.shiya.blockLogger.commands;

import hu.shiya.blockLogger.services.BlockLogger;
import hu.shiya.blockLogger.services.SQL;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WriteToFileCommand implements CommandExecutor {
    private final SQL sqlInstance;
    private final BlockLogger blockLogger;

    public WriteToFileCommand( final BlockLogger blockLogger, final SQL sqlInstance ) {
        this.sqlInstance = sqlInstance;
        this.blockLogger = blockLogger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (commandSender instanceof Player) {
            try {
                Bukkit.getScheduler().runTaskAsynchronously(blockLogger, () -> {
                    sqlInstance.writeLoggedBlocksAsync("statistics.txt");
                    Bukkit.getScheduler().runTask(blockLogger, () -> {
                       blockLogger.getMessageManager().get("messages.write.write-true");
                    });
                });
            } catch (Exception e) {
                blockLogger.getLogger().severe( e.getMessage() );
            }
            return true;
        }
        return true;
    }
}
