package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class BungeeToggle extends MineverseCommand {
	private MineverseChat plugin;

	public BungeeToggle(String name) {
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
		if(mcp.getPlayer().hasPermission("venturechat.bungeetoggle")) {
			if(!mcp.getBungeeToggle()) {				
				mcp.setBungeeToggle(true);
				mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are now receiving BungeeCord chat.");
				MineverseChat.getInstance().synchronize(mcp, true);
				return;
			}			
			mcp.setBungeeToggle(false);
			mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are now blocking BungeeCord chat.");
			MineverseChat.getInstance().synchronize(mcp, true);
			return;
		}
		mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command.");
		return;
	}
}