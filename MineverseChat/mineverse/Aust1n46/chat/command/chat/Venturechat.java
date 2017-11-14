package mineverse.Aust1n46.chat.command.chat;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		if (sender instanceof Player && plugin.getConfig().getString("loglevel", "info").equals("debug")) {
			Player player = (Player) sender;
			String title = ChatColor.GOLD + " | " + ChatColor.BLUE.toString() + ChatColor.BOLD + "Click here to begin..." + ChatColor.RESET + ChatColor.GOLD + " | ";
			String spaces = " ";
			TextComponent tcSpaces = new TextComponent(spaces);
			TextComponent message = new TextComponent(title);
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click here to rank up!").create()));
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rules 1"));
			tcSpaces.addExtra(message);
			player.spigot().sendMessage(tcSpaces);
		}
	}
}