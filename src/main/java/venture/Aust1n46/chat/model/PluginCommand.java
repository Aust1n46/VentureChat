package venture.Aust1n46.chat.model;

import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import com.google.inject.Inject;

import venture.Aust1n46.chat.initiators.application.VentureChat;

public abstract class PluginCommand extends Command implements PluginIdentifiableCommand {
	@Inject
	protected VentureChat plugin;

	protected PluginCommand(final String name) {
		super(name);
	}

	@Override
	public final Plugin getPlugin() {
		return plugin;
	}
}
