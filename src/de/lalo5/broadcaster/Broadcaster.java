package de.lalo5.broadcaster;

import de.lalo5.broadcaster.UpdateChecker.ReleaseType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings({"WeakerAccess", "ResultOfMethodCallIgnored"})
@Deprecated
/**
 * Main class of the Broadcaster plugin.
 *
 * @deprecated This plugin is no longer maintained because of other important projects.
 */
public class Broadcaster extends JavaPlugin {
	
	public static final String prefix = "§f[§3Broadcaster§f] ";
	
	public final Logger log = Logger.getLogger("Broadcaster");

	public List<String> messages;

	public int currentIndex = 0;
	public boolean running = true;
	public int id;
	
	public static boolean update = false;
	public static String updateName = "";
	public static ReleaseType updateType = null;
	public static String updateVersion = "";
	public static String updateLink = "";
	
	private PluginDescriptionFile descFile = getDescription();
	
	@Override
	public void onEnable() {
		
		log.info(prefix + "Loading...");
		
		loadConfig();
		loadMessageFile();

		messages = new ArrayList<>();

		if(getConfig().getBoolean("Cast.OnServerStart")) {
			startBroadcast();
		}

		UpdateChecker updater = new UpdateChecker(this, 49029, getFile(), UpdateChecker.UpdateType.DEFAULT, true);
		update = updater.getResult() == UpdateChecker.UpdateResult.UPDATE_AVAILABLE; // Determine if there is an update ready for us
		updateName = updater.getLatestName(); // Get the latest name
		updateVersion = updater.getLatestGameVersion(); // Get the latest game version
		updateType = updater.getLatestType(); // Get the latest file's type
		updateLink = updater.getLatestFileLink(); // Get the latest link
		
		log.info(prefix + "Successfully enabled.");
		log.info(prefix + "Version " + descFile.getVersion());
	}
	
	@Override
	public void onDisable() {
		
		stopBroadcast();
		saveConfig();
		
		log.info(prefix + "Successfully disabled!");
	}
	
	
	//Main Tasks
	@SuppressWarnings({ "resource" })
	private void broadcast() throws IOException {
		
		FileInputStream file = new FileInputStream("plugins/Broadcaster/messages.txt");
	    BufferedReader reader = new BufferedReader(new InputStreamReader(file));

		if(messages.isEmpty()) {
			for (Object o : reader.lines().toArray()) {
				String s = (String) o;
				messages.add(s);
			}
		}

		String line = messages.get(currentIndex);

		line = line.replace("&", "§");
		line = line.replace("<3", "♥");
		line = line.replace("[*]", "★");
		line = line.replace("[**]", "✹");
		line = line.replace("[p]", "?");
		line = line.replace("[v]", "✔");
		line = line.replace("[+]", "♦");
		line = line.replace("[++]", "✦");
		line = line.replace("%n", "\n");
		line = line.replace("[x]", "█");
		line = line.replace("[/]", "▌");
		line = line.replace("%online_players%", Integer.valueOf(Bukkit.getOnlinePlayers().size()).toString());
		line = line.replace("%max_players%", Integer.valueOf(Bukkit.getMaxPlayers()).toString());

		String castprefix = getConfig().getString("Cast.Prefix");
		Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', castprefix) + line);

		if(currentIndex < (messages.size() - 1)) {
			currentIndex += 1;
		} else if(currentIndex == (messages.size() - 1)) {
			currentIndex = 0;
		}

		this.running = true;
	}
	
	private void startBroadcast() {
		
		long interval = getConfig().getLong("Cast.Interval");
		
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try	{
                broadcast();
            } catch (IOException e) {
                log.warning("Broadcaster could not broadcast message.");
                log.warning("Stopping Broadcaster...");
                Bukkit.getScheduler().cancelTask(id);
                running = false;
            }
        }, 400L, interval * 20L);
	}
	
	private void stopBroadcast() {
		Bukkit.getScheduler().cancelTask(id);
		this.running = false;
	}
	
	
	//Commands
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		String perm = getConfig().getString("Messages.NoPermission");
		
		//Console
		if (!(sender instanceof Player)) {
			if(cmd.getName().equalsIgnoreCase("broadcaster") || cmd.getName().equalsIgnoreCase("brc")) {
				if(sender.hasPermission("broadcaster.main")) {
					if(args.length == 0) {
						
						sender.sendMessage(prefix + "§3Commands");
						sender.sendMessage("");
						sendhelp(sender);
						
						return true;
					} else if(args[0].equalsIgnoreCase("start")) {
						if(sender.hasPermission("broadcaster.start")) {
							
							if (this.running)
				            {
								String ar = getConfig().getString("Messages.ALREADY_RUNNING");
								sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', ar));
								
								return true;
				            } else {
				            	String ttsb = getConfig().getString("Messages.TRYING_TO_START_BROADCAST");
				            	sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', ttsb));
				            				            	
				            	startBroadcast();
				                  
				                String sst = getConfig().getString("Messages.SUCCESSFULLY_STARTED");
				                sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', sst));
				            }
							
				            						
							return true;
						} else {
							sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', perm));
						}
					} else if(args[0].equalsIgnoreCase("stop")) {
						if(sender.hasPermission("broadcaster.stop")) {
							
							if(this.running) {
								
								Bukkit.getScheduler().cancelTask(id);
								
								String ss = getConfig().getString("Messages.SUCCESSFULLY_STOPPED").replace("&", "§"); 
								sender.sendMessage(prefix + ss);
					            
					            this.running = false;
								
							} else {
								
								String as = getConfig().getString("Messages.ALREADY_STOPPED");
								sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', as));
					            
							}
							
							return true;
						} else {
							sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', perm));
						}
					} else if (args[0].equalsIgnoreCase("reload")) {
						if (sender.hasPermission("broadcaster.reload")) {
															            	
							stopBroadcast();
			                
							String bws = getConfig().getString("Messages.BROADCAST_STOPPED_BECAUSE_RELOAD");
							sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', bws));
			            	
							reloadConfig();
							loadMessageFile();
							
							sender.sendMessage(prefix + "§aSuccessfully reloaded.");
							return true;
			            } else {
			            	sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', perm));
						}
			        } else if(args[0].equalsIgnoreCase("update")) {
			        	if(sender.hasPermission("broadcaster.update")) {
			        		  
			        		UpdateChecker updater = new UpdateChecker(this, 49029, this.getFile(), UpdateChecker.UpdateType.NO_VERSION_CHECK, true); // Go straight to downloading, and announce progress to console.
			        		sender.sendMessage(prefix + updater.getResult().toString());
			        		  
			        		return true;
			        	} else {
			        		sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', perm));
						}
			        }
				} else {
					sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', perm));
				}
			}
			
			return true;
		}
		
		//Player
		Player p = (Player) sender;
		
		
		if(cmd.getName().equalsIgnoreCase("broadcaster") || cmd.getName().equalsIgnoreCase("brc")) {
			if(p.hasPermission("broadcaster.main")) {
				if(args.length == 0) {
					
					p.sendMessage(prefix + "§3Commands");
					p.sendMessage("");
					sendhelp(p);
					
					return true;
				} else if(args[0].equalsIgnoreCase("start")) {
					if(p.hasPermission("broadcaster.start")) {
						
						if (this.running)
			            {
							String ar = getConfig().getString("Messages.ALREADY_RUNNING");
							p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', ar));
							
							return true;
			            } else {
			            	String ttsb = getConfig().getString("Messages.TRYING_TO_START_BROADCAST");
			            	p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', ttsb));
			            				            	
			            	startBroadcast();
			                  
			                String sst = getConfig().getString("Messages.SUCCESSFULLY_STARTED");
			                p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', sst));
			            }
						
			            						
						return true;
					} else {
						p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', perm));
					}
				} else if(args[0].equalsIgnoreCase("stop")) {
					if(p.hasPermission("broadcaster.stop")) {
						
						if(this.running) {
							
							Bukkit.getScheduler().cancelTask(id);
							
							String ss = getConfig().getString("Messages.SUCCESSFULLY_STOPPED").replace("&", "§"); 
				            p.sendMessage(prefix + ss);
				            
				            this.running = false;
							
						} else {
							
							String as = getConfig().getString("Messages.ALREADY_STOPPED");
				            p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', as));
				            
						}
						
						return true;
					} else {
						p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', perm));
					}
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (p.hasPermission("broadcaster.reload")) {
														            	
						stopBroadcast();
		                
						String bws = getConfig().getString("Messages.BROADCAST_STOPPED_BECAUSE_RELOAD");
						p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', bws));
		            	
						reloadConfig();
						loadMessageFile();
						
						p.sendMessage(prefix + "§aSuccessfully reloaded.");
						return true;
		            } else {
						p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', perm));
					}
		        } else if(args[0].equalsIgnoreCase("update")) {
		        	if(p.hasPermission("broadcaster.update")) {
		        		  
		        		UpdateChecker updater = new UpdateChecker(this, 49029, this.getFile(), UpdateChecker.UpdateType.NO_VERSION_CHECK, true); // Go straight to downloading, and announce progress to console.
		        		p.sendMessage(prefix + updater.getResult().toString());
		        		  
		        		return true;
		        	} else {
						p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', perm));
					}
		        }
			} else {
				p.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', perm));
			}
		}
		
		return false;
	}
	
	private void sendhelp(CommandSender sender) {
	 
		if(sender.hasPermission("broadcaster.brc")) {
			sender.sendMessage("§9/brc §f- §3Shows all commands");
		}
		if(sender.hasPermission("broadcaster.start")) {
			sender.sendMessage("§9/brc start §f- §3Start the Broadcast");
		}
		if(sender.hasPermission("broadcaster.stop")) {
			sender.sendMessage("§9/brc stop §f- §3Stop the Broadcast");
		}
		/*if(sender.hasPermission("broadcaster.cast")) {
			sender.sendMessage("§9/brc cast [message] §f- §3Cast's a message around the server");
		}*/
		if(sender.hasPermission("broadcaster.reload")) {
			sender.sendMessage("§9/brc reload §f- §3Reload the plugin! (Stop's the Broadcast!)");
		}
        if(sender.hasPermission("broadcaster.update")) {
            sender.sendMessage("§9/brc update §f- §3Download an new found update");
        }
    }
	
	//Config Tasks  	
	private void loadMessageFile() {
  		
  		File messageFile = new File("plugins/Broadcaster/messages.txt");

  		if(!messageFile.exists()) {
  			try {
                messageFile.createNewFile();
  				BufferedWriter bw = new BufferedWriter(new FileWriter(messageFile));
  										
  				bw.write("&6This is a message. &4<3");
  				bw.newLine();
  				bw.write("&aNow with placeholders! %n&2[v]&6[p]&9[*][**]");
  				bw.newLine();
  				bw.write("&3There are &6%online_players% &cout of &6%max_players% players &conline!");
  				bw.newLine();
  				bw.flush();
  				bw.close();
  			} catch (IOException e) {
  				e.printStackTrace();
  			}
  		}
  	}
  	
  	private void loadConfig() {

  		getConfig().options().header("This is the default Broadcaster Config"
  				+ "\n"
  				+ "\n"
  				+ "Change the interval as you like. It is treated as seconds."
  				+ "\n"
  				+ "For example: If you write 60, it will broadcast every 60 seconds.");
  		
  		
  		getConfig().addDefault("Cast.Prefix", "&b[&3Info&b] ");
  		getConfig().addDefault("Cast.Interval", 60);
  		getConfig().addDefault("Cast.OnServerStart", true);
  		getConfig().addDefault("Messages.SUCCESSFULLY_STARTED", "&aThe Broadcast was successfully started.");
	    getConfig().addDefault("Messages.SUCCESSFULLY_STOPPED", "&aThe Broadcast was successfully stopped.");
	    getConfig().addDefault("Messages.ALREADY_RUNNING", "&cThe Broadcast is already running!");
	    getConfig().addDefault("Messages.ALREADY_STOPPED", "&cThe Broadcast was already stopped!");
	    getConfig().addDefault("Messages.TRYING_TO_START_BROADCAST", "&6Trying to start the Broadcast...");
	    getConfig().addDefault("Messages.BROADCAST_STOPPED_BECAUSE_RELOAD", "&cBroadcast stopped because of reload!");
	    getConfig().addDefault("Messages.NoPermission", "&cYou don't have permission to do that!");
	    getConfig().addDefault("Messages.Need_More_Arguments", "&cToo few arguments!");
	
	    getConfig().options().copyDefaults(true);
	    saveConfig();
  	}
  	
  	
}
