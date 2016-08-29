package mineverse.Aust1n46.chat.command.mute;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.channel.ChatChannelInfo;
import mineverse.Aust1n46.chat.command.MineverseCommand;

public class Unmuteall extends MineverseCommand {
	@SuppressWarnings("unused")
	private MineverseChat plugin;
	private ChatChannelInfo cc = MineverseChat.ccInfo;

	public Unmuteall(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.mute")) {
			if(args.length < 1) {
				sender.sendMessage(ChatColor.RED + "Invalid command: /unmuteall [player]");
				return;
			}
			MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
			if(player == null || (!player.isOnline() && !sender.hasPermission("venturechat.mute.offline"))) {
				sender.sendMessage(ChatColor.RED + "Player: " + ChatColor.GOLD + args[0] + ChatColor.RED + " is not online.");
				return;
			}
			boolean bungee = false;
			for(ChatChannel channel : cc.getChannelsInfo()) {
				player.removeMute(channel.getName());				
				if(channel.getBungee()) {
					bungee = true;
				}
			}
			if(bungee) {
				MineverseChat.getInstance().synchronize(player, true);
			}
			sender.sendMessage(ChatColor.RED + "Unmuted player " + ChatColor.GOLD + player.getName() + ChatColor.RED + " in all channels.");
			if(player.isOnline()) {
				player.getPlayer().sendMessage(ChatColor.RED + "You have just been unmuted in all channels.");
			}
			else 
				player.setModified(true);
			return;
		}
		else {
			sender.sendMessage(ChatColor.RED + "You do not have permission for this command.");
			return;
		}
	}
}