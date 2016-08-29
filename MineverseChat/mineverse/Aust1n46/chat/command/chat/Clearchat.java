package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Clearchat extends MineverseCommand {

	public Clearchat(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.clearchat")) {
			for(MineverseChatPlayer player : MineverseChat.players) {
				if(player.isOnline() && !player.getPlayer().hasPermission("venturechat.clearchat.bypass")) {
					for(int a = 1; a <= 20; a++)
						player.getPlayer().sendMessage("");
					player.getPlayer().sendMessage(ChatColor.GREEN + "Your chat has been cleared.");
				}
			}
			sender.sendMessage(ChatColor.GREEN + "Cleared the server chat.");
			return;
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
		return;	
	}
}