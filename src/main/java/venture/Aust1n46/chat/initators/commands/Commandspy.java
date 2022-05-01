package venture.Aust1n46.chat.initators.commands;

import org.bukkit.entity.Player;

import com.google.inject.Inject;

import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.PlayerCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Commandspy extends PlayerCommand {
	@Inject
	private VentureChatPlayerApiService playerApiService;

	@Inject
	public Commandspy(String name) {
		super(name);
	}

	@Override
	public void executeCommand(Player sender, String command, String[] args) {
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer((Player) sender);
		if (mcp.getPlayer().hasPermission("venturechat.commandspy")) {
			if (!mcp.hasCommandSpy()) {
				mcp.setCommandSpy(true);
				mcp.getPlayer().sendMessage(LocalizedMessage.COMMANDSPY_ON.toString());
				return;
			}
			mcp.setCommandSpy(false);
			mcp.getPlayer().sendMessage(LocalizedMessage.COMMANDSPY_OFF.toString());
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
	}
}
