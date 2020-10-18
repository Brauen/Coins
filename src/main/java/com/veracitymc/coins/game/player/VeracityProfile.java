package com.veracitymc.coins.game.player;

import com.veracitymc.coins.Coins;
import com.veracitymc.coins.utils.ConfigValues;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VeracityProfile {

    @Getter private static final Map<String, VeracityProfile> profiles = new HashMap<>();

    @Getter private final UUID uuid;
    @Getter @Setter private int coins, mined, pvpkills, pvekills, onlinetime;

    public VeracityProfile(UUID uuid, boolean cache) {
        this.uuid = uuid;

        Coins.getInstance().getBackend().loadProfile(this);

        if (cache)
            profiles.put(uuid.toString(), this);
    }

    public VeracityProfile(UUID uuid) {
        this(uuid, true);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public void removeCoins(int amount) {
        coins -= amount;
    }

    public void resetCoins() {
        coins = 0;
    }

    public void addMined() {
        mined++;

        if (mined % ConfigValues.MINING_TIMES == 0)
            addCoins(ConfigValues.MINING_COINS);
    }

    public void addPvPKills() {
        pvpkills++;

        if (pvpkills % ConfigValues.PVP_KILLS == 0)
            addCoins(ConfigValues.PVP_KILLS_COINS);
    }

    public void addPvEKills() {
        pvekills++;

        if (pvekills % ConfigValues.PVE_KILLS == 0)
            addCoins(ConfigValues.PVE_KILLS_COINS);
    }

    public void increaseOnlineTime() {
        onlinetime++;

        if (onlinetime % (ConfigValues.ONLINE_TIME * 60) == 0)
            addCoins(ConfigValues.ONLINE_TIME_COINS);
    }

    public void save() {
        Coins.getInstance().getBackend().saveProfile(this);
    }

    public void load(Document doc) {
        if (doc.get("coins") != null)
            this.setCoins(doc.getInteger("coins"));
        if (doc.get("mined") != null)
            this.setMined(doc.getInteger("mined"));
        if (doc.get("pvp") != null)
            this.setPvpkills(doc.getInteger("pvp"));
        if (doc.get("pve") != null)
            this.setPvekills(doc.getInteger("pve"));
        if (doc.get("online") != null)
            this.setOnlinetime(doc.getInteger("online"));
    }

    public Document toDocument() {
        Document doc = new Document();

        doc.append("uuid", this.getUuid().toString());
        doc.append("coins", this.getCoins());
        doc.append("mined", this.getMined());
        doc.append("pvp", this.getPvpkills());
        doc.append("pve", this.getPvekills());
        doc.append("online", this.getOnlinetime());

        return doc;
    }

    public static VeracityProfile getByUuid(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return new VeracityProfile(uuid, true);
        }

        return getByPlayer(player);
    }

    public static VeracityProfile getByUuid(UUID uuid, boolean cache) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return new VeracityProfile(uuid, cache);
        }

        return getByPlayer(player);
    }

    public static VeracityProfile getByPlayer(Player player) {
        if(profiles.containsKey(player.getUniqueId().toString()))
            return profiles.get(player.getUniqueId().toString());

        return new VeracityProfile(player.getUniqueId(), true);
    }

    public static void instate() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Coins.getInstance(), () -> {
            Bukkit.getLogger().info("Attempting to save all online profiles...");
            long start = System.currentTimeMillis();
            VeracityProfile.getProfiles().values().forEach(Coins.getInstance().getBackend()::saveProfile);

            long end = System.currentTimeMillis();
            Bukkit.getLogger().info("Saved " + VeracityProfile.getProfiles().size() + " profile(s). Took " + (end - start) + "ms.");
        }, 200L, (20L * 60L) * 10L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(Coins.getInstance(), () -> {
            for (Map.Entry<String, VeracityProfile> profile : VeracityProfile.getProfiles().entrySet()) {
                if (profile.getValue().getPlayer().isOnline())
                    profile.getValue().increaseOnlineTime();
            }
        }, 20L * 60L, 20L * 60L);
    }
}
