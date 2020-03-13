package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Chlist extends MineverseCommand {

	public Chlist(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Channel List : Alias");
		for(ChatChannel chname : ChatChannel.getChannels()) {
			if(chname.hasPermission()) {
				if(sender.hasPermission(chname.getPermission())) {
					sender.sendMessage(ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.getName() + " : " + chname.getAlias() + " - Permission Required");
				}
			}
			else {
				sender.sendMessage(ChatColor.valueOf(chname.getColor().toUpperCase()) + chname.getName() + " : " + chname.getAlias());
			}
		}
		return;
	}
}