package com.shynixn.bungeesignminigamelib.business.logic;

import com.shynixn.bungeesignminigamelib.api.entity.ConnectionSign;
import com.shynixn.bungeesignminigamelib.api.entity.ServerInfo;
import com.shynixn.bungeesignminigamelib.api.entity.ServerState;
import com.shynixn.bungeesignminigamelib.api.event.ServerInfoUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.shynixn.bungeesignminigamelib.api.BungeeCordApi.*;

/**
 * Created by Shynixn
 */
class BungeeCordController implements BungeeCordProvider.CallBack {
    final String PREFIX;
    private JavaPlugin plugin;
    private BungeeCordProvider provider;

    Map<Player, String> lastServer = new HashMap<>();
    List<ConnectionSign> signs = new ArrayList<>();

    BungeeCordController(JavaPlugin plugin, String prefix) {
        PREFIX = prefix;
        this.plugin = plugin;
        provider = new BungeeCordProvider(this, plugin, this);
        new BungeeCordListener(this, plugin);
        new BungeeCordCommandExecutor(this);
        load(plugin);
        run(plugin);
    }

    boolean ping(String serverName) {
       return provider.ping(serverName);
    }

    void connect(Player player, String serverName) {
        provider.connectToServer(player, serverName);
    }

    private void run(JavaPlugin plugin) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                provider.ping();
            }
        }, 0L, 10L);
    }

    void add(String server, Location location) {
        ConnectionSign info = new SignDataContainer(location, server);
        signs.add(info);
        final ConnectionSign[] signDataContainer = signs.toArray(new ConnectionSign[0]);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run()
            {
                try {
                    FileConfiguration configuration = new YamlConfiguration();
                    File file = new File(plugin.getDataFolder(), "bungeecord_signs.yml");
                    if(file.exists()) {
                        if(!file.delete())
                            throw new IllegalStateException("Cannot delete previous file!");
                    }
                    for(int i = 0; i < signDataContainer.length; i++) {
                        configuration.set("signs." + i, signDataContainer[i].serialize());
                    }
                    configuration.save(file);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void run(ServerInfo serverInfo) {
        Bukkit.getPluginManager().callEvent(new ServerInfoUpdateEvent(serverInfo));
        for(ConnectionSign signInfo : signs.toArray(new ConnectionSign[0])) {
            if(signInfo.getServer().equals(serverInfo.getServerName())) {
                Location location = signInfo.getLocation();
                if(location.getBlock().getState() instanceof Sign) {
                    updateSign((Sign) location.getBlock().getState(), serverInfo);
                }
                else {
                    signs.remove(signInfo);
                }
            }
        }
    }

    Set<String> getServers() {
        Set<String> server = new HashSet<>();
        for(ConnectionSign signInfo : signs) {
            server.add(signInfo.getServer());
        }
        return server;
    }

    void updateSign(Sign sign, ServerInfo info) {
        sign.setLine(0, replaceSign(SIGN_LINE_1, info));
        sign.setLine(1, replaceSign(SIGN_LINE_2, info));
        sign.setLine(2, replaceSign(SIGN_LINE_3, info));
        sign.setLine(3, replaceSign(SIGN_LINE_4, info));
        sign.update();
    }

    private void load(JavaPlugin plugin) {
        try {
            FileConfiguration configuration = new YamlConfiguration();
            File file = new File(plugin.getDataFolder(), "bungeecord_signs.yml");
            if(!file.exists()) {
                if(!file.createNewFile())
                    throw new IllegalStateException("Cannot create file!");
            }
            configuration.load(file);
            if(configuration.getConfigurationSection("signs") != null) {
                Map<String, Object> data = configuration.getConfigurationSection("signs").getValues(false);
                for(String s : data.keySet()) {
                    signs.add(new SignDataContainer(((ConfigurationSection)data.get(s)).getValues(true)));
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private String replaceSign(String line, ServerInfo info) {
        line = line.replace("<maxplayers>", String.valueOf(info.getMaxPlayerAmount()));
        line = line.replace("<players>", String.valueOf(info.getPlayerAmount()));
        line = line.replace("<server>", info.getServerName());
        if(info.getState() == ServerState.INGAME)
            line = line.replace("<state>", SIGN_INGAME);
        else if(info.getState() == ServerState.RESTARTING)
            line = line.replace("<state>", SIGN_RESTARTING);
        else if(info.getState() == ServerState.WAITING_FOR_PLAYERS)
            line = line.replace("<state>", SIGN_WAITING_FOR_PLAYERS);
        else if(info.getState() == ServerState.UNKNOWN)
            line = line.replace("<state>", "UNKNOWN");
        return line;
    }
}
