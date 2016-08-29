package mineverse.Aust1n46.chat.command.chat;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RangedSpy extends MineverseCommand {
	private MineverseChat plugin;

	public RangedSpy(String name) {
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
		if(mcp.getPlayer().hasPermission("venturechat.rangedspy")) {
			if(!mcp.getRangedSpy()) {				
				mcp.setRangedSpy(true);
				mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are now spying on ranged channels.");
				return;
			}			
			mcp.setRangedSpy(false);
			mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer spying on ranged channels.");
			return;
		}
		mcp.getPlayer().sendMessage(ChatColor.RED + "You do not have permission for this command.");
		return;
	}
}