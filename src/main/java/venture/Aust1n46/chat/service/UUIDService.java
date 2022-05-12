package venture.Aust1n46.chat.service;

import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.utilities.FormatUtils;

@Singleton
public class UUIDService {
	@Inject
	private VentureChat plugin;

	public boolean shouldSkipOfflineUUID(UUID uuid) {
		return (FormatUtils.uuidIsOffline(uuid) && !plugin.getConfig().getBoolean("offline_server_acknowledgement", false));
	}

	public void checkOfflineUUIDWarning(UUID uuid) {
		if (shouldSkipOfflineUUID(uuid)) {
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Detected Offline UUID!"));
			plugin.getServer().getConsoleSender()
					.sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - If you are using BungeeCord, make sure you have properly setup IP Forwarding."));
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - https://www.spigotmc.org/wiki/bungeecord-ip-forwarding/"));
			plugin.getServer().getConsoleSender()
					.sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - You can access this wiki page from the log file or just Google it."));
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils
					.FormatStringAll("&8[&eVentureChat&8]&c - If you're running a \"cracked\" server, player data might not be stored properly, and thus, you are on your own."));
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils
					.FormatStringAll("&8[&eVentureChat&8]&c - If you run your server in offline mode, you will probably lose your player data when switching to online mode!"));
			plugin.getServer().getConsoleSender().sendMessage(FormatUtils
					.FormatStringAll("&8[&eVentureChat&8]&c - No player data will be saved in offline mode unless you set the \"cracked\" server acknowledgement in the config!"));
			return;
		}
	}
}
