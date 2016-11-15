package com.shynixn.bungeesignminigamelib.api.event;

import com.shynixn.bungeesignminigamelib.api.entity.ServerInfo;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Shynixn
 */
public class ServerInfoUpdateEvent extends Event {
    private final static HandlerList handlers = new HandlerList();

    private ServerInfo serverInfo;

    public ServerInfoUpdateEvent(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
