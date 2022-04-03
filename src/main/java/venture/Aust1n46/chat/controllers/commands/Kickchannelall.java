package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.UniversalCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Kickchannelall extends UniversalCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	@Inject
	public Kickchannelall(String name) {
		super(name);
	}

	@Override
	public void executeCommand(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.kickchannelall")) {
			if (args.length < 1) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/kickchannelall").replace("{args}", "[player]"));
				return;
			}
			VentureChatPlayer player = playerApiService.getMineverseChatPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return;
			}
			boolean isThereABungeeChannel = false;
			for (String channel : player.getListening()) {
				if (configService.isChannel(channel)) {
					ChatChannel chatChannelObj = configService.getChannel(channel);
					if (chatChannelObj.getBungee()) {
						isThereABungeeChannel = true;
					}
				}
			}
			player.clearListening();
			sender.sendMessage(LocalizedMessage.KICK_CHANNEL_ALL_SENDER.toString().replace("{player}", player.getName()));
			player.addListening(configService.getDefaultChannel().getName());
			player.setCurrentChannel(configService.getDefaultChannel());
			if (configService.getDefaultChannel().getBungee()) {
				isThereABungeeChannel = true;
			}
			if (isThereABungeeChannel) {
				pluginMessageController.synchronize(player, true);
			}
			if (player.isOnline()) {
				player.getPlayer().sendMessage(LocalizedMessage.KICK_CHANNEL_ALL_PLAYER.toString());
				player.getPlayer().sendMessage(LocalizedMessage.MUST_LISTEN_ONE_CHANNEL.toString());
				player.getPlayer()
						.sendMessage(LocalizedMessage.SET_CHANNEL.toString().replace("{channel_color}", ChatColor.valueOf(configService.getDefaultColor().toUpperCase()) + "")
								.replace("{channel_name}", configService.getDefaultChannel().getName()));
			} else {
				player.setModified(true);
			}
			return;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
	}
}
