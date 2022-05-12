package venture.Aust1n46.chat.initators.commands;

import org.bukkit.entity.Player;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import venture.Aust1n46.chat.api.events.ChannelJoinEvent;
import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.PlayerCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

@Singleton
public class Channel extends PlayerCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	@Inject
	public Channel(String name) {
		super(name);
	}

	@Override
	protected void executeCommand(final Player player, final String commandLabel, final String[] args) {
		final VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer(player);
		if (args.length > 0) {
			if (!configService.isChannel(args[0])) {
				mcp.getPlayer().sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[0]));
				return;
			}
			ChatChannel channel = configService.getChannel(args[0]);
			ChannelJoinEvent channelJoinEvent = new ChannelJoinEvent(mcp.getPlayer(), channel,
					LocalizedMessage.SET_CHANNEL.toString().replace("{channel_color}", channel.getColor() + "").replace("{channel_name}", channel.getName()));
			plugin.getServer().getPluginManager().callEvent(channelJoinEvent);
			handleChannelJoinEvent(channelJoinEvent);
			return;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/channel").replace("{args}", "[channel]"));
	}

	private void handleChannelJoinEvent(final ChannelJoinEvent event) {
		if (event.isCancelled())
			return;
		ChatChannel channel = event.getChannel();
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer(event.getPlayer());
		if (channel.hasPermission()) {
			if (!mcp.getPlayer().hasPermission(channel.getPermission())) {
				mcp.removeListening(channel.getName());
				mcp.getPlayer().sendMessage(LocalizedMessage.CHANNEL_NO_PERMISSION.toString());
				return;
			}
		}
		if (mcp.hasConversation()) {
			for (VentureChatPlayer p : playerApiService.getOnlineMineverseChatPlayers()) {
				if (p.isSpy()) {
					p.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION_SPY.toString().replace("{player_sender}", mcp.getName()).replace("{player_receiver}",
							playerApiService.getMineverseChatPlayer(mcp.getConversation()).getName()));
				}
			}
			mcp.getPlayer().sendMessage(
					LocalizedMessage.EXIT_PRIVATE_CONVERSATION.toString().replace("{player_receiver}", playerApiService.getMineverseChatPlayer(mcp.getConversation()).getName()));
			mcp.setConversation(null);
		}
		mcp.addListening(channel.getName());
		mcp.setCurrentChannel(channel);
		mcp.getPlayer().sendMessage(event.getMessage());
		if (channel.getBungee()) {
			pluginMessageController.synchronize(mcp, true);
		}
	}
}
