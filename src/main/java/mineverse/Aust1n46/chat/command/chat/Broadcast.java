package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;

public class Broadcast extends Command {
	private MineverseChat plugin = MineverseChat.getInstance();

	public Broadcast() {
		super("broadcast");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		ConfigurationSection bs = plugin.getConfig().getConfigurationSection("broadcast");
		String broadcastColor = bs.getString("color", "white");
		String broadcastPermissions = bs.getString("permissions", "None");
		String broadcastDisplayTag = Format.FormatStringAll(bs.getString("displaytag", "[Broadcast]"));
		if (broadcastPermissions.equalsIgnoreCase("None") || sender.hasPermission(broadcastPermissions)) {
			if (args.length > 0) {
				String bc = "";
				for (int x = 0; x < args.length; x++) {
					if (args[x].length() > 0)
						bc += args[x] + " ";
				}
				bc = Format.FormatStringAll(bc);
				Format.broadcastToServer(broadcastDisplayTag + ChatColor.valueOf(broadcastColor.toUpperCase()) + " " + bc);
				return true;
			} else {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/broadcast").replace("{args}", "[msg]"));
				return true;
			}
		} else {
			sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
			return true;
		}
	}
}
