package venture.Aust1n46.chat.controllers.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.PlayerCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.PlayerApiService;

public class Ignore extends PlayerCommand {
	@Inject
	private VentureChat plugin;
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private PlayerApiService playerApiService;

	@Inject
	public Ignore(String name) {
		super(name);
	}

	@Override
	public void executeCommand(Player sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return;
		}
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer((Player) sender);
		if (args.length == 0) {
			mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS_IGNORE.toString());
			return;
		}
		if (args[0].equalsIgnoreCase("list")) {
			String ignoreList = "";
			for (UUID ignore : mcp.getIgnores()) {
				VentureChatPlayer i = playerApiService.getMineverseChatPlayer(ignore);
				String iName = ignore.toString();
				if (i != null) {
					iName = i.getName();
				}
				ignoreList += ChatColor.RED + iName + ChatColor.WHITE + ", ";
			}
			mcp.getPlayer().sendMessage(LocalizedMessage.IGNORE_LIST_HEADER.toString());
			if (ignoreList.length() > 0) {
				mcp.getPlayer().sendMessage(ignoreList.substring(0, ignoreList.length() - 2));
			}
			return;
		}
		if (mcp.getName().equalsIgnoreCase(args[0])) {
			mcp.getPlayer().sendMessage(LocalizedMessage.IGNORE_YOURSELF.toString());
			return;
		}
		if (plugin.getConfig().getBoolean("bungeecordmessaging", true)) {
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(byteOutStream);
			try {
				out.writeUTF("Ignore");
				out.writeUTF("Send");
				out.writeUTF(args[0]);
				out.writeUTF(mcp.getUuid().toString());
				pluginMessageController.sendPluginMessage(byteOutStream);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		VentureChatPlayer player = playerApiService.getOnlineMineverseChatPlayer(args[0]);
		if (player == null) {
			mcp.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
			return;
		}
		if (mcp.getIgnores().contains(player.getUuid())) {
			mcp.getPlayer().sendMessage(LocalizedMessage.IGNORE_PLAYER_OFF.toString().replace("{player}", player.getName()));
			mcp.getIgnores().remove(player.getUuid());
			pluginMessageController.synchronize(mcp, true);
			return;
		}
		if (player.getPlayer().hasPermission("venturechat.ignore.bypass")) {
			mcp.getPlayer().sendMessage(LocalizedMessage.IGNORE_PLAYER_CANT.toString().replace("{player}", player.getName()));
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.IGNORE_PLAYER_ON.toString().replace("{player}", player.getName()));
		mcp.getIgnores().add(player.getUuid());
		pluginMessageController.synchronize(mcp, true);
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		if (plugin.getConfig().getBoolean("bungeecordmessaging", true)) {
			List<String> completions = new ArrayList<>();
			StringUtil.copyPartialMatches(args[args.length - 1], playerApiService.getNetworkPlayerNames(), completions);
			Collections.sort(completions);
			return completions;
		}
		return super.tabComplete(sender, alias, args);
	}
}
