package venture.Aust1n46.chat.initators.commands;

import org.bukkit.command.CommandSender;

import com.google.inject.Inject;

import venture.Aust1n46.chat.initiators.application.VentureChat;
import venture.Aust1n46.chat.localization.InternalMessage;
import venture.Aust1n46.chat.model.UniversalCommand;

public class Venturechat extends UniversalCommand {
	@Inject
	private VentureChat plugin;

	@Inject
	public Venturechat(String name) {
		super(name);
	}

	@Override
	public void executeCommand(CommandSender sender, String command, String[] args) {
		sender.sendMessage(InternalMessage.VENTURECHAT_VERSION.toString().replace("{version}", plugin.getDescription().getVersion()));
		sender.sendMessage(InternalMessage.VENTURECHAT_AUTHOR.toString());
	}
}
