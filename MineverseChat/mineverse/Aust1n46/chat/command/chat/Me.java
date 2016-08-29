package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.utilities.Format;

public class Me extends MineverseCommand {
	private MineverseChat plugin;
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Me(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.me")) {
			if(args.length > 0) {
				String msg = "";
				for(int x = 0; x < args.length; x++) 
					if(args[x].length() > 0) 
						msg += " " + args[x];				
				if(sender.hasPermission("venturechat.color")) 
					msg = Format.FormatStringColor(msg);
				if(sender.hasPermission("venturechat.format")) 
					msg = Format.FormatString(msg);
				String filtered = cc.FilterChat(msg);
				if(sender instanceof Player && MineverseChatAPI.getMineverseChatPlayer((Player) sender).hasFilter()) {
					Player p = (Player) sender;
					plugin.getServer().broadcastMessage("* " +p.getDisplayName() + filtered);
					return;
				}
				if(sender instanceof Player) {
					Player p = (Player) sender;
					plugin.getServer().broadcastMessage("* " + p.getDisplayName() + msg);
					return;
				}
				plugin.getServer().broadcastMessage("* " + sender.getName() + msg);
				return;
			}
			sender.sendMessage(ChatColor.RED + "Invalid command: /me [msg]");
			return;
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
	}
}