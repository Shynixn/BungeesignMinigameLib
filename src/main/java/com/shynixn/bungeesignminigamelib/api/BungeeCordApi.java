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
    public static String MOD_RESTARTING = ChatColor.RED + "Restarting";
    public static String MOD_WAITING_FOR_PLAYERS = ChatColor.GREEN + "Waiting for players...";
    public static String MOD_INGAME = ChatColor.BLUE + "Ingame";

    public static String SIGN_RESTARTING = ChatColor.RED + "" + ChatColor.BOLD + "Restarting";
    public static String SIGN_WAITING_FOR_PLAYERS = ChatColor.GREEN + "" + ChatColor.BOLD + "Join";
    public static String SIGN_INGAME = ChatColor.BLUE + "" + ChatColor.BOLD+ "Ingame";

    public static String COMMAND_COMMAND = null;
    public static String COMMAND_USEAGE = "/e <server>";
    public static String COMMAND_PERMISSION_MESSAGE = "You don't have permission.";
    public static String COMMAND_PERMISSION = ".admin";
    public static String COMMAND_DESCRIPTION = "Configures bungee signs.";

    public static String SIGN_LINE_1 = null;
    public static String SIGN_LINE_2 = "<server>";
    public static String SIGN_LINE_3 = "<state>";
    public static String SIGN_LINE_4 = "<players>/<maxplayers>";

    public static String PREFIX = null;

    private static BungeeCord bungeeCord;

    private static BungeeCord getBungeeCord() {
        if(bungeeCord == null)
            bungeeCord = new BungeeCord();
        return bungeeCord;
    }

    public static void enable(JavaPlugin plugin) {
        if(plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        if(COMMAND_COMMAND == null)
            throw new IllegalStateException("Command cannot be null!. Set the COMMAND_COMMAND field!");
        if(SIGN_LINE_1  == null)
            throw new IllegalStateException("Sign line 1 cannot be null!. Set the SIGN_LINE_1 field!");
        if(PREFIX  == null)
            throw new IllegalStateException("Prefix cannot be null!. Set the PREFIX field!");
        getBungeeCord().reload(plugin);
    }

    public static boolean requestServerInformation(String serverName) {
        if(serverName == null)
            throw new IllegalArgumentException("ServerName cannot be null!");
        return getBungeeCord().ping(serverName);
    }

    public static void connect(Player player, String serverName) {
        if(player == null)
            throw new IllegalArgumentException("Player cannot be null!");
        if(serverName == null)
            throw new IllegalArgumentException("ServerName cannot be null!");
        getBungeeCord().connect(player, serverName);
    }

    public static void setServerMotd(String motd) {
        if(motd == null)
            throw new IllegalArgumentException("Motd cannot be null!");
        getBungeeCord().setModt(motd);
    }

    public static String getServerMotd() {
        return Bukkit.getServer().getMotd();
    }

    public static boolean isMinigameModeEnabled() {
        return getBungeeCord().isMinigameModeEnabled();
    }

    public static boolean isSignConnectingEnabled() {
        return getBungeeCord().isSignModeEnabled();
    }

    public static String getServerVersion() {
        return getBungeeCord().getServerVersion();
    }
}
