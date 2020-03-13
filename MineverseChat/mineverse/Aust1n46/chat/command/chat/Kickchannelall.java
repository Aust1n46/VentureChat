package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Kickchannelall extends MineverseCommand {

	public Kickchannelall(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.kickchannelall")) {
			if(args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Invalid command: /kickchannelall [player]");
				return;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if(player == null) {
				sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
				return;
			}
			player.clearListening();
			sender.sendMessage(ChatColor.GOLD + "Kicked player " + ChatColor.RED + player.getName() + ChatColor.GOLD + " from all channels.");
			player.addListening(ChatChannel.getDefaultChannel().getName());
			player.setCurrentChannel(ChatChannel.getDefaultChannel());
			if(player.isOnline()) {
				player.getPlayer().sendMessage(ChatColor.RED + "You have been kicked from all channels.");
				player.getPlayer().sendMessage(ChatColor.RED + "You need to be listening on at least one channel, setting you into the default channel.");
				player.getPlayer().sendMessage("Channel Set: " + ChatColor.valueOf(ChatChannel.getDefaultColor().toUpperCase()) + "[" + ChatChannel.getDefaultChannel().getName() + "]");
			}
			else 
				player.setModified(true);
			return;
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
	}
}