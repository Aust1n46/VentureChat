package mineverse.Aust1n46.chat.command.message;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Spy extends MineverseCommand {
	private MineverseChat plugin;

	public Spy(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer((Player) sender);
		if(mcp.getPlayer().hasPermission("venturechat.spy")) {
			if(!mcp.isSpy()) {				
				mcp.setSpy(true);
				mcp.getPlayer().sendMessage(LocalizedMessage.SPY_ON.toString());
				MineverseChat.getInstance().synchronize(mcp, true);
				return;
			}			
			mcp.setSpy(false);
			mcp.getPlayer().sendMessage(LocalizedMessage.SPY_OFF.toString());
			MineverseChat.getInstance().synchronize(mcp, true);
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return;
	}
}