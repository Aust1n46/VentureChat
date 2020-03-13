package mineverse.Aust1n46.chat.command.chat;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Commandblock extends MineverseCommand {
	private MineverseChat plugin;

	public Commandblock(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.commandblock")) {
			if(args.length > 1) {
				MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
				if(player == null) {
					sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
					return;
				}
				boolean match = false;
				for(String cb : (List<String>) plugin.getConfig().getStringList("blockablecommands")) 
					if(args[1].equals("/" + cb)) 
						match = true;
				if(match || player.isBlockedCommand(args[1])) {
					if(!player.isBlockedCommand(args[1])) {
						player.addBlockedCommand(args[1]);
						player.getPlayer().sendMessage(ChatColor.RED + "You have been blocked from entering command " + args[1] + ".");
						sender.sendMessage(ChatColor.RED + "Blocked player " + ChatColor.GOLD + player.getName() + ChatColor.RED + " from entering command " + args[1] + ".");
						return;
					}
					player.removeBlockedCommand(args[1]);
					player.getPlayer().sendMessage(ChatColor.RED + "You have been unblocked from entering command " + args[1] + ".");
					sender.sendMessage(ChatColor.RED + "Unblocked player " + ChatColor.GOLD + player.getName() + ChatColor.RED + " from entering command " + args[1] + ".");
					return;
				}
				sender.sendMessage(ChatColor.RED + "Invalid command or the command is not blockable.");
				return;
			}
			sender.sendMessage(ChatColor.RED + "Invalid command: /commandblock [player] [command]");
			return;
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
	}
}