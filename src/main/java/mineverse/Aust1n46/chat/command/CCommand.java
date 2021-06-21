package mineverse.Aust1n46.chat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

//This class is a subclass of Bukkit's command class that is used for registering customizable commands, such as aliases 
//and channel aliases.
public class CCommand extends Command {

	private CommandExecutor exe = null;

	public CCommand(String name) {
		super(name);
	}

	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if(exe != null) {
			exe.onCommand(sender, this, commandLabel, args);
		}
		return false;
	}

	public void setExecutor(CommandExecutor exe) {
		this.exe = exe;
	}
}