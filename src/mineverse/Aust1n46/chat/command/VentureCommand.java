package mineverse.Aust1n46.chat.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Interface for the plugin's commands.
 */
public interface VentureCommand {
	public void execute(CommandSender sender, String command, String[] args);

	public default List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
	}
}
