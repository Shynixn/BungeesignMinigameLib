package com.shynixn.bungeesignminigamelib.api;

import com.shynixn.bungeesignminigamelib.business.logic.BungeeCord;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Shynixn
 */
public class BungeeCordApi {
    public String MOD_RESTARTING = ChatColor.RED + "Restarting";
    public String MOD_WAITING_FOR_PLAYERS = ChatColor.GREEN + "Waiting for players...";
    public String MOD_INGAME = ChatColor.BLUE + "Ingame";

    public String SIGN_RESTARTING = ChatColor.RED + "" + ChatColor.BOLD + "Restarting";
    public String SIGN_WAITING_FOR_PLAYERS = ChatColor.GREEN + "" + ChatColor.BOLD + "Join";
    public String SIGN_INGAME = ChatColor.BLUE + "" + ChatColor.BOLD+ "Ingame";

    public String COMMAND_COMMAND = null;
    public String COMMAND_USEAGE = "/e <server>";
    public String COMMAND_PERMISSION_MESSAGE = "You don't have permission.";
    public String COMMAND_PERMISSION = ".admin";
    public String COMMAND_DESCRIPTION = "Configures bungee signs.";

    public String SIGN_LINE_1 = null;
    public String SIGN_LINE_2 = "<server>";
    public String SIGN_LINE_3 = "<state>";
    public String SIGN_LINE_4 = "<players>/<maxplayers>";

    public String PREFIX = null;
    public boolean ENABLED = false;
    public boolean SIGN_MODE = true;

    private BungeeCord bungeeCord;

    public static BungeeCordApi createApiInstance()
    {
        BungeeCordApi bungeeCordApi = new BungeeCordApi();
        bungeeCordApi.bungeeCord = new BungeeCord(bungeeCordApi);
        return bungeeCordApi;
    }

    public void enable(JavaPlugin plugin) {
        if(plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        if(COMMAND_COMMAND == null)
            throw new IllegalStateException("Command cannot be null!. Set the COMMAND_COMMAND field!");
        if(SIGN_LINE_1  == null)
            throw new IllegalStateException("Sign line 1 cannot be null!. Set the SIGN_LINE_1 field!");
        if(PREFIX  == null)
            throw new IllegalStateException("Prefix cannot be null!. Set the PREFIX field!");
        bungeeCord.enable(plugin);
    }

    public void disable() {
        bungeeCord.disable();
    }

    public boolean requestServerInformation(String serverName) {
        if(serverName == null)
            throw new IllegalArgumentException("ServerName cannot be null!");
        return bungeeCord.ping(serverName);
    }

    public boolean requestServerInformation() {
        return bungeeCord.pingAll();
    }

    public void connect(Player player, String serverName) {
        if(player == null)
            throw new IllegalArgumentException("Player cannot be null!");
        if(serverName == null)
            throw new IllegalArgumentException("ServerName cannot be null!");
        bungeeCord.connect(player, serverName);
    }

    public void setServerMotd(String motd) {
        if(motd == null)
            throw new IllegalArgumentException("Motd cannot be null!");
        bungeeCord.setModt(motd);
    }

    public String getServerMotd() {
        return Bukkit.getServer().getMotd();
    }

    public boolean isMinigameModeEnabled() {
        return bungeeCord.isMinigameModeEnabled();
    }

    public boolean isSignConnectingEnabled() {
        return bungeeCord.isSignModeEnabled();
    }

    public String getServerVersion() {
        return bungeeCord.getServerVersion();
    }
}
