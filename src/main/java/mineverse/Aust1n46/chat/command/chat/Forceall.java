package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Forceall extends Command {
	public Forceall() {
		super("forceall");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.forceall")) {
			if (args.length < 1) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/forceall").replace("{args}", "[message]"));
				return true;
			}
			String forcemsg = "";
			for (int x = 0; x < args.length; x++) {
				if (args[x].length() > 0) {
					forcemsg += args[x] + " ";
				}
			}
			sender.sendMessage(LocalizedMessage.FORCE_ALL.toString().replace("{message}", forcemsg));
			for (MineverseChatPlayer player : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
				player.getPlayer().chat(forcemsg);
			}
			return true;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return true;
	}
}
