package hu.shiya.blockLogger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class Data {
    // private final BlockLogger pluginInstance;

    private String playerName;
    private String block;
    private Location location;
    private long time;
    private String type;
    public Data(String playerName, String block, Location location, long time, String type) { //BlockLogger pluginInstance
        this.playerName = playerName;
        this.block = block;
        this.location = location;
        this.time = time;
        this.type = type;
        //this.pluginInstance = pluginInstance;
    }
    public String getPlayerName() {
        return playerName;
    }
    public String getBlock() {
        return block;
    }
    public Location getLocation() {
        return location;
    }
    public long getTime() {
        return time;
    }
    public String getType() {
        return type;
    }

    public void save(ConfigurationSection conf) {
        conf.set(".type", this.getType());
        conf.set(".playername", this.getPlayerName());
        conf.set(".block", this.getBlock());
        conf.set(".location.world", this.getLocation().getWorld().getName());
        conf.set(".location.x", this.getLocation().getBlockX());
        conf.set(".location.y", this.getLocation().getBlockY());
        conf.set(".location.z", this.getLocation().getBlockZ());
        conf.set(".time", this.getTime());
    }
}
