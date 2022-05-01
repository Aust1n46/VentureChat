package venture.Aust1n46.chat.initators.commands;

import org.bukkit.command.CommandSender;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.UniversalCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.VentureChatPlayerApiService;

public class Setchannelall extends UniversalCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	@Inject
	public Setchannelall(String name) {
		super(name);
	}

	@Override
	public void executeCommand(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.setchannelall")) {
			if (args.length < 1) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/setchannelall").replace("{args}", "[player]"));
				return;
			}
			VentureChatPlayer player = playerApiService.getMineverseChatPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return;
			}
			boolean isThereABungeeChannel = false;
			for (ChatChannel channel : configService.getChatChannels()) {
				if (channel.hasPermission()) {
					if (!player.isOnline()) {
						sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE_NO_PERMISSIONS_CHECK.toString());
						return;
					}
					if (!player.getPlayer().hasPermission(channel.getPermission())) {
						player.removeListening(channel.getName());
					} else {
						player.addListening(channel.getName());
					}
				} else {
					player.addListening(channel.getName());
				}
				if (channel.getBungee()) {
					isThereABungeeChannel = true;
				}
			}
			sender.sendMessage(LocalizedMessage.SET_CHANNEL_ALL_SENDER.toString().replace("{player}", player.getName()));
			if (player.isOnline())
				player.getPlayer().sendMessage(LocalizedMessage.SET_CHANNEL_ALL_PLAYER.toString());
			else
				player.setModified(true);
			if (isThereABungeeChannel) {
				pluginMessageController.synchronize(player, true);
			}
			return;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
	}
}
