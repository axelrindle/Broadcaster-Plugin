package de.axelrindle.broadcasterplugin;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Main class of the Broadcaster plugin.
 */
@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
public final class Broadcaster extends JavaPlugin {

    public static final String prefix = "&2Broadcaster &f> ";
	public final Logger log = Logger.getLogger("Broadcaster");

	private FileConfiguration config;

	public List<String> messages;
	
	@Override
	public void onEnable() {
		log.info("Loading...");

		log.info("Loading config...");
        try {
            loadConfig();
        } catch (IOException e) {
            log.severe("Failed to load config! Please check your configuration!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        log.info("Config loaded.");

        log.info("Loading messages...");
        try {
            loadMessages();
        } catch (IOException e) {
            log.severe("Failed to load messages! Please check your configuration!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        log.info("Messages loaded.");

        if(getConfig().getBoolean("Cast.OnServerStart")) {
			startBroadcast();
		}

        getCommand("brc").setExecutor(new BrcCommand(this));
		
		log.info("Done! Version " + getDescription().getVersion());
	}
	
	@Override
	public void onDisable() {
		stopBroadcast();
		
		log.info("Successfully disabled!");
	}
	
	private void startBroadcast() {
		int interval = getConfig().getInt("Cast.Interval");
		BroadcastThread.start(this, messages, interval);
	}
	
	private void stopBroadcast() {
	    BroadcastThread.stop();
	}

    @Override
    public void reloadConfig() {
        log.info("Reloading config...");
        try {
            loadConfig();
        } catch (IOException e) {
            log.severe("Failed to reload config! Please check your configuration!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        log.info("Config reloaded.");
    }

    @Override
	public FileConfiguration getConfig() {
		return config;
	}

	private void loadConfig() throws IOException {
	    File file = new File("plugins/Broadcaster/config.yml");
	    if(!file.exists()) {
            IOUtils.copy(getResource("config.yml"), new FileOutputStream(file));
        }

	    config = YamlConfiguration.loadConfiguration(file);
  	}
  	
  	private void loadMessages() throws IOException {
        File file = new File("plugins/Broadcaster/messages.yml");
        if(!file.exists()) {
            IOUtils.copy(getResource("messages.yml"), new FileOutputStream(file));
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        messages = config.getStringList("Messages");
    }
}
