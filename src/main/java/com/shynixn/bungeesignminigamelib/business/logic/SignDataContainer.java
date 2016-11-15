package com.shynixn.bungeesignminigamelib.business.logic;

import com.shynixn.bungeesignminigamelib.api.entity.ConnectionSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shynixn
 */
class SignDataContainer implements ConnectionSign
{
    private String world;
    private double x;
    private double y;
    private double z;
    private String server;

    SignDataContainer(Map<String, Object> data) {
        server = (String) data.get("server");
        x = (double) data.get("x");
        y = (double) data.get("y");
        z = (double) data.get("z");
        world = (String) data.get("world");
    }

    SignDataContainer(Location location, String server) {
        this.server = server;
        world = location.getWorld().getName();
        x = location.getX();
        y = location.getY();
        z = location.getZ();
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("server", getServer());
        data.put("world", world);
        data.put("x", x);
        data.put("y", y);
        data.put("z", z);
        return data;
    }
}
