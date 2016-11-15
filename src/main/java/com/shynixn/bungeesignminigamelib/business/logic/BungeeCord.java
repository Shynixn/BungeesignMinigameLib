package com.shynixn.bungeesignminigamelib.business.logic;

import com.shynixn.bungeesignminigamelib.api.BungeeCordApi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import static com.shynixn.bungeesignminigamelib.api.BungeeCordApi.*;

/**
 * Created by Shynixn
 */
public final class BungeeCord
{
    private BungeeCordController controller;
    private BungeeCordApi apiInstance;
    private boolean isEnabled = false;

    public BungeeCord(BungeeCordApi apiInstance) {
        this.apiInstance = apiInstance;
    }

    public void enable(JavaPlugin plugin) {
        if(!isEnabled)
            return;
        isEnabled = true;
        apiInstance.COMMAND_USEAGE = "/" + apiInstance.COMMAND_COMMAND + " <server>";
        apiInstance.COMMAND_PERMISSION = apiInstance.COMMAND_COMMAND + ".admin";
        buildConfigFile(plugin);
        if(isMinigameModeEnabled()) {
            Bukkit.getConsoleSender().sendMessage(apiInstance.PREFIX + ChatColor.YELLOW + "Enabled MINIGAME to allow joining.");
        }
        else if(isSignModeEnabled()) {
            controller = new BungeeCordController(plugin, apiInstance);
            Bukkit.getConsoleSender().sendMessage(apiInstance.PREFIX  + ChatColor.YELLOW + "Enabled SIGN to manage signs.");
        }
    }
    public boolean isEnabled()
    {
        return isEnabled;
    }

    public void disable()
    {
        if(isEnabled)
        {
            controller.dispose();
            controller = null;
            isEnabled = false;
        }
    }

    public boolean pingAll() {
        return controller.pingAll();
    }

    public boolean ping(String serverName) {
        return controller.ping(serverName);
    }

    public void connect(Player player, String serverName) {
        controller.connect(player, serverName);
    }

    public boolean isMinigameModeEnabled() {
        return apiInstance.ENABLED && ! apiInstance.SIGN_MODE;
    }

    public boolean isSignModeEnabled() {
        return  apiInstance.ENABLED &&  apiInstance.SIGN_MODE;
    }

    public void setModt(String motd) {
       if(isMinigameModeEnabled()) {
           try {
               motd = motd.replace("[","").replace("]","");
               Class<?> clazz = Class.forName("org.bukkit.craftbukkit.VERSION.CraftServer".replace("VERSION",getServerVersion()));
               Object obj = clazz.cast(Bukkit.getServer());
               obj = BungeeCordHelper.invokeMethodByObject(obj, "getServer");
               BungeeCordHelper. invokeMethodByObject(obj, "setMotd","[" +  motd + ChatColor.RESET + "]");
           }
           catch (Exception ex) {
               ex.printStackTrace();
           }
       }
    }

    private void buildConfigFile(JavaPlugin plugin) {
        try {
            File file = new File(plugin.getDataFolder(),"bungeecord.yml");
            if(!file.exists()) {
                if(!file.createNewFile())
                    throw new IllegalStateException("Cannot build config file!");
                ArrayList<String> s = new ArrayList<>();
                s.add("#BungeeCord");
                s.add("enabled: false");
                s.add("");
                s.add("#Select the connection type: MINIGAME or SIGN");
                s.add("connection: MINIGAME");
                s.add("");
                for(Field field : BungeeCordApi.class.getDeclaredFields()) {
                    if(field.getType() == String.class && !field.getName().equalsIgnoreCase("PREFIX")) {
                        s.add(field.getName().toLowerCase() + ": \"" + String.valueOf(field.get(null)).replace('§', '&') + "\"");
                    }
                }
                BungeeCordHelper.writeAllLines(file, s.toArray(new String[0]));
            }
            else {
                try {
                    FileConfiguration configuration = new YamlConfiguration();
                    configuration.load(file);
                    apiInstance.ENABLED = configuration.getBoolean("enabled");
                    if(configuration.getString("connection").equalsIgnoreCase("MINIGAME"))
                        apiInstance.SIGN_MODE = false;
                    Map<String, Object> map = configuration.getConfigurationSection("").getValues(true);
                    for(Field field :  BungeeCord.class.getDeclaredFields()) {
                        for(String key : map.keySet()) {
                            if(field.getName().equalsIgnoreCase(key)) {
                                if(field.getType() == String.class) {
                                    field.set(null, ChatColor.translateAlternateColorCodes('&', (String) map.get(key)));
                                }
                                else {
                                    field.set(null, map.get(key));
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getServerVersion() {
        try {
            return Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
        }
        catch (Exception ex) {
            throw new RuntimeException("Version not found!");
        }
    }
}
