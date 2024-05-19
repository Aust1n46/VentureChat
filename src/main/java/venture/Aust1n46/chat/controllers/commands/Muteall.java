package venture.Aust1n46.chat.controllers.commands;

import org.bukkit.command.CommandSender;

import com.google.inject.Inject;

import venture.Aust1n46.chat.controllers.PluginMessageController;
import venture.Aust1n46.chat.localization.LocalizedMessage;
import venture.Aust1n46.chat.model.ChatChannel;
import venture.Aust1n46.chat.model.MuteContainer;
import venture.Aust1n46.chat.model.UniversalCommand;
import venture.Aust1n46.chat.model.VentureChatPlayer;
import venture.Aust1n46.chat.service.ConfigService;
import venture.Aust1n46.chat.service.PlayerApiService;
import venture.Aust1n46.chat.utilities.FormatUtils;

public class Muteall extends UniversalCommand {
	@Inject
	private PluginMessageController pluginMessageController;
	@Inject
	private PlayerApiService playerApiService;
	@Inject
	private ConfigService configService;

	@Inject
	public Muteall(String name) {
		super(name);
	}

	@Override
	public void executeCommand(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.mute")) {
			if (args.length < 1) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/muteall").replace("{args}", "[player] {reason}"));
				return;
			}
			VentureChatPlayer player = playerApiService.getMineverseChatPlayer(args[0]);
			if (player == null || (!player.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
				return;
			}
			String reason = "";
			if (args.length > 1) {
				StringBuilder reasonBuilder = new StringBuilder();
				for (int a = 1; a < args.length; a++) {
					reasonBuilder.append(args[a] + " ");
				}
				reason = FormatUtils.FormatStringAll(reasonBuilder.toString().trim());
			}
			if (reason.isEmpty()) {
				boolean bungee = false;
				for (ChatChannel channel : configService.getChatChannels()) {
					if (channel.isMutable()) {
						player.getMutes().put(channel.getName(), new MuteContainer(channel.getName(), 0, ""));
						if (channel.isBungeeEnabled()) {
							bungee = true;
						}
					}
				}
				if (bungee) {
					pluginMessageController.synchronize(player, true);
				}
				sender.sendMessage(LocalizedMessage.MUTE_PLAYER_ALL_SENDER.toString().replace("{player}", player.getName()));
				if (player.isOnline()) {
					player.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_ALL_PLAYER.toString());
				} else
					player.setModified(true);
				return;
			} else {
				boolean bungee = false;
				for (ChatChannel channel : configService.getChatChannels()) {
					if (channel.isMutable()) {
						player.getMutes().put(channel.getName(), new MuteContainer(channel.getName(), 0, reason));
						if (channel.isBungeeEnabled()) {
							bungee = true;
						}
					}
				}
				if (bungee) {
					pluginMessageController.synchronize(player, true);
				}
				sender.sendMessage(LocalizedMessage.MUTE_PLAYER_ALL_SENDER_REASON.toString().replace("{player}", player.getName()).replace("{reason}", reason));
				if (player.isOnline()) {
					player.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_ALL_PLAYER_REASON.toString().replace("{reason}", reason));
				} else
					player.setModified(true);
				return;
			}
		} else {
			sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
			return;
		}
	}
}
