package venture.Aust1n46.chat.initators.commands;

import org.bukkit.entity.Player;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.PlayerCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class ChannelAlias extends PlayerCommand {
	@Inject
	private VentureChatPlayerApiService playerApiService;
	@Inject
	private ConfigService configService;
	@Inject
	private PluginMessageController pluginMessageController;

	public ChannelAlias() {
		super("channelalias");
	}

	@Override
	protected void executeCommand(final Player player, final String commandLabel, final String[] args) {
		VentureChatPlayer mcp = playerApiService.getOnlineMineverseChatPlayer(player);
		for (ChatChannel channel : configService.getChatChannels()) {
			if (commandLabel.toLowerCase().equals(channel.getAlias())) {
				if (args.length == 0) {
					mcp.getPlayer()
							.sendMessage(LocalizedMessage.SET_CHANNEL.toString().replace("{channel_color}", channel.getColor() + "").replace("{channel_name}", channel.getName()));
					if (mcp.hasConversation()) {
						for (VentureChatPlayer p : playerApiService.getOnlineMineverseChatPlayers()) {
							if (p.isSpy()) {
								p.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION_SPY.toString().replace("{player_sender}", mcp.getName())
										.replace("{player_receiver}", playerApiService.getMineverseChatPlayer(mcp.getConversation()).getName()));
							}
						}
						mcp.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION.toString().replace("{player_receiver}",
								playerApiService.getMineverseChatPlayer(mcp.getConversation()).getName()));
						mcp.setConversation(null);
					}
					mcp.addListening(channel.getName());
					mcp.setCurrentChannel(channel);
					if (channel.getBungee()) {
						pluginMessageController.synchronize(mcp, true);
					}
					return;
				} else {
					mcp.setQuickChat(true);
					mcp.setQuickChannel(channel);
					mcp.addListening(channel.getName());
					if (channel.getBungee()) {
						pluginMessageController.synchronize(mcp, true);
					}
					String msg = "";
					for (int x = 0; x < args.length; x++) {
						if (args[x].length() > 0)
							msg += " " + args[x];
					}
					mcp.getPlayer().chat(msg);
					return;
				}
			}
		}
	}
}
