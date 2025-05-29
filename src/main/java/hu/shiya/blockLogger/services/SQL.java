package hu.shiya.blockLogger.services;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.sql.*;
import java.util.ArrayList;

public class SQL {
    private final BlockLogger blockLogger;
    private Connection connection;

    public SQL(final BlockLogger blockLogger) {
        this.blockLogger = blockLogger;
    }

    public void handleDatabaseAsync(String host, String password, String user, String database) {
        try {
            String url = "jdbc:mysql://" + host + "/" + database;
            connection = DriverManager.getConnection(url, user, password);
            blockLogger.getLogger().info("Connected to the database");

            if (connection != null && !connection.isClosed()) {
                String sql = "CREATE TABLE IF NOT EXISTS logged_blocks (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "type VARCHAR(50)," +
                        "playername VARCHAR(50)," +
                        "block VARCHAR(100)," +
                        "world VARCHAR(100)," +
                        "x DOUBLE," +
                        "y DOUBLE," +
                        "z DOUBLE," +
                        "time BIGINT)";

                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
                blockLogger.getLogger().info("Created the table");

            }
        } catch (SQLException e) {
            blockLogger.getLogger().severe(e.getMessage());
        }
    }

    public void saveLoggedBlocksAsync(Data data) {
        try {
            if (connection == null || connection.isClosed()) {
                blockLogger.getLogger().severe("Connection is null or is closed");
            } else {
                String sql = "INSERT INTO logged_blocks (type, playername, block, world, x ,y ,z, time) VALUES (?, ?, ? ,? ,? ,? ,?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, data.getType());
                statement.setString(2, data.getPlayerName());
                statement.setString(3, data.getBlock());
                statement.setString(4, data.getLocation().getWorld().getName());
                statement.setDouble(5, data.getLocation().getBlockX());
                statement.setDouble(6, data.getLocation().getBlockY());
                statement.setDouble(7, data.getLocation().getBlockZ());
                statement.setLong(8, data.getTime());

                statement.executeUpdate();
                blockLogger.getLogger().info("Added the elements successfully");
            }
        } catch (SQLException e) {
            blockLogger.getLogger().severe(e.getMessage());
        }
    }

    public ArrayList<Data> rollBackLogicAsync(long checkTime, String playerName) {
        try {
            if (connection == null || connection.isClosed()) {
                blockLogger.getLogger().severe("Connection is null or is closed");
                return null;
            } else {
                ArrayList<Data> datas = new ArrayList<>();
                String sql = "SELECT * FROM logged_blocks WHERE time > ? AND playername = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setLong(1, checkTime);
                statement.setString(2, playerName);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Data data = new Data();
                    data.setType(resultSet.getString("type"));
                    data.setPlayerName(resultSet.getString("playername"));
                    data.setBlock(resultSet.getString("block"));
                    Location location = data.createLocationSync(data);
                    data.setLocation(location);
                    data.setTime(resultSet.getLong("time"));
                    blockLogger.getLogger().info("Retrieved the elements successfully");
                    datas.add(data);
                }
                return datas;
            }

        } catch (Exception e) {
            blockLogger.getLogger().severe(e.getMessage());
            return null;
        }
    }

    //OLYAN QUERY KELL AMI MEGEGYEZIK EGY PARAMETER NEVVEL ES WITHINRADIUS LESZ LOCATION PARAMETER, radius parameter
    // database.query("SELECT * FROM log WHERE x>? AND x<? ...", playerX-10, playerX+10, ...);

    public ArrayList<Data> locateLogicPlayerAsync(String playerName, Location location, int radius) {
        try {
            if (connection == null || connection.isClosed()) {
                blockLogger.getLogger().severe("Connection is null or is closed");
                return null;
            } else {
                String sql = "SELECT * FROM logged_blocks WHERE playername = ? AND x>? AND x<? AND y>? AND y<? AND z>? AND z<?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, playerName);
                statement.setDouble(2, location.getX() + radius);
                statement.setDouble(3, location.getX() - radius);
                statement.setDouble(4, location.getY() + radius);
                statement.setDouble(5, location.getY() - radius);
                statement.setDouble(6, location.getZ() + radius);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<Data> datas = new ArrayList<>();
                while (resultSet.next()) {
                    Data data = new Data();
                    data.setType(resultSet.getString("type"));
                    data.setPlayerName(resultSet.getString("playername"));
                    data.setBlock(resultSet.getString("block"));
                    Location location2 = data.createLocationSync(data);
                    data.setLocation(location2);
                    data.setTime(resultSet.getLong("time"));
                    blockLogger.getLogger().info("Retrieved the elements successfully");
                    datas.add(data);
                }
                return datas;
            }

        } catch (Exception e) {
            blockLogger.getLogger().severe(e.getMessage());
            return null;
        }
    }

    public ArrayList<Data> locateLogicAsync( Location location, int radius) {
        try {
            if (connection == null || connection.isClosed()) {
                blockLogger.getLogger().severe("Connection is null or is closed");
                return null;
            } else {
                String sql = "SELECT * FROM logged_blocks WHERE x>? AND x<? AND y>? AND y<? AND z>? AND z<?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setDouble(2, location.getX() + radius);
                statement.setDouble(3, location.getX() - radius);
                statement.setDouble(4, location.getY() + radius);
                statement.setDouble(5, location.getY() - radius);
                statement.setDouble(6, location.getZ() + radius);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<Data> datas = new ArrayList<>();
                while (resultSet.next()) {
                    Data data = new Data();
                    data.setType(resultSet.getString("type"));
                    data.setPlayerName(resultSet.getString("playername"));
                    data.setBlock(resultSet.getString("block"));
                    Location location2 = data.createLocationSync(data);
                    data.setLocation(location2);
                    data.setTime(resultSet.getLong("time"));
                    blockLogger.getLogger().info("Retrieved the elements successfully");
                    datas.add(data);
                }
                return datas;
            }

        } catch (Exception e) {
            blockLogger.getLogger().severe(e.getMessage());
            return null;
        }
    }

    public void disableDatabaseAsync() {
        try {
            if (connection == null || connection.isClosed()) {
                blockLogger.getLogger().severe("Connection is null or is closed");
            } else {
                connection.close();
                blockLogger.getLogger().info("Disabling the database");
            }
        } catch (Exception e) {
            blockLogger.getLogger().severe(e.getMessage());
        } finally {
            connection = null;
        }
    }
}
