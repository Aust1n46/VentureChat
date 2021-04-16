package mineverse.Aust1n46.chat.command.message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Notifications extends MineverseCommand {

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return;
		}

		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
		if(!mcp.hasNotifications()) {
			mcp.setNotifications(true);
			mcp.getPlayer().sendMessage(LocalizedMessage.NOTIFICATIONS_ON.toString());
			return;
		}
		mcp.setNotifications(false);
		mcp.getPlayer().sendMessage(LocalizedMessage.NOTIFICATIONS_OFF.toString());
		return;
	}
}