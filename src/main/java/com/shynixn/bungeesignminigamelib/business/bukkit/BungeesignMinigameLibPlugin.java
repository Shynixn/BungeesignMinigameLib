package com.shynixn.bungeesignminigamelib.business.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Shynixn
 */
public final class BungeesignMinigameLibPlugin extends JavaPlugin {
    public static final String PREFIX_CONSOLE = ChatColor.YELLOW + "[BSMLib] ";

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Loading BungeeSignMinigameLib...");
        Bukkit.getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BungeeSignMinigameLib " + getDescription().getVersion() + " by Shynixn");
    }
}
