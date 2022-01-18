package mineverse.Aust1n46.chat.command.chat;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.database.PlayerData;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;

public class Chatreload extends Command {
	private MineverseChat plugin = MineverseChat.getInstance();

	public Chatreload() {
		super("chatreload");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.reload")) {
			PlayerData.savePlayerData();
			MineverseChatAPI.clearMineverseChatPlayerMap();
			MineverseChatAPI.clearNameMap();
			MineverseChatAPI.clearOnlineMineverseChatPlayerMap();

			plugin.reloadConfig();
			MineverseChat.initializeConfigReaders();

			PlayerData.loadLegacyPlayerData();
			PlayerData.loadPlayerData();
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(p);
				if (mcp == null) {
					Bukkit.getConsoleSender()
							.sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Could not find player data post reload for currently online player: " + p.getName()));
					Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - There could be an issue with your player data saving."));
					String name = p.getName();
					UUID uuid = p.getUniqueId();
					mcp = new MineverseChatPlayer(uuid, name);
				}
				mcp.setOnline(true);
				mcp.setHasPlayed(false);
				mcp.setJsonFormat();
				MineverseChatAPI.addMineverseChatOnlinePlayerToMap(mcp);
				MineverseChatAPI.addNameToMap(mcp);
			}

			Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&e - Config reloaded"));
			for (MineverseChatPlayer player : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
				if (player.getPlayer().hasPermission("venturechat.reload")) {
					player.getPlayer().sendMessage(LocalizedMessage.CONFIG_RELOADED.toString());
				}
			}
			return true;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return true;
	}
}
