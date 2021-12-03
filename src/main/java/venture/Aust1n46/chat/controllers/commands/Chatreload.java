package venture.Aust1n46.chat.controllers.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.inject.Inject;

import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.FormatUtils;
import venture.Aust1n46.chat.VentureChat;
import venture.Aust1n46.chat.controllers.VentureChatSpigotFlatFileController;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.model.VentureCommand;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Chatreload implements VentureCommand {
	@Inject
	private VentureChat plugin;
	@Inject
	private VentureChatSpigotFlatFileController spigotFlatFileController;
	@Inject
	private VentureChatPlayerApiService playerApiService;

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.reload")) {
			spigotFlatFileController.savePlayerData();
			playerApiService.clearMineverseChatPlayerMap();
			playerApiService.clearNameMap();
			playerApiService.clearOnlineMineverseChatPlayerMap();
			
			plugin.reloadConfig();
			plugin.initializeConfigReaders();
			
			spigotFlatFileController.loadLegacyPlayerData();
			spigotFlatFileController.loadPlayerData();
			for(Player p : plugin.getServer().getOnlinePlayers()) {
				VentureChatPlayer mcp = playerApiService.getMineverseChatPlayer(p);
				if(mcp == null) {
					Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Could not find player data post reload for currently online player: " + p.getName()));
					Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - There could be an issue with your player data saving."));
					String name = p.getName();
					UUID uuid = p.getUniqueId();
					mcp = new VentureChatPlayer(uuid, name);
				}
				mcp.setOnline(true);
				mcp.setHasPlayed(false);
				mcp.setJsonFormat();
				playerApiService.addMineverseChatOnlinePlayerToMap(mcp);
				playerApiService.addNameToMap(mcp);
			}
			
			Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Config reloaded"));	
			for(VentureChatPlayer player : playerApiService.getOnlineMineverseChatPlayers()) {
				if(player.getPlayer().hasPermission("venturechat.reload")) {
					player.getPlayer().sendMessage(LocalizedMessage.CONFIG_RELOADED.toString());
				}
			}
			return;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return;
	}
}
