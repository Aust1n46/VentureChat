package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Channelinfo extends MineverseCommand {

	public Channelinfo(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.channelinfo")) {
			if(args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Invalid command: /channelinfo [channel]");
				return;
			}
			ChatChannel chname = ChatChannel.getChannel(args[0]);
			if(chname == null) {
				sender.sendMessage(ChatColor.RED + "Invalid channel: " + args[0]);
				return;
			}
			if(chname.hasPermission()) {
				if(!sender.hasPermission(chname.getPermission())) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to look at this channel.");
					return;
				}
			}
			sender.sendMessage(ChatColor.GOLD + "Channel: " + ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.getName());
			sender.sendMessage(ChatColor.GOLD + "Alias: " + ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.getAlias());
			sender.sendMessage(ChatColor.GOLD + "Color: " + ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.getColor());
			sender.sendMessage(ChatColor.GOLD + "ChatColor: " + ChatColor.valueOf(chname.getChatColor().toUpperCase()) + chname.getChatColor());
			sender.sendMessage(ChatColor.GOLD + "Permission: " + ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.getPermission());
			sender.sendMessage(ChatColor.GOLD + "Autojoin: " + ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.getAutojoin());
			sender.sendMessage(ChatColor.GOLD + "Default: " + ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.hasDistance());
			if(!chname.hasDistance() || chname.getBungee()) {
				sender.sendMessage(ChatColor.GOLD + "Distance: " + ChatColor.RED + "N/A");
			}
			else {
				sender.sendMessage(ChatColor.GOLD + "Distance: " + ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.getDistance().toString());
			}
			if(!chname.hasCooldown()) {
				sender.sendMessage(ChatColor.GOLD + "Cooldown: " + ChatColor.RED + "N/A");
			}
			else {
				sender.sendMessage(ChatColor.GOLD + "Cooldown: " + ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.getCooldown());
			}
			sender.sendMessage(ChatColor.GOLD + "Bungeecord: " + ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.getBungee().toString());
			sender.sendMessage(ChatColor.GOLD + "Format: " + ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.getFormat());
			return;
		}
		else {
			sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			return;
		}
	}
}