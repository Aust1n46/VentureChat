package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.utilities.Format;

public class Broadcast extends MineverseCommand {
	private MineverseChat plugin;
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Broadcast(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(cc.broadcastPermissions.equalsIgnoreCase("None") || sender.hasPermission(cc.broadcastPermissions)) {
			if(args.length > 0) {
				String bc = "";
				for(int x = 0; x < args.length; x++) {
					if(args[x].length() > 0) bc += args[x] + " ";
				}
				bc = Format.FormatStringAll(bc);
				plugin.getServer().broadcastMessage(cc.broadcastDisplayTag + ChatColor.valueOf(cc.broadcastColor.toUpperCase()) + " " + bc);
				return;
			}
			else {
				sender.sendMessage(ChatColor.RED + "Invalid command: /broadcast [msg]");
				return;
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			return;
		}
	}
}