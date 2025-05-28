package hu.shiya.blockLogger.services;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.sql.*;

public class SQL {
    private final BlockLogger blockLogger;
    private Connection connection;
    public SQL( final BlockLogger blockLogger ) {
        this.blockLogger = blockLogger;
    }

    public void handleDatabaseAsync(String host, String password, String user, String database) {
        try {
            String url = "jdbc:mysql://" + host + "/" + database;
            connection = DriverManager.getConnection(url, user, password);
            blockLogger.getLogger().info("Connected to the database");

            if (connection != null && !connection.isClosed()) {
                String sql = "CREATE TABLE IF NOT EXISTS logged_blocks (" +
                "id INT AUTO_INCREMENT PRIMARY KEY" +
                "type VARCHAR(50)" +
                "playername VARCHAR(50)" +
                "block VARCHAR(100)" +
                "world VARCHAR(100)" +
                "x DOUBLE" +
                "y DOUBLE" +
                "z DOUBLE" +
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
                statement.setDouble(4, data.getLocation().getBlockX());
                statement.setDouble(5, data.getLocation().getBlockY());
                statement.setDouble(6, data.getLocation().getBlockZ());
                statement.setLong(7, data.getTime());
                statement.executeUpdate();
                blockLogger.getLogger().info("Added the elements successfully");
            }
        } catch (SQLException e) {
            blockLogger.getLogger().severe(e.getMessage());
        }
    }

    public Data getLoggedBlocksAsync() {
        try {
            if (connection == null || connection.isClosed()) {
                blockLogger.getLogger().severe("Connection is null or is closed");
            } else {
                String sql = "SELECT * FROM logged_blocks";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                Data data = new Data();
                while (resultSet.next()) {
                    data.setType(resultSet.getString("type"));
                    data.setPlayerName(resultSet.getString("playername"));
                    data.setBlock(resultSet.getString("block"));
                    Location location = data.createLocationSync(data);
                    data.setLocation(location);
                    data.setTime(resultSet.getLong("time"));
                }
                blockLogger.getLogger().info("Retrieved the elements successfully");
                return data;
            }
         } catch (SQLException e) {
            blockLogger.getLogger().severe(e.getMessage());
        }
        return null;
    }
    public int getLengthOfDatabaseAsync() { //VALOSZINU NEM KELL MAJD MAS METHODUSOK WHERE FELTÉTELLEL KELLENEK NE KELLJEN AZ EGÉSZEN VÉGIGMENNI
        try {
            if (connection == null || connection.isClosed()) {
                blockLogger.getLogger().severe("Connection is null or is closed");
            } else {
                String sql = "SELECT COUNT(*) FROM logged_blocks";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                resultSet.next();
                return resultSet.getInt(1);
            }

        } catch (Exception e) {
            blockLogger.getLogger().severe(e.getMessage());
        }
        return 0;
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
