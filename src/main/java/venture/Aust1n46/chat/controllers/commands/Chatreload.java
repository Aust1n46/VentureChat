package venture.Aust1n46.chat.controllers.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.VentureChatSpigotFlatFileController;
import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.JsonFormat;
import venture.Aust1n46.chat.model.UniversalCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;
import venture.Aust1n46.chat.utilities.FormatUtils;

public class Chatreload extends UniversalCommand {
	@Inject
	private VentureChat plugin;
	@Inject
	private VentureChatSpigotFlatFileController spigotFlatFileController;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	@Inject
	public Chatreload(String name) {
		super(name);
	}

	@Override
	public void executeCommand(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.reload")) {
			spigotFlatFileController.savePlayerData();
			playerApiService.clearMineverseChatPlayerMap();
			playerApiService.clearNameMap();
			playerApiService.clearOnlineMineverseChatPlayerMap();

			plugin.reloadConfig();
			configService.postConstruct();

			spigotFlatFileController.loadLegacyPlayerData();
			spigotFlatFileController.loadPlayerData();
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				VentureChatPlayer mcp = playerApiService.getMineverseChatPlayer(p);
				if (mcp == null) {
					Bukkit.getConsoleSender()
							.sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - Could not find player data post reload for currently online player: " + p.getName()));
					Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&c - There could be an issue with your player data saving."));
					String name = p.getName();
					UUID uuid = p.getUniqueId();
					mcp = new VentureChatPlayer(uuid, name, configService.getDefaultChannel());
				}
				mcp.setOnline(true);
				mcp.setPlayer(plugin.getServer().getPlayer(mcp.getUuid()));
				mcp.setHasPlayed(false);
				String jsonFormat = mcp.getJsonFormat();
				for (JsonFormat j : configService.getJsonFormats()) {
					if (mcp.getPlayer().hasPermission("venturechat.json." + j.getName())) {
						if (configService.getJsonFormat(mcp.getJsonFormat()).getPriority() > j.getPriority()) {
							jsonFormat = j.getName();
						}
					}
				}
				mcp.setJsonFormat(jsonFormat);
				playerApiService.addMineverseChatOnlinePlayerToMap(mcp);
				playerApiService.addNameToMap(mcp);
			}

			Bukkit.getConsoleSender().sendMessage(FormatUtils.FormatStringAll("&8[&eVentureChat&8]&e - Config reloaded"));
			for (VentureChatPlayer player : playerApiService.getOnlineMineverseChatPlayers()) {
				if (player.getPlayer().hasPermission("venturechat.reload")) {
					player.getPlayer().sendMessage(LocalizedMessage.CONFIG_RELOADED.toString());
				}
			}
			return;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return;
	}
}
