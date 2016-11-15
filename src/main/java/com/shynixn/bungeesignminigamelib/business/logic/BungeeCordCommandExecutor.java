package com.shynixn.bungeesignminigamelib.business.logic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.shynixn.bungeesignminigamelib.api.BungeeCordApi.*;

/**
 * Created by Shynixn
 */
class BungeeCordCommandExecutor extends BukkitCommand {
    private BungeeCordController controller;

    BungeeCordCommandExecutor(BungeeCordController controller) {
        super(COMMAND_COMMAND);
        this.controller = controller;
        this.description = COMMAND_DESCRIPTION;
        this.usageMessage = COMMAND_USEAGE;
        this.setPermission(COMMAND_PERMISSION);
        this.setPermissionMessage(COMMAND_PERMISSION_MESSAGE);
        this.setAliases(new ArrayList<String>());
        registerDynamicCommand(COMMAND_COMMAND, this);
    }

    @Override
    public final boolean execute(CommandSender sender, String alias, String[] args) {
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(getPermissionMessage());
            return true;
        }
        if (sender instanceof Player) {
            if(args.length == 1) {
                controller.lastServer.put((Player)sender, args[0]);
                sender.sendMessage(ChatColor.YELLOW + "Rightclick on a sign to convert it into a server sign.");
            }
        }
        return true;
    }

    private void registerDynamicCommand(String command, BukkitCommand clazz) {
        try {
            Class<?> subClazz = Class.forName("org.bukkit.craftbukkit.VERSION.CraftServer".replace("VERSION", BungeeCord.getServerVersion()));
            Object instance = subClazz.cast(Bukkit.getServer());
            instance = BungeeCord.invokeMethodByObject(instance, "getCommandMap");
            BungeeCord.invokeMethodByObject(instance, "register", command, clazz);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
