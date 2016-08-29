package mineverse.Aust1n46.chat.command.message;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Afk extends MineverseCommand {
	private MineverseChat plugin;

	public Afk(String name) {
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
		if(args.length >= 0) {
			if(!mcp.isAFK()) {
				mcp.setAFK(true);
				mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are now AFK.");				
				if(plugin.getConfig().getBoolean("broadcastafk")) {
					for(MineverseChatPlayer p : MineverseChat.players) {
						if(p.isOnline() && !p.getName().equals(mcp.getName())) {
							p.getPlayer().sendMessage(ChatColor.GOLD + mcp.getName() + " is now AFK.");
						}
					}
				}
				return;
			}
			else {
				mcp.setAFK(false);
				mcp.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer AFK.");
				if(plugin.getConfig().getBoolean("broadcastafk")) {
					for(MineverseChatPlayer p : MineverseChat.players) {
						if(p.isOnline() && !p.getName().equals(mcp.getName())) {
							p.getPlayer().sendMessage(ChatColor.GOLD + mcp.getName() + " is no longer AFK.");
						}
					}
				}
				return;
			}
		}
	}
}