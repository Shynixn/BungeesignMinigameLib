package com.shynixn.bungeesignminigamelib.business.logic;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.shynixn.bungeesignminigamelib.api.entity.ServerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by Shynixn
 */
class BungeeCordProvider implements PluginMessageListener {
    private BungeeCordController controller;
    private JavaPlugin plugin;
    private CallBack callBack;

    BungeeCordProvider(BungeeCordController controller, JavaPlugin plugin, CallBack callBack) {
        this.controller = controller;
        this.plugin = plugin;
        this.callBack = callBack;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    boolean ping(String serverName) {
        Player player = getFirstPlayer();
        if(player == null)
            return false;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ServerIP");
        out.writeUTF(serverName);
        player.sendPluginMessage(plugin,"BungeeCord", out.toByteArray());
        return true;
    }

    boolean pingAll()
    {
        Player player = getFirstPlayer();
        if(player == null)
            return false;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        player.sendPluginMessage(plugin,"BungeeCord", out.toByteArray());
        return true;
    }

    boolean pingSigns() {
        Player player = getFirstPlayer();
        if(player == null)
            return false;
        for(String s : controller.getServers()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ServerIP");
            out.writeUTF(s);
            player.sendPluginMessage(plugin,"BungeeCord", out.toByteArray());
        }
        return true;
    }

    void connectToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String type = in.readUTF();
        if(type.equals("ServerIP")) {
            final String serverName = in.readUTF();
            final String ip = in.readUTF();
            final short port = in.readShort();
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    String data = receiveResultFromServer(serverName,ip, port);
                    parseData(serverName,data);
                }
            });
        }
        else if(type.equals("GetServers"))
        {
            for(String serverName : in.readUTF().split(", "))
            {
                ping(serverName);
            }
        }
    }

    private void parseData(String serverName,String data) {
        try {
            if(data == null)
                return;
            final ServerInfo serverInfo = new ServerDataContainer(serverName,data);
            plugin.getServer().getScheduler().runTask(plugin, new Runnable()
            {
                @Override
                public void run()
                {
                    callBack.run(serverInfo);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    interface CallBack {
        void run(ServerInfo serverInfo);
    }

    private Player getFirstPlayer() {
        for(World world : Bukkit.getWorlds()) {
            if(world.getPlayers().size() > 0)
                return world.getPlayers().get(0);
        }
        return null;
    }

    private String receiveResultFromServer(String serverName, String hostname, int port) {
        String data = null;
        try(Socket socket = new Socket(hostname, port)) {
            try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                out.write(0xFE);
                try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
                    StringBuilder buffer = new StringBuilder();
                    int b;
                    while ((b = in.read()) != -1) {
                        if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) {
                            buffer.append((char) b);
                        }
                    }
                    data = buffer.toString();
                }
            }
        }
        catch (Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage(controller.PREFIX + ChatColor.RED + "Failed to reach server "  +  serverName + " (" + hostname + ":" + port + ").");
        }
        return data;
    }

    public void dispose()
    {
        if (plugin != null)
        {
            plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, "BungeeCord", this);
            controller = null;
            plugin = null;
            callBack = null;
        }
    }
}
