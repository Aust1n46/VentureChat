package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.entity.Player;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.PlayerCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.PlayerApiService;

public class Listen extends PlayerCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private PlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	@Inject
	public Listen(String name) {
		super(name);
	}

	@Override
	public void executeCommand(Player player, String command, String[] args) {
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer(player);
		if (args.length > 0) {
			ChatChannel channel = configService.getChannel(args[0]);
			if (channel == null) {
				mcp.getPlayer().sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[0]));
				return;
			}
			if (channel.hasPermission()) {
				if (!mcp.getPlayer().hasPermission(channel.getPermission())) {
					mcp.getListening().remove(channel.getName());
					mcp.getPlayer().sendMessage(LocalizedMessage.CHANNEL_NO_PERMISSION.toString());
					return;
				}
			}
			mcp.getListening().add(channel.getName());
			mcp.getPlayer()
					.sendMessage(LocalizedMessage.LISTEN_CHANNEL.toString().replace("{channel_color}", channel.getColor() + "").replace("{channel_name}", channel.getName()));
			if (channel.getBungee()) {
				pluginMessageController.synchronize(mcp, true);
			}
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/listen").replace("{args}", "[channel]"));
	}
}
