package mineverse.Aust1n46.chat.command.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;

public class Reply extends Command {
	private MineverseChat plugin = MineverseChat.getInstance();

	public Reply() {
		super("reply");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return true;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
		if (args.length > 0) {
			if (mcp.hasReplyPlayer()) {
				if (plugin.getConfig().getBoolean("bungeecordmessaging", true)) {
					sendBungeeCordReply(mcp, args);
					return true;
				}

				MineverseChatPlayer player = MineverseChatAPI.getOnlineMineverseChatPlayer(mcp.getReplyPlayer());
				if (player == null) {
					mcp.getPlayer().sendMessage(LocalizedMessage.NO_PLAYER_TO_REPLY_TO.toString());
					return true;
				}
				if (!mcp.getPlayer().canSee(player.getPlayer())) {
					mcp.getPlayer().sendMessage(LocalizedMessage.NO_PLAYER_TO_REPLY_TO.toString());
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
				String msg = "";
				String echo = "";
				String send = "";
				String spy = "";
				if (args.length > 0) {
					for (int r = 0; r < args.length; r++)
						msg += " " + args[r];
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

					send = Format
							.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("replyformatfrom").replaceAll("sender_", "")));
					echo = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("replyformatto").replaceAll("sender_", "")));
					spy = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("replyformatspy").replaceAll("sender_", "")));

					send = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(player.getPlayer(), send.replaceAll("receiver_", ""))) + msg;
					echo = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(player.getPlayer(), echo.replaceAll("receiver_", ""))) + msg;
					spy = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(player.getPlayer(), spy.replaceAll("receiver_", ""))) + msg;

					if (!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
						for (MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
							if (p.getName().equals(mcp.getName()) || p.getName().equals(player.getName())) {
								continue;
							}
							if (p.isSpy()) {
								p.getPlayer().sendMessage(spy);
							}
						}
					}
					player.getPlayer().sendMessage(send);
					mcp.getPlayer().sendMessage(echo);
					if (player.hasNotifications()) {
						Format.playMessageSound(player);
					}
					player.setReplyPlayer(mcp.getUUID());
					return true;
				}
			}
			mcp.getPlayer().sendMessage(LocalizedMessage.NO_PLAYER_TO_REPLY_TO.toString());
			return true;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/reply").replace("{args}", "[message]"));
		return true;
	}

	private void sendBungeeCordReply(MineverseChatPlayer mcp, String[] args) {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutStream);
		StringBuilder msgBuilder = new StringBuilder();
		for (int r = 0; r < args.length; r++) {
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

		String send = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("replyformatfrom").replaceAll("sender_", "")));
		String echo = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("replyformatto").replaceAll("sender_", "")));
		String spy = "VentureChat:NoSpy";
		if (!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
			spy = Format.FormatStringAll(PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), plugin.getConfig().getString("replyformatspy").replaceAll("sender_", "")));
		}
		try {
			out.writeUTF("Message");
			out.writeUTF("Send");
			out.writeUTF(MineverseChatAPI.getMineverseChatPlayer(mcp.getReplyPlayer()).getName());
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
