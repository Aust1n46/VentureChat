package mineverse.Aust1n46.chat.command.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.api.events.ChannelJoinEvent;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Channel extends Command implements Listener {
	public Channel() {
		super("channel");
	}

	@Override
	public boolean execute(CommandSender sender, String command, String[] args) {
		if (!(sender instanceof Player)) {
			Bukkit.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return true;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
		if (args.length > 0) {
			if (!ChatChannel.isChannel(args[0])) {
				mcp.getPlayer().sendMessage(LocalizedMessage.INVALID_CHANNEL.toString().replace("{args}", args[0]));
				return true;
			}
			ChatChannel channel = ChatChannel.getChannel(args[0]);
			Bukkit.getServer().getPluginManager().callEvent(new ChannelJoinEvent(mcp.getPlayer(), channel,
					LocalizedMessage.SET_CHANNEL.toString().replace("{channel_color}", channel.getColor() + "").replace("{channel_name}", channel.getName())));
			return true;
		}
		mcp.getPlayer().sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString().replace("{command}", "/channel").replace("{args}", "[channel]"));
		return true;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChannelJoin(ChannelJoinEvent event) {
		if (event.isCancelled())
			return;
		ChatChannel channel = event.getChannel();
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(event.getPlayer());
		if (channel.hasPermission()) {
			if (!mcp.getPlayer().hasPermission(channel.getPermission())) {
				mcp.removeListening(channel.getName());
				mcp.getPlayer().sendMessage(LocalizedMessage.CHANNEL_NO_PERMISSION.toString());
				return;
			}
		}
		if (mcp.hasConversation()) {
			for (MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
				if (p.isSpy()) {
					p.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION_SPY.toString().replace("{player_sender}", mcp.getName()).replace("{player_receiver}",
							MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName()));
				}
			}
			mcp.getPlayer().sendMessage(
					LocalizedMessage.EXIT_PRIVATE_CONVERSATION.toString().replace("{player_receiver}", MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName()));
			mcp.setConversation(null);
		}
		mcp.addListening(channel.getName());
		mcp.setCurrentChannel(channel);
		mcp.getPlayer().sendMessage(event.getMessage());
		if (channel.getBungee()) {
			MineverseChat.synchronize(mcp, true);
		}
		return;
	}
}
