package venture.Aust1n46.chat.initators.commands;

import org.bukkit.entity.Player;

import com.google.inject.Inject;

import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.PlayerCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Filter extends PlayerCommand {
	@Inject
	private VentureChatPlayerApiService playerApiService;

	@Inject
	public Filter(String name) {
		super(name);
	}

	@Override
	public void executeCommand(Player sender, String command, String[] args) {
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer((Player) sender);
		if (mcp.getPlayer().hasPermission("venturechat.ignorefilter")) {
			if (!mcp.isFilter()) {
				mcp.setFilter(true);
				mcp.getPlayer().sendMessage(LocalizedMessage.FILTER_ON.toString());
				return;
			}
			mcp.setFilter(false);
			mcp.getPlayer().sendMessage(LocalizedMessage.FILTER_OFF.toString());
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
	}
}
