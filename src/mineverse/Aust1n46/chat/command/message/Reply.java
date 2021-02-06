package mineverse.Aust1n46.chat.command.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;
import mineverse.Aust1n46.chat.versions.VersionHandler;

public class Reply extends MineverseCommand {
	private MineverseChat plugin = MineverseChat.getInstance();

	public Reply(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if(args.length > 0) {
			if(mcp.hasReplyPlayer()) {
				MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(mcp.getReplyPlayer());
				
				if(plugin.getConfig().getBoolean("bungeecordmessaging", true)) {
					sendBungeeCordReply(mcp, player, args);
					return;
				}
				
				if(player == null || !player.isOnline()) {
					mcp.getPlayer().sendMessage(LocalizedMessage.NO_PLAYER_TO_REPLY_TO.toString());
					return;
				}
				if(!mcp.getPlayer().canSee(player.getPlayer())) {
					mcp.getPlayer().sendMessage(LocalizedMessage.NO_PLAYER_TO_REPLY_TO.toString());
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
				String msg = "";
				String echo = "";
				String send = "";
				String spy = "";
				String tellColor = plugin.getConfig().getString("tellcolor", "gray");
				if(args.length > 0) {
					for(int r = 0; r < args.length; r++)
						msg += " " + args[r];
					if(mcp.hasFilter()) {
						msg = Format.FilterChat(msg);
					}
					if(mcp.getPlayer().hasPermission("venturechat.color.legacy")) {
						msg = Format.FormatStringLegacyColor(msg);
					}
					if(mcp.getPlayer().hasPermission("venturechat.color")) {
						msg = Format.FormatStringColor(msg);
					}
					if(mcp.getPlayer().hasPermission("venturechat.format")) {
						msg = Format.FormatString(msg);
					}
					if(plugin.getConfig().getString("replyformatto").equalsIgnoreCase("Default")) {
						echo = "You reply to " + player.getNickname() + ":" + ChatColor.valueOf(tellColor.toUpperCase()) + msg;
					}
					else {
						echo = Format.FormatStringAll(plugin.getConfig().getString("replyformatto").replace("{playerto}", player.getNickname()).replace("{playerfrom}", mcp.getNickname())) + msg;
					}
					if(plugin.getConfig().getString("replyformatfrom").equalsIgnoreCase("Default")) {
						send = mcp.getNickname() + " replies to you:" + ChatColor.valueOf(tellColor.toUpperCase()) + msg;
					}
					else {
						send = Format.FormatStringAll(plugin.getConfig().getString("replyformatfrom").replace("{playerto}", player.getNickname()).replace("{playerfrom}", mcp.getNickname())) + msg;
					}
					if(plugin.getConfig().getString("replyformatspy").equalsIgnoreCase("Default")) {
						spy = mcp.getName() + " replied to " + player.getName() + ":" + ChatColor.valueOf(tellColor.toUpperCase()) + msg;
					}
					else {
						spy = Format.FormatStringAll(plugin.getConfig().getString("replyformatspy").replace("{playerto}", player.getName()).replace("{playerfrom}", mcp.getName())) + msg;
					}
					if(!mcp.getPlayer().hasPermission("venturechat.spy.override")) {
						for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
							if(p.isOnline() && p.isSpy()) {
								p.getPlayer().sendMessage(spy);
							}
						}
					}
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
					player.setReplyPlayer(mcp.getUUID());
					return;
				}
			}
			mcp.getPlayer().sendMessage(LocalizedMessage.NO_PLAYER_TO_REPLY_TO.toString());
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
				.replace("{command}", "/reply")
				.replace("{args}", "[message]"));
	}
	
	private void sendBungeeCordReply(MineverseChatPlayer mcp, MineverseChatPlayer player, String[] args) {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutStream);
		String msg = "";
		String send = "";
		String echo = "";
		String spy = "";
		for(int r = 0; r < args.length; r++) {
			msg += " " + args[r];
		}
		send = Format.FormatStringAll(plugin.getConfig().getString("replyformatfrom")) + msg;
		echo = Format.FormatStringAll(plugin.getConfig().getString("replyformatto")) + msg;
		spy = Format.FormatStringAll(plugin.getConfig().getString("replyformatspy")) + msg;
		
		send = PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), send.replaceAll("sender_", ""));
		echo = PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), echo.replaceAll("sender_", ""));
		spy = PlaceholderAPI.setBracketPlaceholders(mcp.getPlayer(), spy.replaceAll("sender_", ""));
		try {
			out.writeUTF("Message");
			out.writeUTF("Send");
			out.writeUTF(player.getName());
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
	}
}