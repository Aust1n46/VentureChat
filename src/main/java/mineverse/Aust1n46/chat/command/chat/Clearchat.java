package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.localization.InternalMessage;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Clearchat extends Command {
	public Clearchat() {
		super("clearchat");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.clearchat")) {
			for (MineverseChatPlayer player : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
				if (!player.getPlayer().hasPermission("venturechat.clearchat.bypass")) {
					for (int a = 1; a <= 20; a++)
						player.getPlayer().sendMessage(InternalMessage.EMPTY_STRING.toString());
					player.getPlayer().sendMessage(LocalizedMessage.CLEAR_CHAT_SERVER.toString());
				}
			}
			sender.sendMessage(LocalizedMessage.CLEAR_CHAT_SENDER.toString());
			return true;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return true;
	}
}
