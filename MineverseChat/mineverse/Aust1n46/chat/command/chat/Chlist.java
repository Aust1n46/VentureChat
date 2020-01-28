package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Chlist extends MineverseCommand {
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Chlist(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		sender.sendMessage(LocalizedMessage.CHANNEL_LIST_HEADER.toString());
		for(ChatChannel chname : cc.getChannelsInfo()) {
			if(chname.hasPermission()) {
				if(sender.hasPermission(chname.getPermission())) {
					sender.sendMessage(LocalizedMessage.CHANNEL_LIST_WITH_PERMISSIONS.toString()
							.replace("{channel_color}", (ChatColor.valueOf(chname.getColor().toUpperCase())).toString())
							.replace("{channel_name}", chname.getName())
							.replace("{channel_alias}", chname.getAlias()));
				}
			}
			else {
				sender.sendMessage(LocalizedMessage.CHANNEL_LIST.toString()
						.replace("{channel_color}", (ChatColor.valueOf(chname.getColor().toUpperCase())).toString())
						.replace("{channel_name}", chname.getName())
						.replace("{channel_alias}", chname.getAlias()));
			}
		}
		return;
	}
}