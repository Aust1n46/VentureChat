package mineverse.Aust1n46.chat.command.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.versions.VersionHandler;

public class Message extends MineverseCommand {
	private MineverseChat plugin;
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Message(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if(args.length == 0) {
			mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
			.replace("{command}", "/" + command)
			.replace("{args}", "[player] [message]"));
			return;
		}
		if(plugin.getConfig().getBoolean("bungeecordmessaging", true)) {
			if(args.length < 2) {
				mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
						.replace("{command}", "/" + command)
						.replace("{args}", "[player] [message]"));
				return;
			}
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(byteOutStream);
			String msg = "";
			String send = "";
			String echo = "";
			String spy = "";
			for(int r = 1; r < args.length; r++) {
				msg += " " + args[r];
			}
			if(mcp.hasFilter()) {
				msg = cc.FilterChat(msg);
			}
			if(mcp.getPlayer().hasPermission("venturechat.color")) {
				msg = Format.FormatStringColor(msg);
			}
			if(mcp.getPlayer().hasPermission("venturechat.format")) {
				msg = Format.FormatString(msg);
			}
			if(plugin.getConfig().getString("tellformatfrom").equalsIgnoreCase("Default")) {
				send = "{playerfrom} messages you:" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + msg;
			}
			else {
				send = Format.FormatStringAll(plugin.getConfig().getString("tellformatfrom")) + msg;
			}
			if(plugin.getConfig().getString("tellformatto").equalsIgnoreCase("Default")) {
				echo = "You message {playerto}:" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + msg;
			}
			else {
				echo = Format.FormatStringAll(plugin.getConfig().getString("tellformatto")) + msg;
			}
			if(plugin.getConfig().getString("tellformatspy").equalsIgnoreCase("Default")) {
				spy = "{playerfrom} messages {playerto}:" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + msg;
			}
			else {
				spy = Format.FormatStringAll(plugin.getConfig().getString("tellformatspy")) + msg;
			}
			try {
				out.writeUTF("Message");
				out.writeUTF("Send");
				// out.writeUTF(mcp.getPlayer().getServer().getServerName());
				out.writeUTF(args[0]);
				out.writeUTF(mcp.getUUID().toString());
				out.writeUTF(mcp.getName());
				out.writeUTF(send);
				out.writeUTF(echo);
				out.writeUTF(spy);
				mcp.getPlayer().sendPluginMessage(plugin, MineverseChat.PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
				out.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return;
		}
		MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
		for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
			if(ChatColor.stripColor(p.getNickname()).equals(args[0])) {
				player = p;
				break;
			}
		}
		if(player == null || !player.isOnline()) {
			mcp.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
					.replace("{args}", args[0]));
			return;
		}
		if(!mcp.getPlayer().canSee(player.getPlayer())) {
			mcp.getPlayer().sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
					.replace("{args}", args[0]));
			return;
		}
		if(player.getIgnores().contains(mcp.getUUID())) {
			mcp.getPlayer().sendMessage(LocalizedMessage.IGNORING_MESSAGE.toString()
					.replace("{player}", player.getName()));
			return;
		}
		if(!player.getMessageToggle()) {
			mcp.getPlayer().sendMessage(LocalizedMessage.BLOCKING_MESSAGE.toString()
					.replace("{player}", player.getName()));
			return;
		}
		if(args.length >= 2) {
			String msg = "";
			String echo = "";
			String send = "";
			String spy = "";
			if(args[1].length() > 0) {
				for(int r = 1; r < args.length; r++) {
					msg += " " + args[r];
				}
				if(mcp.hasFilter()) {
					msg = cc.FilterChat(msg);
				}
				if(mcp.getPlayer().hasPermission("venturechat.color")) {
					msg = Format.FormatStringColor(msg);
				}
				if(mcp.getPlayer().hasPermission("venturechat.format")) {
					msg = Format.FormatString(msg);
				}
				if(player.isAFK()) {
					mcp.getPlayer().sendMessage(ChatColor.GOLD + player.getName() + " is currently afk and might be unable to chat at this time.");
				}
				if(plugin.getConfig().getString("tellformatto").equalsIgnoreCase("Default")) {
					echo = "You message " + player.getNickname() + ":" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + msg;
				}
				else {
					echo = Format.FormatStringAll(plugin.getConfig().getString("tellformatto").replace("{playerto}", player.getNickname()).replace("{playerfrom}", mcp.getNickname())) + msg;
				}
				if(plugin.getConfig().getString("tellformatfrom").equalsIgnoreCase("Default")) {
					send = mcp.getNickname() + " messages you:" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + msg;
				}
				else {
					send = Format.FormatStringAll(plugin.getConfig().getString("tellformatfrom").replace("{playerto}", player.getNickname()).replace("{playerfrom}", mcp.getNickname())) + msg;
				}
				if(plugin.getConfig().getString("tellformatspy").equalsIgnoreCase("Default")) {
					spy = mcp.getName() + " messages " + player.getName() + ":" + ChatColor.valueOf(cc.tellColor.toUpperCase()) + msg;
				}
				else {
					spy = Format.FormatStringAll(plugin.getConfig().getString("tellformatspy").replace("{playerto}", player.getName()).replace("{playerfrom}", mcp.getName())) + msg;
				}
				player.setReplyPlayer(mcp.getUUID());
				mcp.setReplyPlayer(player.getUUID());
				player.getPlayer().sendMessage(send);
				mcp.getPlayer().sendMessage(echo);
				if(player.hasNotifications()) {
					if(VersionHandler.is1_8() || VersionHandler.is1_7_10() || VersionHandler.is1_7_2() || VersionHandler.is1_7_9()) {
						player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.valueOf("LEVEL_UP"), 1, 0);
					}
					else {
						player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 0);
					}
				}
				if(!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
					for(MineverseChatPlayer sp : MineverseChat.onlinePlayers) {
						if(sp.isSpy()) {
							sp.getPlayer().sendMessage(spy);
						}
					}
				}
			}
		}
		if(args.length == 1) {
			if(args[0].length() > 0) {
				if(!mcp.hasConversation() || (mcp.hasConversation() && !mcp.getConversation().toString().equals(player.getUUID().toString()))) {
					mcp.setConversation(player.getUUID());
					if(!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
						for(MineverseChatPlayer sp : MineverseChat.onlinePlayers) {
							if(sp.isSpy()) {
								sp.getPlayer().sendMessage(LocalizedMessage.ENTER_PRIVATE_CONVERSATION_SPY.toString()
										.replace("{player_sender}", mcp.getName())
										.replace("{player_receiver}", player.getName()));
							}
						}
					}
					mcp.getPlayer().sendMessage(LocalizedMessage.ENTER_PRIVATE_CONVERSATION.toString()
							.replace("{player_receiver}", player.getName()));
				}
				else {
					mcp.setConversation(null);
					if(!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
						for(MineverseChatPlayer sp : MineverseChat.onlinePlayers) {
							if(sp.isSpy()) {
								sp.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION_SPY.toString()
										.replace("{player_sender}", mcp.getName())
										.replace("{player_receiver}", player.getName()));
							}
						}
					}
					mcp.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION.toString()
							.replace("{player_receiver}", player.getName()));
				}
			}
		}
		return;
	}
}