package mineverse.Aust1n46.chat.listeners;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.database.PlayerData;
import mineverse.Aust1n46.chat.utilities.Format;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

//This class listens for when Players login or logout and manages their wrapped MineverseChatPlayer
//and it's data.
public class LoginListener implements Listener {
	private MineverseChat plugin = MineverseChat.getInstance();
	private FileConfiguration playerData = PlayerData.getPlayerData();

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerKick(PlayerKickEvent plog) {
		if(!plugin.getConfig().getConfigurationSection("logout").getString("message", "Default").equalsIgnoreCase("Default")) {
			plog.setLeaveMessage(Format.FormatStringAll(plugin.getConfig().getConfigurationSection("logout").getString("message", "Default").replace("{player}", plog.getPlayer().getName())));
		}
		if(!plugin.getConfig().getConfigurationSection("logout").getBoolean("enabled", true)) {
			plog.setLeaveMessage("");
		}
		playerLeaving(plog.getPlayer());
	}

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

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) throws Exception {
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(event.getPlayer());
		if(mcp == null) {
			Player player = event.getPlayer();
			String name = player.getName();
			// Disabling Mojang UUID Query
			//UUID uuid = UUIDFetcher.getUUIDOf(name);
			UUID uuid = player.getUniqueId();
			ChatChannel current = ChatChannel.getDefaultChannel();
			Set<UUID> ignores = new HashSet<UUID>();
			Set<String> listening = new HashSet<String>();
			listening.add(current.getName());
			HashMap<String, Integer> mutes = new HashMap<String, Integer>();
			Set<String> blockedCommands = new HashSet<String>();
			String jsonFormat = "Default";
			mcp = new MineverseChatPlayer(uuid, name, current, ignores, listening, mutes, blockedCommands, false, null, true, true, name, jsonFormat, false, false, false, true, true);
			MineverseChat.players.add(mcp);
		}
		mcp.setName(event.getPlayer().getName());
		mcp.setOnline(true);
		mcp.setHasPlayed(false);
		MineverseChat.onlinePlayers.add(mcp);
		mcp.setJsonFormat();
		if(!mcp.getPlayer().getDisplayName().equals(mcp.getName())) {
			mcp.setNickname(event.getPlayer().getDisplayName());
		}
		mcp.getPlayer().setDisplayName(Format.FormatStringAll(mcp.getNickname()));
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
		final MineverseChatPlayer sync = mcp;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				plugin.synchronize(sync, false);
			}
		}, 10L);
		if(!plugin.getConfig().getConfigurationSection("login").getString("message", "Default").equalsIgnoreCase("Default")) {
			event.setJoinMessage(Format.FormatStringAll(plugin.getConfig().getConfigurationSection("login").getString("message", "Default").replace("{player}", event.getPlayer().getName())));
		}
		if(!plugin.getConfig().getConfigurationSection("login").getBoolean("enabled", true)) {
			event.setJoinMessage("");
		}
		/*
		 * if(MineverseChat.onlinePlayers.size() == 1) {
		 * plugin.updatePlayerList(sync, true); }
		 * plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new
		 * Runnable() { public void run() {
		 * MineverseChat.networkPlayers.put(sync.getName(),
		 * sync.getPlayer().getServer().getServerName());
		 * plugin.updatePlayerList(sync, false); } }, 1L);
		 */
	}

	private void playerLeaving(Player player) {
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(player);
		ConfigurationSection cs = playerData.getConfigurationSection("players." + mcp.getUUID().toString());
		if(mcp.getPlayer() == null) {
			return;
		}
		if(cs == null) {
			ConfigurationSection ps = playerData.getConfigurationSection("players");
			if(ps == null) {
				cs = playerData.createSection("players");
			}
			cs = playerData.createSection("players." + mcp.getUUID().toString());
		}
		cs.set("name", mcp.getName());
		cs.set("current", mcp.getCurrentChannel().getName());
		String ignores = "";
		for(UUID s : mcp.getIgnores()) {
			ignores += s.toString() + ",";
		}
		cs.set("ignores", ignores);
		String listening = "";
		for(String channel : mcp.getListening()) {
			ChatChannel c = ChatChannel.getChannel(channel);
			listening += c.getName() + ",";
		}
		String mutes = "";
		for(String channel : mcp.getMutes().keySet()) {
			ChatChannel c = ChatChannel.getChannel(channel);
			mutes += c.getName() + ":" + mcp.getMutes().get(c.getName()) + ",";
		}
		String blockedCommands = "";
		for(String s : mcp.getBlockedCommands()) {
			blockedCommands += s + ",";
		}
		if(listening.length() > 0) {
			listening = listening.substring(0, listening.length() - 1);
		}
		cs.set("listen", listening);
		if(mutes.length() > 0) {
			mutes = mutes.substring(0, mutes.length() - 1);
		}
		cs.set("mutes", mutes);
		if(blockedCommands.length() > 0) {
			blockedCommands = blockedCommands.substring(0, blockedCommands.length() - 1);
		}
		cs.set("blockedcommands", blockedCommands);
		cs.set("host", mcp.isHost());
		cs.set("party", mcp.hasParty() ? mcp.getParty().toString() : "");
		cs.set("filter", mcp.hasFilter());
		cs.set("notifications", mcp.hasNotifications());
		cs.set("nickname", mcp.getPlayer().getDisplayName());
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MMM/dd HH:mm:ss");
		String dateNow = formatter.format(currentDate.getTime());
		cs.set("date", dateNow);
		mcp.clearMessages();
		mcp.setOnline(false);
		MineverseChat.onlinePlayers.remove(mcp);
		// System.out.println(mcp.getName() + " logged off.");
		/*
		 * final MineverseChatPlayer sync = mcp;
		 * plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new
		 * Runnable() { public void run() {
		 * MineverseChat.networkPlayers.remove(sync.getName());
		 * plugin.updatePlayerList(sync, false); sync.setOnline(false);
		 * MineverseChat.onlinePlayers.remove(sync); } }, 1L);
		 */
	}
}