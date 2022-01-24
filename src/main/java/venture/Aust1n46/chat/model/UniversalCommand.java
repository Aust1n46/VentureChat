package venture.Aust1n46.chat.model;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import com.google.inject.Inject;

import venture.Aust1n46.chat.initiators.application.VentureChat;

public abstract class UniversalCommand extends Command implements PluginIdentifiableCommand {
	@Inject
	protected VentureChat plugin;

	protected UniversalCommand(final String name) {
		super(name);
	}

	@Override
	public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
		executeVoid(sender, commandLabel, args);
		return true;
	}

	public abstract void executeVoid(final CommandSender sender, final String commandLabel, final String[] args);

	@Override
	public Plugin getPlugin() {
		return plugin;
	}
}
