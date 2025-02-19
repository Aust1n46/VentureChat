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

/**
 * Manages player login and logout events.
 * 
 * @author Aust1n46
 */
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
		mcp.setName(eventPlayerInstance.getName());
		MineverseChatAPI.addNameToMap(mcp);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) throws Exception {
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(event.getPlayer());
		Player player = event.getPlayer();
		String name = player.getName();
		if(mcp == null) {
			UUID uuid = player.getUniqueId();
			mcp = new MineverseChatPlayer(uuid, name);
			MineverseChatAPI.addMineverseChatPlayerToMap(mcp);
			MineverseChatAPI.addNameToMap(mcp);
		}
		UUIDFetcher.checkOfflineUUIDWarning(mcp.getUUID());
		//check for name change
		if(!mcp.getName().equals(name)) {
			handleNameChange(mcp, event.getPlayer());
		}
		mcp.setOnline(true);
		mcp.setHasPlayed(false);
		MineverseChatAPI.addMineverseChatOnlinePlayerToMap(mcp);
		mcp.setJsonFormat();
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

		if (MineverseChat.isConnectedToProxy()) {
			long delayInTicks = 20L;
			final MineverseChatPlayer sync = mcp;
			plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
				public void run() {
					MineverseChat.synchronize(sync, false);
				}
			}, delayInTicks);
		}
	}
}
