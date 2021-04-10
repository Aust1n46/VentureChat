package mineverse.Aust1n46.chat.command;

import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

//This class is a standard command executor that is used to run the MineverseCommand's.
public class MineverseCommandExecutor implements TabExecutor {
	private Map<String, MineverseCommand> commands;

	public MineverseCommandExecutor(Map<String, MineverseCommand> commands) {
		this.commands = commands;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] parameters) {
		commands.get(command.getName()).execute(sender, command.getName(), parameters);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return commands.get(command.getName()).onTabComplete(sender, command, label, args);
	}
}
