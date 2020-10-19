package com.veracitymc.coins;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.veracitymc.coins.backend.BackendType;
import com.veracitymc.coins.backend.VeracityBackend;
import com.veracitymc.coins.backend.backends.FlatfileBackend;
import com.veracitymc.coins.backend.backends.MongoBackend;
import com.veracitymc.coins.backend.backends.RedisBackend;
import com.veracitymc.coins.backend.backends.SQLBackend;
import com.veracitymc.coins.backend.creds.MongoCredentials;
import com.veracitymc.coins.backend.creds.RedisCredentials;
import com.veracitymc.coins.backend.creds.SQLCredentials;
import com.veracitymc.coins.backend.files.ItemFile;
import com.veracitymc.coins.commands.CoinsCommand;
import com.veracitymc.coins.game.inventory.CoinsGUI;
import com.veracitymc.coins.game.player.VeracityProfile;
import com.veracitymc.coins.game.player.utils.PlayerUtil;
import com.veracitymc.coins.game.player.utils.UUIDPair;
import com.veracitymc.coins.listeners.ListenerManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.UUID;

@Getter
public final class Coins extends JavaPlugin {

    @Getter private static Coins instance;
    private PaperCommandManager commandManager;
    private VeracityBackend backend;
    private ListenerManager listenerManager;
    private CoinsGUI coinsGUI;
    private ItemFile itemFile;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        BackendType type = BackendType.getOrDefault(getConfig().getString("backend.driver"));
        switch (type) {
            case REDIS: {
                backend = new RedisBackend(
                        new RedisCredentials(
                                getConfig().getString("backend.redis.host"),
                                getConfig().getInt("backend.redis.port"),
                                getConfig().getString("backend.redis.pass")
                        )
                );
                break;
            }
            case MONGO: {
                backend = new MongoBackend(
                        new MongoCredentials(
                                getConfig().getString("backend.mongo.host"),
                                getConfig().getInt("backend.mongo.port"),
                                getConfig().getString("backend.mongo.auth.username"),
                                getConfig().getString("backend.mongo.auth.password"),
                                getConfig().getString("backend.mongo.database"),
                                getConfig().getString("backend.mongo.auth.authDb")
                        )
                );
                break;
            }
            case MYSQL: {
                backend = new SQLBackend(
                        new SQLCredentials(
                                getConfig().getString("backend.mysql.host"),
                                getConfig().getInt("backend.mysql.port"),
                                getConfig().getString("backend.mysql.username"),
                                getConfig().getString("backend.mysql.password"),
                                getConfig().getString("backend.mysql.database")
                        )
                );
                break;
            }
            case FLATFILE: {
                backend = new FlatfileBackend();
            }
        }

        if(!backend.isLoaded()) {
            getLogger().severe("Unable to connect to backend. Shutting down.");
            Bukkit.getServer().shutdown();
            return;
        }

        registerCommands(commandManager = new PaperCommandManager(this));

        listenerManager = new ListenerManager();
        listenerManager.registerListeners();

        itemFile = new ItemFile();
        itemFile.init();

        coinsGUI = new CoinsGUI();
        coinsGUI.loadInventorys();
    }

    @Override
    public void onDisable() {
        if (backend != null && backend.isLoaded()) {
            VeracityProfile.getProfiles().values().forEach(backend::saveProfileSync);

            backend.close();
        }
    }

    public void registerCommands(PaperCommandManager commandManager) {
        registerDependencies(commandManager);
        registerContexts(commandManager);
        Arrays.<BaseCommand>asList(new BaseCommand[] {new CoinsCommand()}).forEach(commandManager::registerCommand);
    }

    public void registerDependencies(PaperCommandManager commandManager) {

    }

    public void registerContexts(PaperCommandManager commandManager) {
        commandManager.getCommandContexts().registerContext(VeracityProfile.class, c -> {
            String arg = c.popFirstArg();
            Player target = Bukkit.getPlayerExact(arg);
            UUID targetUuid = null;

            if (target == null) {
                try {
                    UUIDPair pair = PlayerUtil.getExtraInfo(arg);
                    targetUuid = pair.getKey();
                } catch (Exception e) {
                }
            } else {
                targetUuid = target.getUniqueId();
            }

            if (targetUuid != null) {
                VeracityProfile profile = VeracityProfile.getByUuid(targetUuid);
                if (profile != null)
                    return profile;
            } else {
                c.getSender().sendMessage(ChatColor.RED + "Unable to find a player with the name '" + arg + "'");
                throw new InvalidCommandArgument(true);
            }
            return null;
        });
    }
}
