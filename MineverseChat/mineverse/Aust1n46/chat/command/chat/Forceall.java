package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Forceall extends MineverseCommand {

	public Forceall(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.forceall")) {
			if(args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Invalid command: /forceall [msg]");
				return;
			}
			String forcemsg = "";
			for(int x = 0; x < args.length; x++) 
				if(args[x].length() > 0) 
					forcemsg += args[x] + " ";
			sender.sendMessage(ChatColor.GOLD + "Forcing all players to run: " + ChatColor.RED + forcemsg);
			for(MineverseChatPlayer player : MineverseChat.players) 
				if(player.isOnline())
					player.getPlayer().chat(forcemsg);
			return;
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
	}
}