package hu.shiya.blockLogger;

import org.bukkit.Location;
import org.bukkit.World;

public class Data {
    public String PlayerName;
    public String Block;
    public Location Location;
    public long Time;
    public String Type;
    public Data(String playerName, String block, Location location, long time, String type) {
        this.PlayerName = playerName;
        this.Block = block;
        this.Location = location;
        this.Time = time;
        this.Type = type;
    }
}
