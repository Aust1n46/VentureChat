package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Setchannelall extends MineverseCommand {

	public Setchannelall(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.setchannelall")) {
			if(args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Invalid command: /setchannelall [player]");
				return;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if(player == null) {
				sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
				return;
			}
			for(ChatChannel channel : ChatChannel.getChannels()) {
				if(channel.hasPermission()) {
					if(!player.isOnline()) {
						sender.sendMessage(ChatColor.RED + "Can't run permission check on offline player.");
						return;
					}
					if(!player.getPlayer().hasPermission(channel.getPermission())) {
						player.removeListening(channel.getName());
					}
					else {
						player.addListening(channel.getName());
					}
				}
				else {
					player.addListening(channel.getName());
				}
			}
			sender.sendMessage(ChatColor.GOLD + "Set player " + ChatColor.RED + args[0] + ChatColor.GOLD + " into all channels.");
			if(player.isOnline()) 
				player.getPlayer().sendMessage(ChatColor.RED + "You have been set into all channels.");
			else
				player.setModified(true);
			return;
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
	}
}