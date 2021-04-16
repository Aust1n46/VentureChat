package mineverse.Aust1n46.chat.command.message;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.VentureCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class MessageToggle implements VentureCommand {

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
		if(mcp.getPlayer().hasPermission("venturechat.messagetoggle")) {
			if(!mcp.getMessageToggle()) {				
				mcp.setMessageToggle(true);
				mcp.getPlayer().sendMessage(LocalizedMessage.MESSAGE_TOGGLE_ON.toString());
				MineverseChat.getInstance().synchronize(mcp, true);
				return;
			}			
			mcp.setMessageToggle(false);
			mcp.getPlayer().sendMessage(LocalizedMessage.MESSAGE_TOGGLE_OFF.toString());
			MineverseChat.getInstance().synchronize(mcp, true);
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return;
	}
}
