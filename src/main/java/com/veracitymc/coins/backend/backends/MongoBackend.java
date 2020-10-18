package com.veracitymc.coins.backend.backends;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.veracitymc.coins.Coins;
import com.veracitymc.coins.backend.BackendType;
import com.veracitymc.coins.backend.IBackend;
import com.veracitymc.coins.backend.VeracityBackend;
import com.veracitymc.coins.backend.creds.MongoCredentials;
import com.veracitymc.coins.game.player.VeracityProfile;
import com.veracitymc.coins.utils.TaskUtils;
import org.bson.Document;

import java.util.Collections;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class MongoBackend extends VeracityBackend implements IBackend {

    private MongoClient mongo;
    private MongoDatabase db;
    private MongoCollection<Document> profiles;

    public MongoBackend(MongoCredentials credentials) {
        super(BackendType.MONGO);

        try {
            ServerAddress address = new ServerAddress(credentials.getHostname(), credentials.getPort());

            if(Coins.getInstance().getConfig().getBoolean("backend.mongo.auth.enable")) {
                MongoCredential credential = MongoCredential.createCredential(credentials.getUsername(), credentials.getAuthDb(), credentials.getPassword().toCharArray());
                this.mongo = new MongoClient(address, Collections.singletonList(credential));
            } else {
                this.mongo = new MongoClient(address);
            }

            this.db = this.mongo.getDatabase(credentials.getDatabase());
            this.profiles = this.db.getCollection("profiles");

            this.logInfoMessage("Mongo Driver successfully loaded.");
            setLoaded(true);
        } catch(Exception e) {
            this.logException("Mongo Driver failed to load.", e);
        }
    }

    @Override
    public void close() {
        if(this.mongo != null)
            this.mongo.close();
    }

    /*=============================*/
    // Profiles

    @Override
    public void createProfile(VeracityProfile profile) {
        TaskUtils.runAsync(() -> {
            this.profiles.insertOne(profile.toDocument());
        });
    }
    @Override
    public void deleteProfile(VeracityProfile profile) {
        TaskUtils.runAsync(() -> {
            this.profiles.deleteOne(eq("uuid", profile.getUuid().toString()));
        });
    }
    @Override
    public void deleteProfiles() {
        TaskUtils.runAsync(() -> {
            this.profiles.drop();
            this.profiles = this.db.getCollection("profiles");
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
        Document doc = profile.toDocument();
        this.profiles.findOneAndReplace(eq("uuid", profile.getUuid().toString()), doc);
    }
    @Override
    public void loadProfile(VeracityProfile profile) {
        Document doc = this.profiles.find(eq("uuid", profile.getUuid().toString())).first();
        if(doc != null) {
            profile.load(doc);
        } else {
            this.createProfile(profile);
        }
    }
    @Override
    public void loadProfiles() {
        for(Document doc : this.profiles.find()) {
            if(!doc.containsKey("uuid"))
                continue;
            UUID uuid = UUID.fromString(doc.getString("uuid"));
            VeracityProfile.getByUuid(uuid);
        }
    }
}
