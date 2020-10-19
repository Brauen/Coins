package com.veracitymc.coins.backend.backends;

import com.veracitymc.coins.Coins;
import com.veracitymc.coins.backend.BackendType;
import com.veracitymc.coins.backend.VeracityBackend;
import com.veracitymc.coins.game.player.VeracityProfile;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FlatfileBackend extends VeracityBackend {

    public FlatfileBackend() {
        super(BackendType.FLATFILE);
        setLoaded(true);
    }

    @Override
    public void close() {
        return;
    }

    /*=============================*/
    // Profile
    @Override
    public void createProfile(VeracityProfile profile) {

    }

    @Override
    public void deleteProfile(VeracityProfile profile) {
        File d = new File(Coins.getInstance().getDataFolder() + File.separator + "profile.yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(d);

        data.set("profiles." + profile.getUuid().toString(), null);

        if (VeracityProfile.getProfiles().containsKey(profile))
            VeracityProfile.getProfiles().remove(profile);
    }

    @Override
    public void saveProfile(VeracityProfile profile) {
        File d = new File(Coins.getInstance().getDataFolder() + File.separator + "profile.yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(d);

        data.set("profiles." + profile.getUuid().toString() + ".name", profile.getName());
        data.set("profiles." + profile.getUuid().toString() + ".coins", profile.getCoins());
        data.set("profiles." + profile.getUuid().toString() + ".mined", profile.getMined());
        data.set("profiles." + profile.getUuid().toString() + ".pvp", profile.getPvpkills());
        data.set("profiles." + profile.getUuid().toString() + ".pve", profile.getPvekills());
        data.set("profiles." + profile.getUuid().toString() + ".online", profile.getOnlinetime());

        try {
            data.save(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveProfileSync(VeracityProfile profile) {
        saveProfile(profile);
    }

    @Override
    public synchronized void loadProfile(VeracityProfile profile) {
        File d = new File(Coins.getInstance().getDataFolder() + File.separator + "profile.yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(d);

        if (data.contains("profiles")) {
            for (String s : data.getConfigurationSection("profiles").getKeys(false)) {
                if (profile.getUuid().equals(UUID.fromString(s))) {
                    profile.setName(data.getString("profiles." + s + ".name"));
                    profile.setCoins(data.getInt("profiles." + s + ".coins"));
                    profile.setMined(data.getInt("profiles." + s + ".mined"));
                    profile.setPvpkills(data.getInt("profiles." + s + ".pvp"));
                    profile.setPvekills(data.getInt("profiles." + s + ".pve"));
                    profile.setOnlinetime(data.getInt("profiles." + s + ".online"));
                } else continue;
            }
        }
    }

    @Override
    public void loadProfiles() {
        File d = new File(Coins.getInstance().getDataFolder() + File.separator + "profile.yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(d);

        if (data.contains("profiles")) {
            for (String s : data.getConfigurationSection("profiles").getKeys(false)) {
                UUID uuid = UUID.fromString(s);
                String name = data.getString("profiles." + s + ".name");
                int coins = data.getInt("profiles." + s + ".coins");
                int mined = data.getInt("profiles." + s + ".mined");
                int pvp = data.getInt("profiles." + s + ".pvp");
                int pve = data.getInt("profiles." + s + ".pve");
                int online = data.getInt("profiles." + s + ".online");
                VeracityProfile profile = new VeracityProfile(uuid);
                profile.setName(name);
                profile.setCoins(coins);
                profile.setMined(mined);
                profile.setPvpkills(pvp);
                profile.setPvekills(pve);
                profile.setOnlinetime(online);
                VeracityProfile.getProfiles().put(profile.getUuid().toString(), profile);
            }
        }
    }

    @Override
    public void deleteProfiles() {
        File d = new File(Coins.getInstance().getDataFolder() + File.separator + "profile.yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(d);

        if (data.contains("profiles")) {
            data.set("profiles", null);
        }

        try {
            data.save(d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
