package com.veracitymc.coins.backend.backends;

import com.veracitymc.coins.backend.BackendType;
import com.veracitymc.coins.backend.IBackend;
import com.veracitymc.coins.backend.VeracityBackend;
import com.veracitymc.coins.backend.creds.SQLCredentials;
import com.veracitymc.coins.game.player.VeracityProfile;
import com.veracitymc.coins.utils.TaskUtils;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class SQLBackend extends VeracityBackend implements IBackend {

    @Getter private static Connection connection;

    private static final String INSERT = "INSERT INTO Profiles VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE uuid=?";
    private static final String DELETE = "DELETE FROM Profiles WHERE uuid=?";
    private static final String CHECK = "SELECT * FROM Profiles WHERE uuid=?";

    public SQLBackend(SQLCredentials credentials) {
        super(BackendType.MYSQL);

        try {
            Statement statement;

            openConnection(credentials);
            statement = connection.createStatement();

            DatabaseMetaData meta;
            ResultSet res;

            try {
                boolean exists = false;
                meta = connection.getMetaData();
                res = meta.getTables(null, null, "Profiles", new String[]{"TABLE"});
                while (res.next()) {
                    if (res.getString("TABLE_NAME").equals("Profiles")) {
                        exists = true;
                    }
                }

                if (!exists) {
                    String query = "CREATE TABLE Profiles ("
                            + "uuid VARCHAR(36),"
                            + "coins INT,"
                            + "mined INT,"
                            + "pvp INT,"
                            + "pve INT,"
                            + "online INT"
                            + ")";
                    statement.executeQuery(query);
                }

                setLoaded(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /*=============================*/
    // Profiles

    @Override
    public void createProfile(VeracityProfile profile) {
        TaskUtils.runAsync(() -> {
            try {
                PreparedStatement select = null;
                select = connection.prepareCall(CHECK);

                select.setString(1, profile.getUuid().toString());

                ResultSet set = select.executeQuery();

                if (set.next()) return;

                PreparedStatement insert = connection.prepareStatement(INSERT);
                insert.setString(1, profile.getUuid().toString());
                insert.setInt(2, profile.getCoins());
                insert.setInt(3, profile.getMined());
                insert.setInt(4, profile.getPvpkills());
                insert.setInt(5, profile.getPvekills());
                insert.setInt(6, profile.getOnlinetime());
                insert.setString(7, profile.getUuid().toString());

                insert.executeUpdate();

                if (!VeracityProfile.getProfiles().containsKey(profile.getUuid().toString()))
                    VeracityProfile.getProfiles().put(profile.getUuid().toString(), profile);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void deleteProfile(VeracityProfile profile) {
        try {
            PreparedStatement delete = null;
            delete = connection.prepareStatement(DELETE);

            delete.setString(1, profile.getUuid().toString());

            delete.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteProfiles() {
        try {
            String del = "DELETE from Profiles";
            PreparedStatement statement = connection.prepareStatement(del);

            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void saveProfile(VeracityProfile profile) {
        TaskUtils.runAsync(() -> {
            try {

                PreparedStatement insert = connection.prepareStatement(INSERT);

                insert.setString(1, profile.getUuid().toString());
                insert.setInt(2, profile.getCoins());
                insert.setInt(3, profile.getMined());
                insert.setInt(4, profile.getPvpkills());
                insert.setInt(5, profile.getPvekills());
                insert.setInt(6, profile.getOnlinetime());
                insert.setString(7, profile.getUuid().toString());

                insert.executeUpdate();
            } catch(Exception ex) {
                deleteProfile(profile);
            }
        });
    }

    @Override
    public void saveProfileSync(VeracityProfile profile) {
        try {

            PreparedStatement insert = connection.prepareStatement(INSERT);

            insert.setString(1, profile.getUuid().toString());
            insert.setInt(2, profile.getCoins());
            insert.setInt(3, profile.getMined());
            insert.setInt(4, profile.getPvpkills());
            insert.setInt(5, profile.getPvekills());
            insert.setInt(6, profile.getOnlinetime());
            insert.setString(7, profile.getUuid().toString());

            insert.executeUpdate();
        } catch(Exception ex) {
            deleteProfile(profile);
        }
    }

    @Override
    public void loadProfile(VeracityProfile profile) {

       try {
           PreparedStatement select = null;

           select = connection.prepareCall(CHECK);

           select.setString(1, profile.getUuid().toString());

           ResultSet set = select.executeQuery();

           try {
               while (set.next()) {
                   int coins = set.getInt("coins");
                   int pvp = set.getInt("pvp");
                   int pve = set.getInt("pve");
                   int online = set.getInt("online");

                   profile.setCoins(coins);
                   profile.setPvpkills(pvp);
                   profile.setPvekills(pve);
                   profile.setOnlinetime(online);
               }
           } catch (Exception ex) {
               deleteProfile(profile);
           }
       } catch (SQLException ex) {
          ex.printStackTrace();
       }
    }

    @Override
    public void loadProfiles() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ResultSet set = null;
        try {
            set = statement.executeQuery("SELECT * FROM Profiles");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            while (set.next()) {
                UUID uuid = UUID.fromString(set.getString("uuid"));

                loadProfile(new VeracityProfile(uuid));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void openConnection(SQLCredentials credentials) throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }

            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + credentials.getHost() + ":" + credentials.getPort() + "/" + credentials.getDatabase();
            connection = DriverManager.getConnection(url, credentials.getUsername(), credentials.getPassword());
        }
    }

    /*=============================*/
}
