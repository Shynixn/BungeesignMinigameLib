package com.shynixn.bungeesignminigamelib.business.logic;

import com.shynixn.bungeesignminigamelib.api.entity.ConnectionSign;
import com.shynixn.bungeesignminigamelib.api.entity.ServerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Shynixn
 */
class BungeeCordListener implements Listener
{
    private BungeeCordController controller;

    BungeeCordListener(BungeeCordController controller, JavaPlugin plugin) {
        this.controller = controller;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getClickedBlock().getState() instanceof Sign) {
                if(controller.lastServer.containsKey(event.getPlayer())) {
                    String server = controller.lastServer.get(event.getPlayer());
                    controller.lastServer.remove(event.getPlayer());
                    Sign sign = (Sign) event.getClickedBlock().getState();
                    controller.add(server, sign.getLocation());
                    controller.updateSign(sign, new ServerDataContainer(server, 0,0));
                }
                else {
                    Sign sign = (Sign) event.getClickedBlock().getState();
                    try {
                        ConnectionSign signInfo;
                        if((signInfo = getBungeeCordSignInfo(sign.getLocation())) != null) {
                            controller.connect(event.getPlayer(), signInfo.getServer());
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private ConnectionSign getBungeeCordSignInfo(Location location2) {
        for(ConnectionSign info : controller.signs.toArray(new ConnectionSign[0])) {
            Location location1 = info.getLocation();
            if(location1.getBlockX() == location2.getBlockX()) {
                if(location1.getBlockY() == location2.getBlockY()) {
                    if(location1.getBlockZ() == location2.getBlockZ()) {
                        return info;
                    }
                }
            }
        }
        return null;
    }
}
