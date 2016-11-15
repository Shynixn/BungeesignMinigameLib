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
    private static boolean ENABLED = false;
    private static boolean SIGN_MODE = true;

    private BungeeCordController controller;

    public void reload(JavaPlugin plugin) {
        COMMAND_USEAGE = "/" + COMMAND_COMMAND + " <server>";
        COMMAND_PERMISSION = COMMAND_COMMAND + ".admin";
        buildConfigFile(plugin);
        if(isMinigameModeEnabled()) {
            Bukkit.getConsoleSender().sendMessage(PREFIX + ChatColor.YELLOW + "Enabled MINIGAME to allow joining.");
        }
        else if(isSignModeEnabled()) {
            controller = new BungeeCordController(plugin, PREFIX);
            Bukkit.getConsoleSender().sendMessage(PREFIX  + ChatColor.YELLOW + "Enabled SIGN to manage signs.");
        }
    }

    public boolean ping(String serverName) {
        return controller.ping(serverName);
    }

    public void connect(Player player, String serverName) {
        controller.connect(player, serverName);
    }

    public boolean isMinigameModeEnabled() {
        return ENABLED && !SIGN_MODE;
    }

    public boolean isSignModeEnabled() {
        return ENABLED && SIGN_MODE;
    }

    public void setModt(String motd) {
       if(isMinigameModeEnabled()) {
           try {
               motd = motd.replace("[","").replace("]","");
               Class<?> clazz = Class.forName("org.bukkit.craftbukkit.VERSION.CraftServer".replace("VERSION",getServerVersion()));
               Object obj = clazz.cast(Bukkit.getServer());
               obj = invokeMethodByObject(obj, "getServer");
               invokeMethodByObject(obj, "setMotd","[" +  motd + ChatColor.RESET + "]");
           }
           catch (Exception ex) {
               ex.printStackTrace();
           }
       }
    }

    private static void buildConfigFile(JavaPlugin plugin) {
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
                writeAllLines(file, s.toArray(new String[0]));
            }
            else {
                try {
                    FileConfiguration configuration = new YamlConfiguration();
                    configuration.load(file);
                    ENABLED = configuration.getBoolean("enabled");
                    if(configuration.getString("connection").equalsIgnoreCase("MINIGAME"))
                        SIGN_MODE = false;
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

    private static boolean writeAllLines(File file, String... text) {
        try {
            if(file.exists()) {
                if(!file.delete())
                    throw new IllegalStateException("Cannot delete previous file!");
            }
            if(!file.createNewFile())
                throw new IllegalStateException("Cannot create file!");
            try(BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"))) {
                for(int i = 0; i< text.length; i++) {
                    bufferedWriter.write(text[i] + "\n");
                }
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static Object invokeMethodByObject(Object object, String name, Object... params) {
        Class<?> clazz = object.getClass();
        do {
            for(Method method : clazz.getDeclaredMethods()) {
                try {
                    if(method.getName().equalsIgnoreCase(name)) {
                        method.setAccessible(true);
                        return method.invoke(object, params);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }while (clazz != null);
        throw new RuntimeException("Cannot find correct method.");
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
