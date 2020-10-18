package com.veracitymc.coins.backend.backends;

import com.veracitymc.coins.backend.BackendType;
import com.veracitymc.coins.backend.VeracityBackend;
import com.veracitymc.coins.backend.creds.RedisCredentials;
import com.veracitymc.coins.game.player.VeracityProfile;
import com.veracitymc.coins.utils.TaskUtils;
import lombok.Getter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bson.Document;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;
import java.util.UUID;

public class RedisBackend extends VeracityBackend {

    @Getter private JedisPool pool;

    public RedisBackend(RedisCredentials credentials) {
        super(BackendType.REDIS);

        if(!credentials.password()) {
            this.pool = new JedisPool(new GenericObjectPoolConfig(), credentials.getHost(), credentials.getPort(), 3000);
        } else {
            this.pool = new JedisPool(new GenericObjectPoolConfig(), credentials.getHost(), credentials.getPort(), 3000, credentials.getPassword());
        }

        try(Jedis jedis = pool.getResource()) {
            setLoaded(jedis.isConnected());
            if(isLoaded())
                logInfoMessage("Redis Driver successfully loaded.");
            else
                throw new Exception("Unable to establish Jedis connection.");
        } catch(Exception ex) {
            logException("Redis Driver failed to load.", ex);
        }
    }

    @Override
    public void close() {
        if(this.pool != null)
            if(!this.pool.isClosed())
                this.pool.close();
    }

    /*=============================*/
    // Profiles

    @Override
    public void createProfile(VeracityProfile profile) {
        this.saveProfile(profile);
    }

    @Override
    public void deleteProfile(VeracityProfile profile) {
        try(Jedis jedis = this.getPool().getResource()) {
            jedis.del(this.getKey(KeyType.PROFILE, profile.getUuid().toString()));
        }
    }

    @Override
    public void deleteProfiles() {
        TaskUtils.runAsync(() -> {
            try(Jedis jedis = this.getPool().getResource()) {
                jedis.del(this.getKey(KeyType.PROFILE) + "*");
            }
        });
    }

    @Override
    public void saveProfile(VeracityProfile profile) {
        TaskUtils.runAsync(() -> {
            this.saveProfileSync(profile);
        });
    }

    @Override
    public void saveProfileSync(VeracityProfile profile) {
        try(Jedis jedis = this.getPool().getResource()) {
            jedis.set(this.getKey(KeyType.PROFILE, profile.getUuid().toString()), profile.toDocument().toJson());
        }
    }

    @Override
    public void loadProfile(VeracityProfile profile) {
        try(Jedis jedis = this.getPool().getResource()) {
            String json = jedis.get(this.getKey(KeyType.PROFILE, profile.getUuid().toString()));

            if(json != null) {
                Document doc = Document.parse(json);
                profile.load(doc);
            } else {
                this.createProfile(profile);
            }
        }
    }

    @Override
    public void loadProfiles() {
        try(Jedis jedis = this.getPool().getResource()) {
            Set<String> profiles = jedis.keys(this.getKey(KeyType.PROFILE) + "*");

            profiles.forEach(profile -> {
                Document doc = Document.parse(jedis.get(profile));
                if(doc == null || !doc.containsKey("uuid"))
                    return;

                UUID uuid = UUID.fromString(doc.getString("uuid"));
                VeracityProfile.getByUuid(uuid);
            });
        }
    }

    /*=============================*/

    private String getKey(KeyType type) {
        return "veracitymc" + ":" + type.getPrefix() + ":";
    }

    private String getKey(KeyType type, String identifier) {
        return getKey(type) + identifier;
    }

    private enum KeyType {

        PROFILE("profile");

        @Getter private String prefix;

        KeyType(String prefix) {
            this.prefix = prefix;
        }
    }
}
