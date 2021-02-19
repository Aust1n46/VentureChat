package mineverse.Aust1n46.chat.command.mute;

import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;

public class Mute extends MineverseCommand {

	public Mute(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.mute")) {
			if (args.length < 2) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/mute")
						.replace("{args}", "[player] [channel] {time}"));
				return;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if (player == null || (!player.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return;
			}
			if (args.length == 2) {
				if (ChatChannel.isChannel(args[1])) {
					ChatChannel channel = ChatChannel.getChannel(args[1]);
					if (player.isMuted(channel.getName())) {
						sender.sendMessage(LocalizedMessage.PLAYER_ALREADY_MUTED.toString()
								.replace("{player}", player.getName()).replace("{channel_color}", channel.getColor())
								.replace("{channel_name}", channel.getName()));
						return;
					}
					if (channel.isMutable()) {
						player.addMute(channel.getName(), 0);
						sender.sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER.toString()
								.replace("{player}", player.getName()).replace("{channel_color}", channel.getColor())
								.replace("{channel_name}", channel.getName()));
						if (player.isOnline())
							player.getPlayer()
									.sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER.toString()
											.replace("{channel_color}", channel.getColor())
											.replace("{channel_name}", channel.getName()));
						else
							player.setModified(true);
						if (channel.getBungee()) {
							MineverseChat.getInstance().synchronize(player, true);
						}
						return;
					}
					sender.sendMessage(LocalizedMessage.CHANNEL_CANNOT_MUTE.toString()
							.replace("{channel_color}", channel.getColor())
							.replace("{channel_name}", channel.getName()));
					return;
				}
				sender.sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[1]));
				return;
			}
			if (ChatChannel.isChannel(args[1])) {
				ChatChannel channel = ChatChannel.getChannel(args[1]);
				if (player.isMuted(channel.getName())) {
					sender.sendMessage(LocalizedMessage.PLAYER_ALREADY_MUTED.toString()
							.replace("{player}", player.getName()).replace("{channel_color}", channel.getColor())
							.replace("{channel_name}", channel.getName()));
					return;
				}
				if (channel.isMutable()) {
					try {
						long datetime = System.currentTimeMillis();
						long time = Format.parseTimeStringToMillis(args[2]);
						if (time > 0) {
							player.addMute(channel.getName(), datetime + time);
							String timeString = Format.parseTimeStringFromMillis(time);
							sender.sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME.toString()
									.replace("{player}", player.getName())
									.replace("{channel_color}", channel.getColor())
									.replace("{channel_name}", channel.getName())
									.replace("{time}", timeString));
							if (player.isOnline())
								player.getPlayer()
										.sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER_TIME.toString()
												.replace("{channel_color}", channel.getColor())
												.replace("{channel_name}", channel.getName())
												.replace("{time}", timeString));
							else
								player.setModified(true);
							if (channel.getBungee()) {
								MineverseChat.getInstance().synchronize(player, true);
							}
							return;
						}
						sender.sendMessage(LocalizedMessage.INVALID_TIME.toString().replace("{args}", args[2]));
					} catch (Exception e) {
						sender.sendMessage(LocalizedMessage.INVALID_TIME.toString().replace("{args}", args[2]));
					}
					return;
				}
				sender.sendMessage(LocalizedMessage.CHANNEL_CANNOT_MUTE.toString()
						.replace("{channel_color}", channel.getColor()).replace("{channel_name}", channel.getName()));
				return;
			}
			sender.sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[1]));
			return;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
	}
}
