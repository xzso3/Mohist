package red.mohist.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import red.mohist.Metrics;
import red.mohist.api.ServerAPI;

public abstract class ConfigBase
{
    protected final File configFile;
    protected static String commandName;
    private static boolean metricsStarted;
    
    /* ======================================================================== */

    public YamlConfiguration config;
    protected int version;
    protected static Map<String, Command> commands;
    protected Map<String, Setting> settings = new HashMap<>();

    /* ======================================================================== */

    public ConfigBase(String fileName, String commandName)
    {
        this.configFile = new File("mohist-config", fileName);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.commandName = commandName;
        this.commands = new HashMap<>();
        this.addCommands();
    }

    protected abstract void addCommands();

    public Map<String, Setting> getSettings()
    {
        return settings;
    }

    public static void registerCommands()
    {
        for (Map.Entry<String, Command> entry : commands.entrySet())
        {
            ServerAPI.getNMSServer().server.getCommandMap().register(entry.getKey(), commandName, entry.getValue());
        }
        if (!metricsStarted) {
            Metrics.MohistMetrics.startMetrics();
            metricsStarted = true;
        }
    }

    public void save()
    {
        try
        {
            config.save(configFile);
        }
        catch (IOException ex)
        {
            ServerAPI.getNMSServer().logSevere("Could not save " + configFile);
            ex.printStackTrace();
        }
    }

    protected abstract void load();
 
    public void set(String path, Object val)
    {
        config.set(path, val);
    }

    public boolean isSet(String path)
    {
        return config.isSet(path);
    }

    public boolean isInt(String path)
    {
        return config.isInt(path);
    }

    public boolean isBoolean(String path)
    {
        return config.isBoolean(path);
    }

    public boolean getBoolean(String path)
    {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def)
    {
        return getBoolean(path, def, true);
    }

    public boolean getBoolean(String path, boolean def, boolean useDefault)
    {
        if (useDefault)
        {
        config.addDefault(path, def);
        }
        return config.getBoolean(path, def);
    }

    public int getInt(String path)
    {
        return config.getInt(path);
    }

    public int getInt(String path, int def)
    {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    public <T> List getList(String path, T def)
    {
        config.addDefault(path, def);
        return config.getList(path, config.getList(path));
    }

    public String getString(String path, String def)
    {
        return getString(path, def, true);
    }

    public String getString(String path, String def, boolean useDefault)
    {
        if (useDefault)
        {
        config.addDefault(path, def);
        }
        return config.getString(path, def);
    }

    public List<String> getStringList(String path, List<String> def)
    {
        config.addDefault(path, def);
        return config.getStringList(path);
    }

    public YamlConfiguration getConfig()
    {
        return config;
    }

    public String getFakePlayer(String className, String defaultName)
    {
        return getString("fake-players." + className + ".username", defaultName);
    }

    private static String transform(String s) {
        return ChatColor.translateAlternateColorCodes('&', s).replaceAll("\\\\n", "\n");
    }
}
