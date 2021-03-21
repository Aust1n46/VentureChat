package mineverse.Aust1n46.chat.command.mute;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;
import mineverse.Aust1n46.chat.utilities.Format;

public class Mute extends MineverseCommand {
	private MineverseChat plugin = MineverseChat.getInstance();

	public Mute(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if (sender.hasPermission("venturechat.mute")) {
			if (args.length < 2) {
				sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/mute")
						.replace("{args}", "[player] [channel] {time}"));
				return;
			}
			if (ChatChannel.isChannel(args[1])) {
				ChatChannel channel = ChatChannel.getChannel(args[1]);
				if (channel.isMutable()) {
					long datetime = System.currentTimeMillis();
					long time = 0;
					if(args.length > 2) {
						try {
							time = Format.parseTimeStringToMillis(args[2]);
							if (time <= 0) {
								sender.sendMessage(LocalizedMessage.INVALID_TIME.toString().replace("{args}", args[2]));
								return;
							}
						} 
						catch (Exception e) {
							sender.sendMessage(LocalizedMessage.INVALID_TIME.toString().replace("{args}", args[2]));
							return;
						}
					}
					if(channel.getBungee()) {
						if(args.length > 2) {
							sendBungeeCordMute(sender, args[0], channel, datetime + time);
							return;
						}
						sendBungeeCordMute(sender, args[0], channel, 0);
						return;
					}
					MineverseChatPlayer playerToMute = MineverseChatAPI.getMineverseChatPlayer(args[0]);
					if (playerToMute == null || (!playerToMute.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
						sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString().replace("{args}", args[0]));
						return;
					}
					if (playerToMute.isMuted(channel.getName())) {
						sender.sendMessage(LocalizedMessage.PLAYER_ALREADY_MUTED.toString()
								.replace("{player}", playerToMute.getName()).replace("{channel_color}", channel.getColor())
								.replace("{channel_name}", channel.getName()));
						return;
					}
					if(args.length > 2) {
						playerToMute.addMute(channel.getName(), datetime + time);
						String timeString = Format.parseTimeStringFromMillis(time);
						sender.sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER_TIME.toString()
								.replace("{player}", playerToMute.getName())
								.replace("{channel_color}", channel.getColor())
								.replace("{channel_name}", channel.getName())
								.replace("{time}", timeString));
						if (playerToMute.isOnline()) {
							playerToMute.getPlayer()
									.sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER_TIME.toString()
											.replace("{channel_color}", channel.getColor())
											.replace("{channel_name}", channel.getName())
											.replace("{time}", timeString));
						}
						else {
							playerToMute.setModified(true);
						}
						return;
					}
					playerToMute.addMute(channel.getName(), 0);
					sender.sendMessage(LocalizedMessage.MUTE_PLAYER_SENDER.toString()
							.replace("{player}", playerToMute.getName()).replace("{channel_color}", channel.getColor())
							.replace("{channel_name}", channel.getName()));
					if (playerToMute.isOnline()) {
						playerToMute.getPlayer()
								.sendMessage(LocalizedMessage.MUTE_PLAYER_PLAYER.toString()
										.replace("{channel_color}", channel.getColor())
										.replace("{channel_name}", channel.getName()));
					}
					else {
						playerToMute.setModified(true);
					}
					return;
				}
				sender.sendMessage(LocalizedMessage.CHANNEL_CANNOT_MUTE.toString()
						.replace("{channel_color}", channel.getColor())
						.replace("{channel_name}", channel.getName()));
				return;
			}
			sender.sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[1]));
			return;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		if(args.length == 1) {
			StringUtil.copyPartialMatches(args[0], MineverseChat.networkPlayerNames, completions);
			Collections.sort(completions);
	        return completions;
		}
		if(args.length == 2) {
			StringUtil.copyPartialMatches(args[1], ChatChannel.getChatChannels().stream().map(ChatChannel::getName).collect(Collectors.toList()), completions);
			Collections.sort(completions);
	        return completions;
		}
		return Collections.emptyList();
	}
	
	private void sendBungeeCordMute(CommandSender sender, String playerToMute, ChatChannel channel, long time) {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOutStream);
		try {
			out.writeUTF("Mute");
			out.writeUTF("Send");
			if(sender instanceof Player) {
				out.writeUTF(((Player) sender).getUniqueId().toString());
			}
			else {
				out.writeUTF("VentureChat:Console");
			}
			out.writeUTF(playerToMute);
			out.writeUTF(channel.getName());
			out.writeLong(time);
			plugin.getServer().getOnlinePlayers().iterator().next().sendPluginMessage(plugin, MineverseChat.PLUGIN_MESSAGING_CHANNEL, byteOutStream.toByteArray());
			out.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
