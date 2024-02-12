package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.entity.Player;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.PlayerCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.PlayerApiService;

public class BungeeToggle extends PlayerCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private PlayerApiService playerApiService;

	@Inject
	public BungeeToggle(String name) {
		super(name);
	}

	@Override
	protected void executeCommand(Player player, String command, String[] args) {
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer(player);
		if (mcp.getPlayer().hasPermission("venturechat.bungeetoggle")) {
			if (!mcp.isBungeeToggle()) {
				mcp.setBungeeToggle(true);
				mcp.getPlayer().sendMessage(LocalizedMessage.BUNGEE_TOGGLE_ON.toString());
				pluginMessageController.synchronize(mcp, true);
				return;
			}
			mcp.setBungeeToggle(false);
			mcp.getPlayer().sendMessage(LocalizedMessage.BUNGEE_TOGGLE_OFF.toString());
			pluginMessageController.synchronize(mcp, true);
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return;
	}
}
