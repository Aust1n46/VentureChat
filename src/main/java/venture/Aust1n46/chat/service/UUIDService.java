package venture.Aust1n46.chat.service;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.proxy.VentureChatProxySource;
import venture.Aust1n46.chat.utilities.FormatUtils;

@Singleton
public class UUIDService {
	@Inject
	private VentureChat plugin;
	
	/**
     * Returns whether the passed UUID is a v3 UUID. Offline UUIDs are v3, online are v4.
     *
     * @param uuid the UUID to check
     * @return whether the UUID is a v3 UUID & thus is offline
     */
    public boolean uuidIsOffline(UUID uuid) {
        return uuid.version() == 3;
    }
    
    public boolean shouldSkipOfflineUUID(UUID uuid) {
    	return (uuidIsOffline(uuid) && !plugin.getConfig().getBoolean("offline_server_acknowledgement", false));
    }
    
    public boolean shouldSkipOfflineUUIDProxy(UUID uuid, VentureChatProxySource source) {
    	return (uuidIsOffline(uuid) && !source.isOfflineServerAcknowledgementSet());
    }
    
    public void checkOfflineUUIDWarning(UUID uuid) {
		if(shouldSkipOfflineUUID(uuid)) {
    		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Detected Offline UUID!"));
    		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - If you are using BungeeCord, make sure you have properly setup IP Forwarding."));
    		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - https://www.spigotmc.org/wiki/bungeecord-ip-forwarding/"));
    		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - You can access this wiki page from the log file or just Google it."));
    		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - If you're running a \"cracked\" server, player data might not be stored properly, and thus, you are on your own."));
    		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - If you run your server in offline mode, you will probably lose your player data when switching to online mode!"));
    		Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - No player data will be saved in offline mode unless you set the \"cracked\" server acknowledgement in the config!"));
    		return;
		}
    }
    
    public void checkOfflineUUIDWarningProxy(UUID uuid, VentureChatProxySource source) {
		if(shouldSkipOfflineUUIDProxy(uuid, source)) {
			source.sendConsoleMessage("&8[&eVentureChat&8]&c - Detected Offline UUID!");
			source.sendConsoleMessage("&8[&eVentureChat&8]&c - If you are using BungeeCord, make sure you have properly setup IP Forwarding.");
			source.sendConsoleMessage("&8[&eVentureChat&8]&c - https://www.spigotmc.org/wiki/bungeecord-ip-forwarding/");
			source.sendConsoleMessage("&8[&eVentureChat&8]&c - You can access this wiki page from the log file or just Google it.");
			source.sendConsoleMessage("&8[&eVentureChat&8]&c - If you're running a \"cracked\" server, player data might not be stored properly, and thus, you are on your own.");
			source.sendConsoleMessage("&8[&eVentureChat&8]&c - If you run your server in offline mode, you will probably lose your player data when switching to online mode!");
			source.sendConsoleMessage("&8[&eVentureChat&8]&c - No player data will be saved in offline mode unless you set the \"cracked\" server acknowledgement in the config!");	
    		return;
		}
    }
}
