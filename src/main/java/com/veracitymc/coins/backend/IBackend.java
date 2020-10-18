package com.veracitymc.coins.backend;

import com.veracitymc.coins.game.player.VeracityProfile;

public interface IBackend {

    void close();

    void createProfile(VeracityProfile profile);

    void deleteProfile(VeracityProfile profile);

    void deleteProfiles();

    void saveProfile(VeracityProfile profile);

    void saveProfileSync(VeracityProfile profile);

    void loadProfile(VeracityProfile profile);

    void loadProfiles();
}
