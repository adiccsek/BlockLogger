package hu.shiya.blockLogger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class Data {
    private String type;
    private String playerName;
    private String block;
    private Location location;
    private long time;
    public Data(String playerName, String block, Location location, long time, String type) {
        this.playerName = playerName;
        this.block = block;
        this.location = location;
        this.time = time;
        this.type = type;
    }
    public Data( ) { }
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
        conf.set(".time", this.getTime() / 60000);
    }

    public void load(ConfigurationSection conf) {
       this.type = conf.getString("type");
       this.playerName = conf.getString("playername");
       this.block = conf.getString("block");
       World bukkitWorld = Bukkit.getWorld(conf.getString("location.world"));
       this.location = new Location(bukkitWorld, conf.getInt("location.x"), conf.getInt("location.y"), conf.getInt("location.z"));
       this.time = conf.getLong("time");
    }
}
