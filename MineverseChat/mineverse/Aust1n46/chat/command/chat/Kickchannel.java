package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Kickchannel extends MineverseCommand {
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Kickchannel(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.kickchannel")) {
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Invalid command: /kickchannel [player] [channelname]");
				return;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if(player == null) {
				sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
				return;
			}
			ChatChannel channel = cc.getChannelInfo(args[1]);
			if(channel == null) {
				sender.sendMessage(ChatColor.RED + "Invalid channel: " + args[1]);
				return;
			}			
			sender.sendMessage(ChatColor.GOLD + "Kicked player " + ChatColor.RED + args[0] + ChatColor.GOLD + " from channel: " + ChatColor.valueOf(channel.getColor().toUpperCase()) + channel.getName());
			String format = ChatColor.valueOf(channel.getColor().toUpperCase()) + "[" + channel.getName() + "] " + ChatColor.valueOf(channel.getColor().toUpperCase());
			player.removeListening(channel.getName());
			if(player.isOnline()) {
				player.getPlayer().sendMessage("Leaving Channel: " + format);
			}
			else 
				player.setModified(true);
			if(player.getListening().size() == 0) {
				player.setCurrentChannel(cc.getDefaultChannel());
				if(player.isOnline()) {
					player.getPlayer().sendMessage(ChatColor.RED + "You need to be listening on at least one channel, setting you into the default channel.");
					player.getPlayer().sendMessage("Channel Set: " + ChatColor.valueOf(cc.defaultColor.toUpperCase()) + "[" + cc.getDefaultChannel().getName() + "]");
				}
				else 
					player.setModified(true);
			}
			return;
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
	}
}