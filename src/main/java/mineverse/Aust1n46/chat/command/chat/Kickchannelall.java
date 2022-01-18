package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Kickchannelall extends Command {
	public Kickchannelall() {
		super("kickchannelall");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.kickchannelall")) {
			if (args.length < 1) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/kickchannelall").replace("{args}", "[player]"));
				return true;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return true;
			}
			boolean isThereABungeeChannel = false;
			for (String channel : player.getListening()) {
				if (ChatChannel.isChannel(channel)) {
					ChatChannel chatChannelObj = ChatChannel.getChannel(channel);
					if (chatChannelObj.getBungee()) {
						isThereABungeeChannel = true;
					}
				}
			}
			player.clearListening();
			sender.sendMessage(LocalizedMessage.KICK_CHANNEL_ALL_SENDER.toString().replace("{player}", player.getName()));
			player.addListening(ChatChannel.getDefaultChannel().getName());
			player.setCurrentChannel(ChatChannel.getDefaultChannel());
			if (ChatChannel.getDefaultChannel().getBungee()) {
				isThereABungeeChannel = true;
			}
			if (isThereABungeeChannel) {
				MineverseChat.synchronize(player, true);
			}
			if (player.isOnline()) {
				player.getPlayer().sendMessage(LocalizedMessage.KICK_CHANNEL_ALL_PLAYER.toString());
				player.getPlayer().sendMessage(LocalizedMessage.MUST_LISTEN_ONE_CHANNEL.toString());
				player.getPlayer()
						.sendMessage(LocalizedMessage.SET_CHANNEL.toString().replace("{channel_color}", ChatColor.valueOf(ChatChannel.getDefaultColor().toUpperCase()) + "")
								.replace("{channel_name}", ChatChannel.getDefaultChannel().getName()));
			} else {
				player.setModified(true);
			}
			return true;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return true;
	}
}
