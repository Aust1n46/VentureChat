package mineverse.Aust1n46.chat.command.message;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Notifications extends MineverseCommand {
	private MineverseChat plugin;

	public Notifications(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This command must be run by a player.");
			return;
		}

		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if(!mcp.hasNotifications()) {
			mcp.setNotifications(true);
			mcp.getPlayer().sendMessage(ChatColor.GREEN + "You are now receiving notifications.");
			return;
		}
		mcp.setNotifications(false);
		mcp.getPlayer().sendMessage(ChatColor.GREEN + "You are no longer receiving notifications.");
		return;
	}
}