package mineverse.Aust1n46.chat.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

//This class is the parent class of all of the plugins commands.  The execute method runs the command.
public abstract class MineverseCommand {
	public abstract void execute(CommandSender sender, String command, String[] args);
	
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
	}
}
