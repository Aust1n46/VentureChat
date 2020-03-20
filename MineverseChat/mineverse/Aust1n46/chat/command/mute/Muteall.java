package mineverse.Aust1n46.chat.command.mute;

import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Muteall extends MineverseCommand {
	@SuppressWarnings("unused")
	private MineverseChat plugin;
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Muteall(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.mute")) {
			if(args.length < 1) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
						.replace("{command}", "/muteall")
						.replace("{args}", "[player]"));
				return;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if(player == null || (!player.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
				sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
						.replace("{args}", args[0]));
				return;
			}
			boolean bungee = false;
			for(ChatChannel channel : cc.getChannelsInfo()) {
				if(channel.isMutable()) {
					player.addMute(channel.getName(), 0);				
					if(channel.getBungee()) {
						bungee = true;
					}
				}
			}
			if(bungee) {
				MineverseChat.getInstance().synchronize(player, true);
			}
			sender.sendMessage(LocalizedMessage.MUTE_PLAYER_ALL_SENDER.toString()
					.replace("{player}", player.getName()));
			if(player.isOnline()) {
				player.getPlayer().sendMessage(LocalizedMessage.MUTE_PLAYER_ALL_PLAYER.toString());
			}
			else 
				player.setModified(true);
			return;
		}
		else {
			sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
			return;
		}
	}
}