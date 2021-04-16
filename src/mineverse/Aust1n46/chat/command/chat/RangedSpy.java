package mineverse.Aust1n46.chat.command.chat;

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RangedSpy extends MineverseCommand {

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
		if(mcp.getPlayer().hasPermission("venturechat.rangedspy")) {
			if(!mcp.getRangedSpy()) {				
				mcp.setRangedSpy(true);
				mcp.getPlayer().sendMessage(LocalizedMessage.RANGED_SPY_ON.toString());
				return;
			}			
			mcp.setRangedSpy(false);
			mcp.getPlayer().sendMessage(LocalizedMessage.RANGED_SPY_OFF.toString());
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return;
	}
}
