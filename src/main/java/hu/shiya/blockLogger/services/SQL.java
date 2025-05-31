package hu.shiya.blockLogger.services;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.lang.module.Configuration;
import java.sql.*;
import java.util.ArrayList;

public class SQL {
    private final BlockLogger blockLogger;
    private Connection connection;
    private ConfigurationSection config;

    public SQL(final BlockLogger blockLogger, final ConfigurationSection config) {
        this.blockLogger = blockLogger;
        this.config = config;
    }

    public void handleDatabaseAsync(String host, String password, String user, String database) {
        try {
            String url = "jdbc:mysql://" + host + "/" + database;
            connection = DriverManager.getConnection(url, user, password);
            String message = config.getString("messages.db.connection-true");
            blockLogger.getLogger().info(message);

            if (connection != null && !connection.isClosed()) {
                // Create logged_blocks
                String sql1 = "CREATE TABLE IF NOT EXISTS logged_blocks (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "type VARCHAR(50)," +
                        "playername VARCHAR(50)," +
                        "block VARCHAR(100)," +
                        "world VARCHAR(100)," +
                        "x DOUBLE," +
                        "y DOUBLE," +
                        "z DOUBLE," +
                        "time BIGINT," +
                        "gamemode VARCHAR(50)," +
                        "rollblock VARCHAR(100)," +
                        "rollamount INT)";
                PreparedStatement statement1 = connection.prepareStatement(sql1);
                statement1.executeUpdate();

                // Create rolled_logged_blocks
                String sql2 = "CREATE TABLE IF NOT EXISTS rolled_logged_blocks (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "type VARCHAR(50)," +
                        "playername VARCHAR(50)," +
                        "block VARCHAR(100)," +
                        "world VARCHAR(100)," +
                        "x DOUBLE," +
                        "y DOUBLE," +
                        "z DOUBLE," +
                        "time BIGINT ," +
                        "gamemode VARCHAR(50)," +
                        "rollblock VARCHAR(100)," +
                        "rollamount INT)";
                PreparedStatement statement2 = connection.prepareStatement(sql2);
                statement2.executeUpdate();
            }
                message = config.getString("messages.db.tables-true");
                blockLogger.getLogger().info(message);

        } catch (SQLException e) {
            blockLogger.getLogger().severe(e.getMessage());
        }
    }

    public void saveLoggedBlocksAsync(Data data) {
        try {
            if (connection == null || connection.isClosed()) {
                String message = config.getString("db.connection-false");
                blockLogger.getLogger().severe(message);
                return;
            }
            int rowsAffected = 0;
            if (!data.getType().equalsIgnoreCase("place")) {
                String updateSql = "DELETE FROM logged_blocks WHERE world = ? AND x = ? AND y = ? AND z = ? AND playername = ? AND gamemode = ? AND block = ?";

                PreparedStatement deleteStatement = connection.prepareStatement(updateSql);
                deleteStatement.setString(1, data.getLocation().getWorld().getName());
                deleteStatement.setInt(2, data.getLocation().getBlockX());
                deleteStatement.setInt(3, data.getLocation().getBlockY());
                deleteStatement.setInt(4, data.getLocation().getBlockZ());
                deleteStatement.setString(5, data.getPlayerName());
                deleteStatement.setString(6, data.getGameMode());
                deleteStatement.setString(7, data.getBlock());

                rowsAffected = deleteStatement.executeUpdate();
                if (config.getBoolean("debug-messages")) {
                    blockLogger.getLogger().info("Updated rows: " + rowsAffected);
                };
            }

            if (rowsAffected == 0) {
                String insertSql = "INSERT INTO logged_blocks (type, time, world, x, y, z, playername, block, gamemode, rollblock, rollamount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                insertData(data, insertSql);
            }

        } catch (SQLException e) {
            blockLogger.getLogger().severe("Database error: " + e.getMessage());
        }
    }

    public ArrayList<Data> rollBackLogicAsync(long checkTime, String playerName) {
        try {
            if (connection == null || connection.isClosed()) {
                String message = config.getString("db.connection-false");
                blockLogger.getLogger().severe(message);
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
                    Location location = new Location(Bukkit.getWorld(resultSet.getString("world")), resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"));
                    data.setLocation(location);
                    data.setTime(resultSet.getLong("time"));
                    data.setGameMode(resultSet.getString("gamemode"));
                    data.setRollBlock(resultSet.getString("rollblock"));
                    data.setRollAmount(resultSet.getInt("rollamount"));

                   if (config.getBoolean("debug-messages")) {
                       blockLogger.getLogger().info("Retrieved the elements successfully");
                       blockLogger.getLogger().info(data.toString());
                   }
                    datas.add(data);
                }
                return datas;
            }

        } catch (Exception e) {
            blockLogger.getLogger().severe(e.getMessage());
            return null;
        }
    }
    public void handleRollBackAsync(long givenArgumentTime, Data data) {
        try {
            if (connection == null || connection.isClosed()) {
                blockLogger.getLogger().severe("Connection is null or is closed");
            } else {
                String sql = "DELETE FROM logged_blocks WHERE time > ? AND world = ? AND x = ? AND y = ? AND z = ? AND playername = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setLong(1, givenArgumentTime);
                statement.setString(2, data.getLocation().getWorld().getName());
                statement.setDouble(3, data.getLocation().getBlockX());
                statement.setDouble(4, data.getLocation().getBlockY());
                statement.setDouble(5, data.getLocation().getBlockZ());
                statement.setString(6, data.getPlayerName());

                statement.executeUpdate();
                if (config.getBoolean("debug-messages")) {
                    blockLogger.getLogger().info("Deleted the elements successfully (logged_blocks)");
                }

                String sql2 = "INSERT INTO rolled_logged_blocks (type, time, world, x, y, z, playername, block, gamemode, rollblock, rollamount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                insertData(data, sql2);
            }
        } catch ( Exception e ) {
            blockLogger.getLogger().severe(e.getMessage());
        }
    }

    public ArrayList<Data> locateLogicPlayerAsync(String playerName, Location location, int radius) {
        try {
            if (connection == null || connection.isClosed()) {
                String message = config.getString("db.connection-false");
                blockLogger.getLogger().severe(message);
                return null;
            }

                double minX = location.getX() - radius;
                double maxX = location.getX() + radius;
                double minY = location.getY() - radius;
                double maxY = location.getY() + radius;
                double minZ = location.getZ() - radius;
                double maxZ = location.getZ() + radius;

             if (playerName != null) {
                String sql = "SELECT * FROM logged_blocks WHERE playername = ? AND x >= ? AND x <= ? AND y >= ? AND y <= ? AND z >= ? AND z <= ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, playerName);
                statement.setDouble(2, minX);
                statement.setDouble(3, maxX);
                statement.setDouble(4, minY);
                statement.setDouble(5, maxY);
                statement.setDouble(6, minZ);
                statement.setDouble(7, maxZ);
                return getData(statement);
            } else {
                String sql = "SELECT * FROM logged_blocks WHERE x >= ? AND x <= ? AND y >= ? AND y <= ? AND z >= ? AND z <= ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setDouble(1, minX);
                statement.setDouble(2, maxX);
                statement.setDouble(3, minY);
                statement.setDouble(4, maxY);
                statement.setDouble(5, minZ);
                statement.setDouble(6, maxZ);
                return getData(statement);
            }
        } catch (Exception e) {
            blockLogger.getLogger().severe(e.getMessage());
            return null;
        }
    }

    @NotNull
    private ArrayList<Data> getData(PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        ArrayList<Data> datas = new ArrayList<>();
        while (resultSet.next()) {
            Data data = new Data();
            data.setType(resultSet.getString("type"));
            data.setPlayerName(resultSet.getString("playername"));
            data.setBlock(resultSet.getString("block"));
            Location location2 = new Location(Bukkit.getWorld(resultSet.getString("world")), resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"));
            data.setLocation(location2);
            data.setTime(resultSet.getLong("time"));
            data.setGameMode(resultSet.getString("gamemode"));
            data.setRollBlock(resultSet.getString("rollblock"));
            data.setRollAmount(resultSet.getInt("rollamount"));
            if (config.getBoolean("debug-messages")) {
                blockLogger.getLogger().info("Retrieved the elements successfully");
            }
            datas.add(data);
        }
        return datas;
    }

    private void insertData(Data data, String sql2) throws SQLException {
        PreparedStatement insertStatement = connection.prepareStatement(sql2);
        insertStatement.setString(1, data.getType());
        insertStatement.setLong(2, data.getTime());
        insertStatement.setString(3, data.getLocation().getWorld().getName());
        insertStatement.setInt(4, data.getLocation().getBlockX());
        insertStatement.setInt(5, data.getLocation().getBlockY());
        insertStatement.setInt(6, data.getLocation().getBlockZ());
        insertStatement.setString(7, data.getPlayerName());
        insertStatement.setString(8, data.getBlock());
        insertStatement.setString(9, data.getGameMode());
        insertStatement.setString(10, data.getRollBlock());
        insertStatement.setInt(11, data.getRollAmount());

        insertStatement.executeUpdate();
        if (config.getBoolean("debug-messages")) {
            blockLogger.getLogger().info("Inserted the elements successfully");
        }
    }

    public void writeLoggedBlocksAsync(String fileRoute) {
        try {
            FileWriter writer = new FileWriter(fileRoute);
            String sql = "SELECT * FROM rolled_logged_blocks";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Data data = new Data();
                data.setType(resultSet.getString("type"));
                data.setPlayerName(resultSet.getString("playername"));
                data.setBlock(resultSet.getString("block"));
                Location location = new Location(Bukkit.getWorld(resultSet.getString("world")), resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"));
                data.setLocation(location);
                data.setTime(resultSet.getLong("time"));
                data.setGameMode(resultSet.getString("gamemode"));
                data.setRollBlock(resultSet.getString("rollblock"));
                data.setRollAmount(resultSet.getInt("rollamount"));

                writer.write( data.getType() + ";" + data.getPlayerName() + ";" + data.getBlock() + ";" +
                        data.getLocation() + ";" + data.getTime() + ";" + data.getGameMode() + ";" + data.getRollBlock() + data.getRollAmount() + "\n");
            }
            writer.close();
            if (config.getBoolean("debug-messages")) {
                blockLogger.getLogger().info("Written the elements successfully");
            }
        } catch (Exception e) {
            blockLogger.getLogger().severe(e.getMessage());
        }
    }

    public void disableDatabaseAsync() {
        try {
            if (connection == null || connection.isClosed()) {
                String message = config.getString("db.connection-false");
                blockLogger.getLogger().severe(message);
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
