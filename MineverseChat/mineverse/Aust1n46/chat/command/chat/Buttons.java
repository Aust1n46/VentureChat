package mineverse.Aust1n46.chat.command.chat;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Buttons extends MineverseCommand {
	private MineverseChat plugin;

	public Buttons(String name) {
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
		if(mcp.getPlayer().hasPermission("venturechat.ignorebutton") || mcp.getPlayer().hasPermission("venturechat.ignorebutton")) {
			if(mcp.getButtons()) {
				mcp.setButtons(false);
				mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer ignoring json buttons.");
				return;
			}			
			mcp.setButtons(true);
			mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are now ignoring json buttons.");
			return;
		}
		mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command.");
	}
}