package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.entity.Player;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.PlayerCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class MessageToggle extends PlayerCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private VentureChatPlayerApiService playerApiService;

	@Inject
	public MessageToggle(String name) {
		super(name);
	}

	@Override
	public void executeCommand(Player player, String command, String[] args) {
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer(player);
		if (mcp.getPlayer().hasPermission("venturechat.messagetoggle")) {
			if (!mcp.isMessageToggle()) {
				mcp.setMessageToggle(true);
				mcp.getPlayer().sendMessage(LocalizedMessage.MESSAGE_TOGGLE_ON.toString());
				pluginMessageController.synchronize(mcp, true);
				return;
			}
			mcp.setMessageToggle(false);
			mcp.getPlayer().sendMessage(LocalizedMessage.MESSAGE_TOGGLE_OFF.toString());
			pluginMessageController.synchronize(mcp, true);
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
		return;
	}
}
