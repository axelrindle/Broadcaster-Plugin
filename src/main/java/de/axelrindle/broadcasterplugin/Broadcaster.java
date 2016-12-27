package de.axelrindle.broadcasterplugin;

import com.google.common.io.Files;
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

    private static final String consolePrefix = "[Broadcaster] ";
    public static final String prefix = "&2Broadcaster &f> ";
	public final Logger log = Logger.getLogger("Broadcaster");

	private FileConfiguration config;

	public List<String> messages;
	
	@Override
	public void onEnable() {
		log.info(consolePrefix + "Loading...");

		log.info(consolePrefix + "Loading config...");
        try {
            loadConfig();
        } catch (IOException e) {
            log.severe("Failed to load config! Please check your configuration!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        log.info(consolePrefix + "Config loaded.");

        log.info(consolePrefix + "Loading messages...");
        try {
            loadMessages();
        } catch (IOException e) {
            log.severe(consolePrefix + "Failed to load messages! Please check your configuration!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        log.info(consolePrefix + "Messages loaded.");

        if(getConfig().getBoolean("Cast.OnServerStart")) {
			startBroadcast();
		}

        getCommand("brc").setExecutor(new BrcCommand(this));
		
		log.info(consolePrefix + "Done! Version " + getDescription().getVersion());
	}
	
	@Override
	public void onDisable() {
		stopBroadcast();
		
		log.info(consolePrefix + "Successfully disabled!");
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
        log.info(consolePrefix + "Reloading config and messages...");
        try {
            loadConfig();
            loadMessages();
        } catch (IOException e) {
            log.severe(consolePrefix + "Failed to reload config! Please check your configuration!");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        log.info(consolePrefix + "Config and messages reloaded.");
    }

    @Override
	public FileConfiguration getConfig() {
		return config;
	}

	private void loadConfig() throws IOException {
	    File file = new File("plugins/Broadcaster/config.yml");
	    if(!file.exists()) {
            Files.createParentDirs(file);
            file.createNewFile();
            IOUtils.copy(getResource("config.yml"), new FileOutputStream(file));
        }

	    config = YamlConfiguration.loadConfiguration(file);
  	}
  	
  	private void loadMessages() throws IOException {
        File file = new File("plugins/Broadcaster/messages.yml");
        if(!file.exists()) {
            Files.createParentDirs(file);
            file.createNewFile();
            IOUtils.copy(getResource("messages.yml"), new FileOutputStream(file));
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        messages = config.getStringList("Messages");
    }
}
