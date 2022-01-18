package mineverse.Aust1n46.chat.command.mute;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Unmuteall extends Command {
	public Unmuteall() {
		super("unmuteall");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.mute")) {
			if (args.length < 1) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/unmuteall").replace("{args}", "[player]"));
				return true;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if (player == null || (!player.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return true;
			}
			boolean bungee = false;
			for (ChatChannel channel : ChatChannel.getChatChannels()) {
				player.removeMute(channel.getName());
				if (channel.getBungee()) {
					bungee = true;
				}
			}
			if (bungee) {
				MineverseChat.synchronize(player, true);
			}
			sender.sendMessage(LocalizedMessage.UNMUTE_PLAYER_ALL_SENDER.toString().replace("{player}", player.getName()));
			if (player.isOnline()) {
				player.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_ALL_PLAYER.toString());
			} else
				player.setModified(true);
			return true;
		} else {
			sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
			return true;
		}
	}
}
