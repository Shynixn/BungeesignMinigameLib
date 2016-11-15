package com.shynixn.bungeesignminigamelib.business.logic;

import com.shynixn.bungeesignminigamelib.api.entity.ServerInfo;
import com.shynixn.bungeesignminigamelib.api.entity.ServerState;

/**
 * Created by Shynixn
 */
class ServerDataContainer implements ServerInfo
{
    private int playerAmount = 0;
    private int maxPlayerAmount = 0;
    private ServerState state;
    private String serverName;

    ServerDataContainer(String serverName, int playerAmount, int maxPlayerAmount) {
        this.playerAmount = playerAmount;
        this.maxPlayerAmount = maxPlayerAmount;
        this.state = ServerState.UNKNOWN;
        this.serverName = serverName;
    }

    ServerDataContainer(String serverName, String data) throws Exception {
        this.serverName = serverName;
        try
        {
            String motd = "";
            boolean started = false;
            int i;
            for(i = 0; i < data.length(); i++) {
                if(data.charAt(i) == '[')
                    started = true;
                else if(data.charAt(i) == ']')
                    break;
                else if(started)
                    motd += data.charAt(i);
            }
            state = ServerState.getStateFromName(motd);
            motd =  "";
            started = false;
            for(; i < data.length(); i++) {
                if(data.charAt(i) == '§') {
                    if(started) {
                        break;
                    }
                    else {
                        started = true;
                    }
                }
                else if(started) {
                    motd += data.charAt(i);
                }
            }
            playerAmount = Integer.parseInt(motd);
            motd =  "";
            started = false;
            for(; i < data.length(); i++) {
                if(data.charAt(i) == '§')
                    started = true;
                else if(data.charAt(i) == '§')
                    break;
                else if(started)
                    motd += data.charAt(i);
            }
            maxPlayerAmount = Integer.parseInt(motd);
        }
        catch (Exception ex) {
            state = ServerState.RESTARTING;
            playerAmount = 0;
            maxPlayerAmount = 0;
        }
    }

    @Override
    public int getPlayerAmount() {
        return playerAmount;
    }

    @Override
    public int getMaxPlayerAmount() {
        return maxPlayerAmount;
    }

    @Override
    public ServerState getState() {
        return state;
    }

    @Override
    public String getServerName() {
        return serverName;
    }
}
