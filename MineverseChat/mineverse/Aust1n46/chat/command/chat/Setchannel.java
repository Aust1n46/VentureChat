package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Setchannel extends MineverseCommand {

	public Setchannel(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.setchannel")) {
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Invalid command: /setchannel [player] [channelname]");
				return;
			}
			if(args[0].length() > 1) {
				ChatChannel channel = ChatChannel.getChannel(args[1]);
				MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
				if(player == null) {
					sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
					return;
				}
				if(channel == null) {
					sender.sendMessage(ChatColor.RED + "Invalid channel: " + args[1]);
					return;
				}
				if(channel.hasPermission()) {
					if(!player.isOnline()) {
						sender.sendMessage(ChatColor.RED + "Can't run permission check on offline player.");
						return;
					}
					if(!player.getPlayer().hasPermission(channel.getPermission())) {
						player.removeListening(channel.getName());
						sender.sendMessage(ChatColor.RED + "This player does not have permission for channel: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
						return;
					}
				}				
				player.addListening(channel.getName());
				player.setCurrentChannel(channel);
				sender.sendMessage(ChatColor.GOLD + "Set player " + ChatColor.RED + player.getName() + ChatColor.GOLD + " into channel: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
				if(player.hasConversation()) {					
					for(MineverseChatPlayer p : MineverseChat.onlinePlayers) {
						if(p.isSpy()) {
							p.getPlayer().sendMessage(player.getName() + " is no longer in a private conversation with " + MineverseChatAPI.getMineverseChatPlayer(player.getConversation()).getName() + ".");
						}
					}
					if(player.isOnline()) 
						player.getPlayer().sendMessage("You are no longer in private conversation with " + MineverseChatAPI.getMineverseChatPlayer(player.getConversation()).getName() + ".");
					else 
						player.setModified(true);
					player.setConversation(null);
				}
				String format = ChatColor.valueOf(channel.getColor().toUpperCase()) + "[" + channel.getName() + "] " + ChatColor.valueOf(channel.getColor().toUpperCase());
				if(player.isOnline()) 
					player.getPlayer().sendMessage("Channel Set: " + format);
				else 
					player.setModified(true);
				return;
			}
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
	}
}