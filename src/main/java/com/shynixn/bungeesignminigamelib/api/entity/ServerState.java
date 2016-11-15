package com.shynixn.bungeesignminigamelib.api.entity;

import org.bukkit.ChatColor;

import static com.shynixn.bungeesignminigamelib.api.BungeeCordApi.*;

/**
 * Created by Shynixn
 */
public enum ServerState {
    RESTARTING,
    WAITING_FOR_PLAYERS,
    INGAME,
    UNKNOWN;

    public static ServerState getStateFromName(String name) {
        try {
            name = ChatColor.translateAlternateColorCodes('&', name).substring(0, name.length()-2);
            if(name.equalsIgnoreCase(MOD_INGAME))
                return INGAME;
            else if(name.equalsIgnoreCase(MOD_RESTARTING))
                return RESTARTING;
            else if(name.equalsIgnoreCase(MOD_WAITING_FOR_PLAYERS))
                return WAITING_FOR_PLAYERS;
        }
        catch (Exception ex) {
            return RESTARTING;
        }
        return UNKNOWN;
    }
}
