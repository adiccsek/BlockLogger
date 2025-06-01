package hu.shiya.blockLogger.utils;

import net.md_5.bungee.api.ChatColor;

public class PrefixUtil {
    public PrefixUtil() {
        getPrefix();
    }
    public String getPrefix() {
        return ChatColor.GOLD + "[" + ChatColor.BLUE + "BlockLogger" + ChatColor.GOLD + "]" ;
    }
}
