package hu.shiya.blockLogger.services;

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
    private String gameMode;
    private String rollBlock;
    private int rollAmount;
    public Data(String playerName, String block, Location location, long time, String type, String gameMode, String rollBlock, int rollAmount) {
        this.playerName = playerName;
        this.block = block;
        this.location = location;
        this.time = time;
        this.type = type;
        this.gameMode = gameMode;
        this.rollBlock = rollBlock;
        this.rollAmount = rollAmount;
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
    public String getGameMode() { return gameMode; }
    public String getRollBlock() { return rollBlock; }
    public int getRollAmount() { return rollAmount; }

    public void setType( String type ) {
        this.type = type;
    }
    public void setPlayerName( String playerName ) {
        this.playerName = playerName;
    }
    public void setBlock( String block ) {
        this.block = block;
    }
    public void setLocation( Location location ) {
        this.location = location;
    }
    public void setTime( long time ) {
        this.time = time;
    }
    public void setGameMode( String gameMode ) { this.gameMode = gameMode; }
    public void setRollBlock( String rollBlock ) { this.rollBlock = rollBlock; }
    public void setRollAmount( int rollAmount ) { this.rollAmount = rollAmount; }
}
