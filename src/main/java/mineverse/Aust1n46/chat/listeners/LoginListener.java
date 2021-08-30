package mineverse.Aust1n46.chat.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.database.PlayerData;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.utilities.UUIDFetcher;

//This class listens for when Players login or logout and manages their wrapped MineverseChatPlayer
//and it's data.
public class LoginListener implements Listener {
	private MineverseChat plugin = MineverseChat.getInstance();

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(playerQuitEvent.getPlayer());
		PlayerData.savePlayerData(mcp);
		mcp.clearMessages();
		mcp.setOnline(false);
		MineverseChatAPI.removeMineverseChatOnlinePlayerToMap(mcp);
	}
	
	void handleNameChange(MineverseChatPlayer mcp, Player eventPlayerInstance) {
		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Detected Name Change. Old Name:&c " + mcp.getName() + " &eNew Name:&c " + eventPlayerInstance.getName()));
		MineverseChatAPI.removeNameFromMap(mcp.getName());
		//reset nickname if nickname equals old username
		if(mcp.getName().equals(eventPlayerInstance.getDisplayName())) {
			eventPlayerInstance.setDisplayName(eventPlayerInstance.getName());
			mcp.setNickname(eventPlayerInstance.getName());
		}
		mcp.setName(eventPlayerInstance.getName());
		MineverseChatAPI.addNameToMap(mcp);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) throws Exception {
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(event.getPlayer());
		if(mcp == null) {
			Player player = event.getPlayer();
			String name = player.getName();
			UUID uuid = player.getUniqueId();
			mcp = new MineverseChatPlayer(uuid, name);
			MineverseChatAPI.addMineverseChatPlayerToMap(mcp);
			MineverseChatAPI.addNameToMap(mcp);
		}
		UUIDFetcher.checkOfflineUUIDWarning(mcp.getUUID());
		//check for name change
		if(!mcp.getName().equals(event.getPlayer().getName())) {
			handleNameChange(mcp, event.getPlayer());
		}
		if(!event.getPlayer().getDisplayName().equals(mcp.getName())) {
			mcp.setNickname(event.getPlayer().getDisplayName());
		}
		event.getPlayer().setDisplayName(Format.FormatStringAll(mcp.getNickname()));
		mcp.setOnline(true);
		mcp.setHasPlayed(false);
		MineverseChatAPI.addMineverseChatOnlinePlayerToMap(mcp);
		mcp.setJsonFormat();
		if(plugin.getConfig().getBoolean("nickname-in-tablist", false)) {
			String nick = mcp.getNickname();
			if(nick.length() >= 16) {
				nick = nick.substring(0, 16);
			}
			mcp.getPlayer().setPlayerListName(Format.FormatStringAll(nick));
		}	
		for(ChatChannel ch : ChatChannel.getAutojoinList()) {
			if(ch.hasPermission()) {
				if(mcp.getPlayer().hasPermission(ch.getPermission())) {
					mcp.addListening(ch.getName());
				}
			}
			else {
				mcp.addListening(ch.getName());
			}
		}
		
		try {
			if(plugin.getServer().spigot().getPaperConfig().getBoolean("settings.velocity-support.enabled") || plugin.getServer().spigot().getConfig().getBoolean("settings.bungeecord")) {
				long delayInTicks = 20L;
				final MineverseChatPlayer sync = mcp;
				plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
					public void run() {
						MineverseChat.synchronize(sync, false);
					}
				}, delayInTicks);
			}
		}
		catch(NoSuchMethodError exception) { // Thrown if server isn't Paper.
			// Do nothing
		}
	}
}
