package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Kickchannel extends Command {
	public Kickchannel() {
		super("kickchannel");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.kickchannel")) {
			if (args.length < 2) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/kickchannel").replace("{args}", "[player] [channel]"));
				return true;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return true;
			}
			ChatChannel channel = ChatChannel.getChannel(args[1]);
			if (channel == null) {
				sender.sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[1]));
				return true;
			}
			sender.sendMessage(LocalizedMessage.KICK_CHANNEL.toString().replace("{player}", args[0]).replace("{channel_color}", channel.getColor() + "").replace("{channel_name}",
					channel.getName()));
			player.removeListening(channel.getName());
			if (player.isOnline()) {
				player.getPlayer()
						.sendMessage(LocalizedMessage.LEAVE_CHANNEL.toString().replace("{channel_color}", channel.getColor() + "").replace("{channel_name}", channel.getName()));
			} else {
				player.setModified(true);
			}
			boolean isThereABungeeChannel = channel.getBungee();
			if (player.getListening().size() == 0) {
				player.addListening(ChatChannel.getDefaultChannel().getName());
				player.setCurrentChannel(ChatChannel.getDefaultChannel());
				if (ChatChannel.getDefaultChannel().getBungee()) {
					isThereABungeeChannel = true;
				}
				if (player.isOnline()) {
					player.getPlayer().sendMessage(LocalizedMessage.MUST_LISTEN_ONE_CHANNEL.toString());
					player.getPlayer()
							.sendMessage(LocalizedMessage.SET_CHANNEL.toString().replace("{channel_color}", ChatColor.valueOf(ChatChannel.getDefaultColor().toUpperCase()) + "")
									.replace("{channel_name}", ChatChannel.getDefaultChannel().getName()));
				} else
					player.setModified(true);
			}
			if (isThereABungeeChannel) {
				MineverseChat.synchronize(player, true);
			}
			return true;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return true;
	}
}
