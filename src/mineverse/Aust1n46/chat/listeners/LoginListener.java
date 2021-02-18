package mineverse.Aust1n46.chat.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
	private boolean firstPlayerHasJoined = false;

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent plog) {
		if(!plugin.getConfig().getConfigurationSection("logout").getString("message", "Default").equalsIgnoreCase("Default")) {
			plog.setQuitMessage(Format.FormatStringAll(plugin.getConfig().getConfigurationSection("logout").getString("message", "Default").replace("{player}", plog.getPlayer().getName())));
		}
		if(!plugin.getConfig().getConfigurationSection("logout").getBoolean("enabled", true)) {
			plog.setQuitMessage("");
		}
		playerLeaving(plog.getPlayer());
	}
	
	private void playerLeaving(Player player) {
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(player);
		PlayerData.savePlayerData(mcp);
		mcp.clearMessages();
		mcp.setOnline(false);
		MineverseChatAPI.removeMineverseChatOnlinePlayerToMap(mcp);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) throws Exception {
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(event.getPlayer());
		if(mcp == null) {
			Player player = event.getPlayer();
			String name = player.getName();
			UUID uuid = player.getUniqueId();
			ChatChannel current = ChatChannel.getDefaultChannel();
			Set<UUID> ignores = new HashSet<UUID>();
			Set<String> listening = new HashSet<String>();
			listening.add(current.getName());
			HashMap<String, Integer> mutes = new HashMap<String, Integer>();
			Set<String> blockedCommands = new HashSet<String>();
			String jsonFormat = "Default";
			mcp = new MineverseChatPlayer(uuid, name, current, ignores, listening, mutes, blockedCommands, false, null, true, true, name, jsonFormat, false, false, false, true, true);
			MineverseChatAPI.addMineverseChatPlayerToMap(mcp);
			MineverseChatAPI.addNameToMap(mcp);
		}
		UUIDFetcher.checkOfflineUUIDWarning(mcp.getUUID());
		//check for name change
		if(!mcp.getName().equals(event.getPlayer().getName())) {
			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Detected Name Change. Old Name:&c " + mcp.getName() + " &eNew Name:&c " + event.getPlayer().getName()));
			MineverseChatAPI.removeNameFromMap(mcp.getName());
			mcp.setName(event.getPlayer().getName());
			MineverseChatAPI.addNameToMap(mcp);
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
		
		long delayInTicks = 20L;
		// Add extra delay to allow the sync to run properly
		if(!firstPlayerHasJoined) {
			delayInTicks = 100L;
			firstPlayerHasJoined = true;
		}
		final MineverseChatPlayer sync = mcp;
		plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
			public void run() {
				plugin.synchronize(sync, false);
			}
		}, delayInTicks);
		if(!plugin.getConfig().getConfigurationSection("login").getString("message", "Default").equalsIgnoreCase("Default")) {
			event.setJoinMessage(Format.FormatStringAll(plugin.getConfig().getConfigurationSection("login").getString("message", "Default").replace("{player}", event.getPlayer().getName())));
		}
		if(!plugin.getConfig().getConfigurationSection("login").getBoolean("enabled", true)) {
			event.setJoinMessage("");
		}
	}
}
