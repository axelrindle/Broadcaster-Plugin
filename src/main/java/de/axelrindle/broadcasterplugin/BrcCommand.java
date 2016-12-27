package de.axelrindle.broadcasterplugin;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * The plugin's main command <b>/brc</b>.
 */
class BrcCommand implements CommandExecutor {

    private JavaPlugin plugin;

    BrcCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            onPlayer(sender, command, label, args);
        } else {
            onConsole(sender, command, label, args);
        }

        return true;
    }

    private void onConsole(CommandSender sender, Command command, String label, String[] args) {

    }

    private void onPlayer(CommandSender sender, Command command, String label, String[] args) {

        Player p = (Player) sender;

        if(p.hasPermission("broadcaster.brc")) {
            if(args.length == 0) {
                sendHelp(sender);
            } else if(args[0].equalsIgnoreCase("start")) {
                if(p.hasPermission("broadcaster.start")) {

                    if(!BroadcastThread.isRunning()) {
                        BroadcastThread.start(plugin, ((Broadcaster) plugin).messages, plugin.getConfig().getInt("Cast.Interval"));
                        sender.sendMessage(
                                Formatter.formatColors(plugin.getConfig().getString("Messages.Started"))
                        );
                    } else {
                        sender.sendMessage(
                                Formatter.formatColors(plugin.getConfig().getString("Messages.AlreadyRunning"))
                        );
                    }

                } else {
                    noPermission(sender);
                }
            } else if(args[0].equalsIgnoreCase("stop")) {
                if(p.hasPermission("broadcaster.stop")) {

                    if(BroadcastThread.isRunning()) {
                        BroadcastThread.stop();
                        sender.sendMessage(
                                Formatter.formatColors(plugin.getConfig().getString("Messages.Stopped"))
                        );
                    } else {
                        sender.sendMessage(
                                Formatter.formatColors(plugin.getConfig().getString("Messages.AlreadyStopped"))
                        );
                    }

                } else {
                    noPermission(sender);
                }
            } else if(args[0].equalsIgnoreCase("reload")) {
                if(p.hasPermission("broadcaster.reload")) {

                    if(!BroadcastThread.isRunning()) {
                        BroadcastThread.stop();
                        sender.sendMessage(
                                Formatter.formatColors(plugin.getConfig().getString("Messages.ReloadStopped"))
                        );
                    }
                    plugin.reloadConfig();

                } else {
                    noPermission(sender);
                }
            } else if(args[0].equalsIgnoreCase("cast")) {
                if(p.hasPermission("broadcaster.cast")) {

                    String[] subarray = (String[]) ArrayUtils.subarray(args, 1, args.length);
                    String s = "";
                    for (String s1 : subarray) {
                        s += s1;
                    }
                    s = Formatter.format(plugin, s);

                    boolean needsPermission = plugin.getConfig().getBoolean("Cast.NeedPermissionToSee");
                    if(needsPermission) {
                        Bukkit.getServer().broadcast(s, "broadcaster.see");
                    } else {
                        Bukkit.getServer().broadcastMessage(s);
                    }

                } else {
                    noPermission(sender);
                }
            }
        } else {
            noPermission(sender);
        }

    }

    /**
     * Sends the {@link CommandSender} a message indicating that he has no permission to execute a command.
     *
     * @param sender The {@link CommandSender} to inform.
     */
    private void noPermission(CommandSender sender) {
        sender.sendMessage(
                Formatter.formatColors(plugin.getConfig().getString("Messages.NoPermission"))
        );
    }

    /**
     * Sends the {@link CommandSender} an overview over all commands he may execute.
     *
     * @param sender The {@link CommandSender} to inform.
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Formatter.formatColors(Broadcaster.prefix) + "Help");
        int i = 0;
        if(sender.hasPermission("broadcaster.brc")) {
            sender.sendMessage("§2/brc §f- §3Shows all commands");
            i++;
        }
        if(sender.hasPermission("broadcaster.start")) {
            sender.sendMessage("§9/brc start §f- §3Start the Broadcast");
            i++;
        }
        if(sender.hasPermission("broadcaster.stop")) {
            sender.sendMessage("§9/brc stop §f- §3Stop the Broadcast");
            i++;
        }
		if(sender.hasPermission("broadcaster.cast")) {
			sender.sendMessage("§9/brc cast [message] §f- §3Cast's a message around the server");
            i++;
		}
        if(sender.hasPermission("broadcaster.reload")) {
            sender.sendMessage("§9/brc reload §f- §3Reload the plugin! (Stop's the Broadcast!)");
            i++;
        }

        if(i == 0) {
            noPermission(sender);
        }
    }
}
