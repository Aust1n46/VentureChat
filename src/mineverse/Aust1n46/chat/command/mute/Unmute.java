package mineverse.Aust1n46.chat.command.mute;

import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Unmute extends MineverseCommand {

	public Unmute(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.mute")) {
			if (args.length < 2) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/unmute")
						.replace("{args}", "[player] [channel]"));
				return;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if (player == null || (!player.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return;
			}
			for (ChatChannel channel : ChatChannel.getChannels()) {
				if (channel.getName().equalsIgnoreCase(args[1]) || channel.getAlias().equalsIgnoreCase(args[1])) {
					if (!player.isMuted(channel.getName())) {
						sender.sendMessage(LocalizedMessage.PLAYER_NOT_MUTED.toString()
								.replace("{player}", player.getName()).replace("{channel_color}", channel.getColor())
								.replace("{channel_name}", channel.getName()));
						return;
					}
					player.removeMute(channel.getName());
					sender.sendMessage(LocalizedMessage.UNMUTE_PLAYER_SENDER.toString()
							.replace("{player}", player.getName()).replace("{channel_color}", channel.getColor())
							.replace("{channel_name}", channel.getName()));
					if (player.isOnline()) {
						player.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_PLAYER.toString()
								.replace("{player}", player.getName()).replace("{channel_color}", channel.getColor())
								.replace("{channel_name}", channel.getName()));
					} else {
						player.setModified(true);
					}
					if (channel.getBungee()) {
						MineverseChat.getInstance().synchronize(player, true);
					}
					return;
				}
			}
			sender.sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[1]));
			return;
		} else {
			sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
			return;
		}
	}
}