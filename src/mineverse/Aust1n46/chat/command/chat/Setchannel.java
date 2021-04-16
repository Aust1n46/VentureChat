package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Setchannel extends MineverseCommand {

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.setchannel")) {
			if(args.length < 2) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
						.replace("{command}", "/setchannel")
						.replace("{args}", "[player] [channel]"));
				return;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if(player == null) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
						.replace("{args}", args[0]));
				return;
			}
			ChatChannel channel = ChatChannel.getChannel(args[1]);
			if(channel == null) {
				sender.sendMessage(LocalizedMessage.INVALID_CHANNEL.toString()
						.replace("{args}", args[1]));
				return;
			}
			if(channel.hasPermission()) {
				if(!player.isOnline()) {
					sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE_NO_PERMISSIONS_CHECK.toString());
					return;
				}
				if(!player.getPlayer().hasPermission(channel.getPermission())) {
					player.removeListening(channel.getName());
					sender.sendMessage(LocalizedMessage.SET_CHANNEL_PLAYER_CHANNEL_NO_PERMISSION.toString()
							.replace("{player}", player.getName())
							.replace("{channel_color}", channel.getColor() + "")
							.replace("{channel_name}", channel.getName()));
					return;
				}
			}				
			player.addListening(channel.getName());
			player.setCurrentChannel(channel);
			sender.sendMessage(LocalizedMessage.SET_CHANNEL_SENDER.toString()
					.replace("{player}", player.getName())
					.replace("{channel_color}", channel.getColor() + "")
					.replace("{channel_name}", channel.getName()));
			if(player.hasConversation()) {					
				for(MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
					if(p.isSpy()) {
						p.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION_SPY.toString()
								.replace("{player_sender}", player.getName())
								.replace("{player_receiver}", MineverseChatAPI.getMineverseChatPlayer(player.getConversation()).getName()));
					}
				}
				if(player.isOnline()) 
					player.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION.toString()
							.replace("{player_receiver}", MineverseChatAPI.getMineverseChatPlayer(player.getConversation()).getName()));
				else 
					player.setModified(true);
				player.setConversation(null);
			}
			if(player.isOnline()) 
				player.getPlayer().sendMessage(LocalizedMessage.SET_CHANNEL.toString()
						.replace("{channel_color}", channel.getColor() + "")
						.replace("{channel_name}", channel.getName()));
			else 
				player.setModified(true);
			return;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
	}
}
