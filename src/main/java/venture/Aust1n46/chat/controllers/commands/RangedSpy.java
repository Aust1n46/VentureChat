package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.entity.Player;

import com.google.inject.Inject;

import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.PlayerCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.PlayerApiService;

public class RangedSpy extends PlayerCommand {
	@Inject
	private PlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	@Inject
	public RangedSpy(String name) {
		super(name);
	}

	@Override
	public void executeCommand(Player player, String command, String[] args) {
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer((Player) player);
		if (mcp.getPlayer().hasPermission("venturechat.rangedspy")) {
			if (!configService.isRangedSpy(mcp)) {
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
