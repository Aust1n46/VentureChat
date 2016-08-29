package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Force extends MineverseCommand {

	public Force(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.force")) {
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Invalid command: /force [player] [msg]");
				return;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if(player == null || !player.isOnline()) {
				sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
				return;
			}
			String forcemsg = "";
			for(int x = 1; x < args.length; x++) 
				if(args[x].length() > 0) 
					forcemsg += args[x] + " ";
			sender.sendMessage(ChatColor.GOLD + "Forcing player " + ChatColor.RED + player.getName() + ChatColor.GOLD + " to run: " + ChatColor.RED + forcemsg);
			player.getPlayer().chat(forcemsg);
			return;
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
	}
}