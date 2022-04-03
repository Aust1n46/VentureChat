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

public class Kickchannel extends UniversalCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private VentureChatPlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	@Inject
	public Kickchannel(String name) {
		super(name);
	}

	@Override
	public void executeCommand(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.kickchannel")) {
			if (args.length < 2) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/kickchannel").replace("{args}", "[player] [channel]"));
				return;
			}
			VentureChatPlayer player = playerApiService.getMineverseChatPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return;
			}
			ChatChannel channel = configService.getChannel(args[1]);
			if (channel == null) {
				sender.sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[1]));
				return;
			}
			sender.sendMessage(LocalizedMessage.KICK_CHANNEL.toString().replace("{player}", args[0]).replace("{channel_color}", channel.getColor() + "").replace("{channel_name}",
					channel.getName()));
			player.removeListening(channel.getName());
			if (player.isOnline()) {
				player.getPlayer()
						.sendMessage(LocalizedMessage.LEAVE_CHANNEL.toString().replace("{channel_color}", channel.getColor() + "").replace("{channel_name}", channel.getName()));
			} else {
				player.setModified(true);
			}
			boolean isThereABungeeChannel = channel.getBungee();
			if (player.getListening().size() == 0) {
				player.addListening(configService.getDefaultChannel().getName());
				player.setCurrentChannel(configService.getDefaultChannel());
				if (configService.getDefaultChannel().getBungee()) {
					isThereABungeeChannel = true;
				}
				if (player.isOnline()) {
					player.getPlayer().sendMessage(LocalizedMessage.MUST_LISTEN_ONE_CHANNEL.toString());
					player.getPlayer()
							.sendMessage(LocalizedMessage.SET_CHANNEL.toString().replace("{channel_color}", ChatColor.valueOf(configService.getDefaultColor().toUpperCase()) + "")
									.replace("{channel_name}", configService.getDefaultChannel().getName()));
				} else
					player.setModified(true);
			}
			if (isThereABungeeChannel) {
				pluginMessageController.synchronize(player, true);
			}
			return;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
	}
}
