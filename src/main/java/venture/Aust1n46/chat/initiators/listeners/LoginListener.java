package venture.Aust1n46.chat.initiators.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import mineverse.Aust1n46.chat.utilities.FormatUtils;
import venture.Aust1n46.chat.Logger;
import venture.Aust1n46.chat.VentureChat;
import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.controllers.VentureChatSpigotFlatFileController;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.UUIDService;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

/**
 * Manages player login and logout events.
 * 
 * @author Aust1n46
 */
@Singleton
public class LoginListener implements Listener {
	@Inject
	private VentureChat plugin;
	@Inject
	private UUIDService uuidService;
	@Inject
	private VentureChatSpigotFlatFileController spigotFlatFileController;
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	@Inject
	private Logger log;

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
		VentureChatPlayer ventureChatPlayer = playerApiService.getOnlineMineverseChatPlayer(playerQuitEvent.getPlayer());
		if (ventureChatPlayer == null) {
			log.warn("onPlayerQuit() Could not find VentureChatPlayer");
		} else {
			spigotFlatFileController.savePlayerData(ventureChatPlayer);
			ventureChatPlayer.clearMessages();
			ventureChatPlayer.setOnline(false);
			playerApiService.removeMineverseChatOnlinePlayerToMap(ventureChatPlayer);
			log.debug("onPlayerQuit() ventureChatPlayer:{} quit", ventureChatPlayer);
		}
	}
	
	void handleNameChange(VentureChatPlayer mcp, Player eventPlayerInstance) {
		plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Detected Name Change. Old Name:&c " + mcp.getName() + " &eNew Name:&c " + eventPlayerInstance.getName()));
		playerApiService.removeNameFromMap(mcp.getName());
		mcp.setName(eventPlayerInstance.getName());
		playerApiService.addNameToMap(mcp);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) throws Exception {
		VentureChatPlayer mcp = playerApiService.getMineverseChatPlayer(event.getPlayer());
		Player player = event.getPlayer();
		String name = player.getName();
		if(mcp == null) {
			UUID uuid = player.getUniqueId();
			mcp = new VentureChatPlayer(uuid, name);
			playerApiService.addMineverseChatPlayerToMap(mcp);
			playerApiService.addNameToMap(mcp);
		}
		uuidService.checkOfflineUUIDWarning(mcp.getUuid());
		//check for name change
		if(!mcp.getName().equals(name)) {
			handleNameChange(mcp, event.getPlayer());
		}
		mcp.setOnline(true);
		mcp.setHasPlayed(false);
		playerApiService.addMineverseChatOnlinePlayerToMap(mcp);
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
		
		try {
			if(plugin.getServer().spigot().getConfig().getBoolean("settings.bungeecord") || plugin.getServer().spigot().getPaperConfig().getBoolean("settings.velocity-support.enabled")) {
				long delayInTicks = 20L;
				final VentureChatPlayer sync = mcp;
				plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
					public void run() {
						pluginMessageController.synchronize(sync, false);
					}
				}, delayInTicks);
			}
		}
		catch(NoSuchMethodError exception) { // Thrown if server isn't Paper.
			// Do nothing
		}
	}
}
