package mineverse.Aust1n46.chat.command.mute;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import mineverse.Aust1n46.chat.utilities.Format;

public class Mute extends Command {
	public Mute() {
		super("mute");
	}

	private static final List<String> COMMON_MUTE_TIMES = Collections.unmodifiableList(Arrays.asList(new String[] { "12h", "15m", "1d", "1h", "1m", "30s" }));

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.mute")) {
			if (args.length < 2) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/mute").replace("{args}", "[channel] [player] {time} {reason}"));
				return true;
			}
			if (ChatChannel.isChannel(args[0])) {
				ChatChannel channel = ChatChannel.getChannel(args[0]);
				if (channel.isMutable()) {
					long datetime = System.currentTimeMillis();
					long time = 0;
					int reasonStartPos = 2;
					String reason = "";
					if (args.length > 2) {
						String timeString = args[2];
						if (Character.isDigit(timeString.charAt(0))) {
							reasonStartPos = 3;
							time = Format.parseTimeStringToMillis(timeString);
							if (time <= 0) {
								sender.sendMessage(LocalizedMessage.INVALID_TIME.toString().replace("{args}", timeString));
								return true;
							}
						}
						StringBuilder reasonBuilder = new StringBuilder();
						for (int a = reasonStartPos; a < args.length; a++) {
							reasonBuilder.append(args[a] + " ");
						}
						reason = Format.FormatStringAll(reasonBuilder.toString().trim());
					}
					if (channel.getBungee()) {
						sendBungeeCordMute(sender, args[1], channel, time, reason);
						return true;
					}
					MineverseChatPlayer playerToMute = MineverseChatAPI.getMineverseChatPlayer(args[1]);
					if (playerToMute == null || (!playerToMute.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
						sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[1]));
						return true;
					}
					if (playerToMute.isMuted(channel.getName())) {
						sender.sendMessage(LocalizedMessage.PLAYER_ALREADY_MUTED.toString().replace("{player}", playerToMute.getName())
								.replace("{channel_color}", channel.getColor()).replace("{channel_name}", channel.getName()));
						return true;
					}

					if (time > 0) {
						if (reason.isEmpty()) {
							playerToMute.addMute(channel.getName(), datetime + time);
							String timeString = Format.parseTimeStringFromMillis(time);
							sender.sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME.toString().replace("{player}", playerToMute.getName())
									.replace("{channel_color}", channel.getColor()).replace("{channel_name}", channel.getName()).replace("{time}", timeString));
							if (playerToMute.isOnline()) {
								playerToMute.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER_TIME.toString().replace("{channel_color}", channel.getColor())
										.replace("{channel_name}", channel.getName()).replace("{time}", timeString));
							} else {
								playerToMute.setModified(true);
							}
							return true;
						} else {
							playerToMute.addMute(channel.getName(), datetime + time, reason);
							String timeString = Format.parseTimeStringFromMillis(time);
							sender.sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME_REASON.toString().replace("{player}", playerToMute.getName())
									.replace("{channel_color}", channel.getColor()).replace("{channel_name}", channel.getName()).replace("{time}", timeString)
									.replace("{reason}", reason));
							if (playerToMute.isOnline()) {
								playerToMute.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER_TIME_REASON.toString().replace("{channel_color}", channel.getColor())
										.replace("{channel_name}", channel.getName()).replace("{time}", timeString).replace("{reason}", reason));
							} else {
								playerToMute.setModified(true);
							}
							return true;
						}
					} else {
						if (reason.isEmpty()) {
							playerToMute.addMute(channel.getName());
							sender.sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER.toString().replace("{player}", playerToMute.getName())
									.replace("{channel_color}", channel.getColor()).replace("{channel_name}", channel.getName()));
							if (playerToMute.isOnline()) {
								playerToMute.getPlayer().sendMessage(
										LocalizedMessage.MUTE_PLAYER_PLAYER.toString().replace("{channel_color}", channel.getColor()).replace("{channel_name}", channel.getName()));
							} else {
								playerToMute.setModified(true);
							}
							return true;
						} else {
							playerToMute.addMute(channel.getName(), reason);
							sender.sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_REASON.toString().replace("{player}", playerToMute.getName())
									.replace("{channel_color}", channel.getColor()).replace("{channel_name}", channel.getName()).replace("{reason}", reason));
							if (playerToMute.isOnline()) {
								playerToMute.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER_REASON.toString().replace("{channel_color}", channel.getColor())
										.replace("{channel_name}", channel.getName()).replace("{reason}", reason));
							} else {
								playerToMute.setModified(true);
							}
							return true;
						}
					}
				}
				sender.sendMessage(LocalizedMessage.CHANNEL_CANNOT_MUTE.toString().replace("{channel_color}", channel.getColor()).replace("{channel_name}", channel.getName()));
				return true;
			}
			sender.sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[0]));
			return true;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return true;
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
				StringUtil.copyPartialMatches(args[1], MineverseChatAPI.getOnlineMineverseChatPlayers().stream().filter(mcp -> !mcp.isMuted(chatChannelObj.getName()))
						.map(MineverseChatPlayer::getName).collect(Collectors.toList()), completions);
				Collections.sort(completions);
				return completions;
			}
		}
		if (args.length == 3) {
			StringUtil.copyPartialMatches(args[2], COMMON_MUTE_TIMES, completions);
			Collections.sort(completions);
			return completions;

		}
		return Collections.emptyList();
	}

	private void sendBungeeCordMute(CommandSender sender, String playerToMute, ChatChannel channel, long time, String reason) {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutStream);
		try {
			out.writeUTF("Mute");
			out.writeUTF("Send");
			if (sender instanceof Player) {
				out.writeUTF(((Player) sender).getUniqueId().toString());
			} else {
				out.writeUTF("VentureChat:Console");
			}
			out.writeUTF(playerToMute);
			out.writeUTF(channel.getName());
			out.writeLong(time);
			out.writeUTF(reason);
			MineverseChat.sendPluginMessage(byteOutStream);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
