package mineverse.Aust1n46.chat.command;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

//This class is a standard command executor that is used to run the MineverseCommand's.
public class MineverseCommandExecutor implements CommandExecutor {
	private Map<String, MineverseCommand> commands;

	public MineverseCommandExecutor(Map<String, MineverseCommand> commands) {
		this.commands = commands;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] parameters) {
		for(String com : commands.keySet()) {
			if(command.getName().equalsIgnoreCase(com)) {
				commands.get(com).execute(sender, command.getName(), parameters);
				return true;
			}
		}
		return false;
	}
}