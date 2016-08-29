package mineverse.Aust1n46.chat.command.chat;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.command.MineverseCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Venturechat extends MineverseCommand {
	private MineverseChat plugin;

	public Venturechat(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "VentureChat Version: " + plugin.getDescription().getVersion());
		sender.sendMessage(ChatColor.GOLD + "Written by Aust1n46");
	}
}