package mineverse.Aust1n46.chat.command.mute;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Unmute extends Command {
	public Unmute() {
		super("unmute");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.mute")) {
			if (args.length < 2) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/unmute").replace("{args}", "[channel] [player]"));
				return true;
			}
			if (ChatChannel.isChannel(args[0])) {
				ChatChannel channel = ChatChannel.getChannel(args[0]);
				if (channel.getBungee()) {
					sendBungeeCordUnmute(sender, args[1], channel);
					return true;
				}
				MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[1]);
				if (player == null || (!player.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
					sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[1]));
					return true;
				}
				if (!player.isMuted(channel.getName())) {
					sender.sendMessage(LocalizedMessage.PLAYER_NOT_MUTED.toString().replace("{player}", player.getName()).replace("{channel_color}", channel.getColor())
							.replace("{channel_name}", channel.getName()));
					return true;
				}
				player.removeMute(channel.getName());
				sender.sendMessage(LocalizedMessage.UNMUTE_PLAYER_SENDER.toString().replace("{player}", player.getName()).replace("{channel_color}", channel.getColor())
						.replace("{channel_name}", channel.getName()));
				if (player.isOnline()) {
					player.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_PLAYER.toString().replace("{player}", player.getName())
							.replace("{channel_color}", channel.getColor()).replace("{channel_name}", channel.getName()));
				} else {
					player.setModified(true);
				}
				return true;
			}
			sender.sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[0]));
			return true;
		} else {
			sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
			return true;
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		if (args.length == 1) {
			StringUtil.copyPartialMatches(args[0], ChatChannel.getChatChannels().stream().map(ChatChannel::getName).collect(Collectors.toList()), completions);
			Collections.sort(completions);
			return completions;
		}
		if (args.length == 2) {
			if (ChatChannel.isChannel(args[0])) {
				ChatChannel chatChannelObj = ChatChannel.getChannel(args[0]);
				if (chatChannelObj.getBungee()) {
					StringUtil.copyPartialMatches(args[1], MineverseChatAPI.getNetworkPlayerNames(), completions);
					Collections.sort(completions);
					return completions;
				}
				StringUtil.copyPartialMatches(args[1], MineverseChatAPI.getOnlineMineverseChatPlayers().stream().filter(mcp -> mcp.isMuted(chatChannelObj.getName()))
						.map(MineverseChatPlayer::getName).collect(Collectors.toList()), completions);
				Collections.sort(completions);
				return completions;
			}
		}
		return Collections.emptyList();
	}

	private void sendBungeeCordUnmute(CommandSender sender, String playerToUnmute, ChatChannel channel) {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutStream);
		try {
			out.writeUTF("Unmute");
			out.writeUTF("Send");
			if (sender instanceof Player) {
				out.writeUTF(((Player) sender).getUniqueId().toString());
			} else {
				out.writeUTF("VentureChat:Console");
			}
			out.writeUTF(playerToUnmute);
			out.writeUTF(channel.getName());
			MineverseChat.sendPluginMessage(byteOutStream);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
