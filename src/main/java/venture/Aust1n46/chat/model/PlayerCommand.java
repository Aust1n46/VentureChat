package venture.Aust1n46.chat.model;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import venture.Aust1n46.chat.localization.LocalizedMessage;

public abstract class PlayerCommand extends UniversalCommand {
	protected PlayerCommand(final String name) {
		super(name);
	}

	@Override
	public void executeVoid(final CommandSender sender, final String commandLabel, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			execute(player, commandLabel, args);
		} else {
			plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
		}
	}

	public abstract void execute(final Player player, final String commandLabel, final String[] args);
}
