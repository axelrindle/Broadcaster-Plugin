package de.lalo5.broadcaster;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BroadcasterListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
	  Player player = event.getPlayer();
	  if(player.hasPermission("broadcaster.update") && Broadcaster.update)
	  {
	    player.sendMessage("An update is available: " + Broadcaster.name + ", a " + Broadcaster.type + " for " + Broadcaster.version + " available at " + Broadcaster.link);
	    // Will look like - An update is available: AntiCheat v1.5.9, a release for CB 1.6.2-R0.1 available at http://media.curseforge.com/XYZ
	    player.sendMessage("Type /brc update if you would like to automatically update.");
	  }
	}
	
	
}
