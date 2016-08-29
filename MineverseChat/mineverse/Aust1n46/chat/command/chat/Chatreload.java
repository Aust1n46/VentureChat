package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Chatreload extends MineverseCommand {
	private MineverseChat plugin;

	public Chatreload(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.reload")) {
			plugin.reloadConfig();
			Bukkit.getPluginManager().disablePlugin(plugin);
			Bukkit.getPluginManager().enablePlugin(plugin);
			plugin.getServer().getLogger().info("[" + plugin.getConfig().getString("pluginname", "MineverseChat") + "] Config reloaded");		
			for(MineverseChatPlayer player : MineverseChat.players) {
				if(player.isOnline() && player.getPlayer().hasPermission("venturechat.reload")) {
					player.getPlayer().sendMessage(ChatColor.GOLD + plugin.getConfig().getString("pluginname", "MineverseChat") + " config reloaded.");
				}
			}
			return;
		}
		sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
		return;
	}
}