package com.shynixn.bungeesignminigamelib.api.entity;

/**
 * Created by Shynixn
 */
public interface ServerInfo {
    int getPlayerAmount();

    int getMaxPlayerAmount();

    ServerState getState();

    String getServerName();
}
