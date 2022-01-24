package mineverse.Aust1n46.chat.command.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.clip.placeholderapi.PlaceholderAPI;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;

public class Message extends Command {
	public Message() {
		super("message");
	}

	private MineverseChat plugin = MineverseChat.getInstance();

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return true;
		}

		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
		if (args.length == 0) {
			mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/" + command).replace("{args}", "[player] [message]"));
			return true;
		}

		if (plugin.getConfig().getBoolean("bungeecordmessaging", true)) {
			sendBungeeCordMessage(mcp, command, args);
			return true;
		}

		MineverseChatPlayer player = MineverseChatAPI.getOnlineMineverseChatPlayer(args[0]);
		if (player == null) {
			mcp.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
			return true;
		}
		if (!mcp.getPlayer().canSee(player.getPlayer())) {
			mcp.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
			return true;
		}
		if (player.getIgnores().contains(mcp.getUUID())) {
			mcp.getPlayer().sendMessage(LocalizedMessage.IGNORING_MESSAGE.toString().replace("{player}", player.getName()));
			return true;
		}
		if (!player.getMessageToggle()) {
			mcp.getPlayer().sendMessage(LocalizedMessage.BLOCKING_MESSAGE.toString().replace("{player}", player.getName()));
			return true;
		}

		if (args.length >= 2) {
			String msg = "";
			String echo = "";
			String send = "";
			String spy = "";
			if (args[1].length() > 0) {
				for (int r = 1; r < args.length; r++) {
					msg += " " + args[r];
				}
				if (mcp.hasFilter()) {
					msg = Format.FilterChat(msg);
				}
				if (mcp.getPlayer().hasPermission("venturechat.color.legacy")) {
					msg = Format.FormatStringLegacyColor(msg);
				}
				if (mcp.getPlayer().hasPermission("venturechat.color")) {
					msg = Format.FormatStringColor(msg);
				}
				if (mcp.getPlayer().hasPermission("venturechat.format")) {
					msg = Format.FormatString(msg);
				}

				send = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("tellformatfrom").replaceAll("sender_", "")));
				echo = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("tellformatto").replaceAll("sender_", "")));
				spy = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("tellformatspy").replaceAll("sender_", "")));

				send = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(player.getPlayer(), send.replaceAll("receiver_", ""))) + msg;
				echo = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(player.getPlayer(), echo.replaceAll("receiver_", ""))) + msg;
				spy = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(player.getPlayer(), spy.replaceAll("receiver_", ""))) + msg;

				player.setReplyPlayer(mcp.getUUID());
				mcp.setReplyPlayer(player.getUUID());
				player.getPlayer().sendMessage(send);
				mcp.getPlayer().sendMessage(echo);
				if (player.hasNotifications()) {
					Format.playMessageSound(player);
				}
				if (!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
					for (MineverseChatPlayer sp : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
						if (sp.getName().equals(mcp.getName()) || sp.getName().equals(player.getName())) {
							continue;
						}
						if (sp.isSpy()) {
							sp.getPlayer().sendMessage(spy);
						}
					}
				}
			}
		}
		if (args.length == 1) {
			if (args[0].length() > 0) {
				if (!mcp.hasConversation() || (mcp.hasConversation() && !mcp.getConversation().toString().equals(player.getUUID().toString()))) {
					mcp.setConversation(player.getUUID());
					if (!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
						for (MineverseChatPlayer sp : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
							if (sp.getName().equals(mcp.getName())) {
								continue;
							}
							if (sp.isSpy()) {
								sp.getPlayer().sendMessage(LocalizedMessage.ENTER_PRIVATE_CONVERSATION_SPY.toString().replace("{player_sender}", mcp.getName())
										.replace("{player_receiver}", player.getName()));
							}
						}
					}
					mcp.getPlayer().sendMessage(LocalizedMessage.ENTER_PRIVATE_CONVERSATION.toString().replace("{player_receiver}", player.getName()));
				} else {
					mcp.setConversation(null);
					if (!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
						for (MineverseChatPlayer sp : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
							if (sp.getName().equals(mcp.getName())) {
								continue;
							}
							if (sp.isSpy()) {
								sp.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION_SPY.toString().replace("{player_sender}", mcp.getName())
										.replace("{player_receiver}", player.getName()));
							}
						}
					}
					mcp.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION.toString().replace("{player_receiver}", player.getName()));
				}
			}
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String label, String[] args) {
		if (plugin.getConfig().getBoolean("bungeecordmessaging", true)) {
			List<String> completions = new ArrayList<>();
			StringUtil.copyPartialMatches(args[args.length - 1], MineverseChatAPI.getNetworkPlayerNames(), completions);
			Collections.sort(completions);
			return completions;
		}
		return super.tabComplete(sender, label, args);
	}

	private void sendBungeeCordMessage(MineverseChatPlayer mcp, String command, String[] args) {
		if (args.length < 2) {
			mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/" + command).replace("{args}", "[player] [message]"));
			return;
		}
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutStream);
		StringBuilder msgBuilder = new StringBuilder();
		for (int r = 1; r < args.length; r++) {
			msgBuilder.append(" " + args[r]);
		}
		String msg = msgBuilder.toString();
		if (mcp.hasFilter()) {
			msg = Format.FilterChat(msg);
		}
		if (mcp.getPlayer().hasPermission("venturechat.color.legacy")) {
			msg = Format.FormatStringLegacyColor(msg);
		}
		if (mcp.getPlayer().hasPermission("venturechat.color")) {
			msg = Format.FormatStringColor(msg);
		}
		if (mcp.getPlayer().hasPermission("venturechat.format")) {
			msg = Format.FormatString(msg);
		}

		String send = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("tellformatfrom").replaceAll("sender_", "")));
		String echo = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("tellformatto").replaceAll("sender_", "")));
		String spy = "VentureChat:NoSpy";
		if (!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
			spy = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("tellformatspy").replaceAll("sender_", "")));
		}
		try {
			out.writeUTF("Message");
			out.writeUTF("Send");
			out.writeUTF(args[0]);
			out.writeUTF(mcp.getUUID().toString());
			out.writeUTF(mcp.getName());
			out.writeUTF(send);
			out.writeUTF(echo);
			out.writeUTF(spy);
			out.writeUTF(msg);
			mcp.getPlayer().sendPluginMessage(plugin, MineverseChat.PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
