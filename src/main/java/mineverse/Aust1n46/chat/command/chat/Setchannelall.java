package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Setchannelall extends Command {
	public Setchannelall() {
		super("setchannelall");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.setchannelall")) {
			if (args.length < 1) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/setchannelall").replace("{args}", "[player]"));
				return true;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return true;
			}
			boolean isThereABungeeChannel = false;
			for (ChatChannel channel : ChatChannel.getChatChannels()) {
				if (channel.hasPermission()) {
					if (!player.isOnline()) {
						sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE_NO_PERMISSIONS_CHECK.toString());
						return true;
					}
					if (!player.getPlayer().hasPermission(channel.getPermission())) {
						player.removeListening(channel.getName());
					} else {
						player.addListening(channel.getName());
					}
				} else {
					player.addListening(channel.getName());
				}
				if (channel.getBungee()) {
					isThereABungeeChannel = true;
				}
			}
			sender.sendMessage(LocalizedMessage.SET_CHANNEL_ALL_SENDER.toString().replace("{player}", player.getName()));
			if (player.isOnline())
				player.getPlayer().sendMessage(LocalizedMessage.SET_CHANNEL_ALL_PLAYER.toString());
			else
				player.setModified(true);
			if (isThereABungeeChannel) {
				MineverseChat.synchronize(player, true);
			}
			return true;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return true;
	}
}
