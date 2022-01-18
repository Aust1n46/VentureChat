package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Force extends Command {
	public Force() {
		super("force");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.force")) {
			if (args.length < 2) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/force").replace("{args}", "[player] [message]"));
				return true;
			}
			MineverseChatPlayer player = MineverseChatAPI.getOnlineMineverseChatPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return true;
			}
			String forcemsg = "";
			for (int x = 1; x < args.length; x++)
				if (args[x].length() > 0)
					forcemsg += args[x] + " ";
			sender.sendMessage(LocalizedMessage.FORCE_PLAYER.toString().replace("{player}", player.getName()).replace("{message}", forcemsg));
			player.getPlayer().chat(forcemsg);
			return true;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return true;
	}
}
