package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

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
			plugin.getServer().getLogger().info("[VentureChat] Config reloaded");		
			for(MineverseChatPlayer player : MineverseChat.players) {
				if(player.isOnline() && player.getPlayer().hasPermission("venturechat.reload")) {
					player.getPlayer().sendMessage(LocalizedMessage.CONFIG_RELOADED.toString());
				}
			}
			return;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return;
	}
}