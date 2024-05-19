package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.command.CommandSender;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.UniversalCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.PlayerApiService;

public class Unmuteall extends UniversalCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private PlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	@Inject
	public Unmuteall(String name) {
		super(name);
	}

	@Override
	public void executeCommand(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.mute")) {
			if (args.length < 1) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/unmuteall").replace("{args}", "[player]"));
				return;
			}
			VentureChatPlayer player = playerApiService.getMineverseChatPlayer(args[0]);
			if (player == null || (!player.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return;
			}
			boolean bungee = false;
			for (ChatChannel channel : configService.getChatChannels()) {
				player.getMutes().remove(channel.getName());
				if (channel.getBungee()) {
					bungee = true;
				}
			}
			if (bungee) {
				pluginMessageController.synchronize(player, true);
			}
			sender.sendMessage(LocalizedMessage.UNMUTE_PLAYER_ALL_SENDER.toString().replace("{player}", player.getName()));
			if (player.isOnline()) {
				player.getPlayer().sendMessage(LocalizedMessage.UNMUTE_PLAYER_ALL_PLAYER.toString());
			} else
				player.setModified(true);
			return;
		} else {
			sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
			return;
		}
	}
}
