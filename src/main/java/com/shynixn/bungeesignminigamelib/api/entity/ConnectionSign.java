package com.shynixn.bungeesignminigamelib.api.entity;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Created by Shynixn
 */
public interface ConnectionSign extends ConfigurationSerializable {
    String getServer();

    Location getLocation();
}
