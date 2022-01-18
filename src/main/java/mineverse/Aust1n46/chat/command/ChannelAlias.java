package mineverse.Aust1n46.chat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class ChannelAlias extends Command {
	private MineverseChat plugin = MineverseChat.getInstance();

	public ChannelAlias() {
		super("channelalias");
	}

	@Override
	public boolean execute(final CommandSender sender, final String commandLabel, final String[] args) {
		if (!(sender instanceof Player)) {
			plugin.getServer().getConsoleSender().sendMessage(LocalizedMessage.COMMAND_MUST_BE_RUN_BY_PLAYER.toString());
			return true;
		}
		MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) sender);
		for (ChatChannel channel : ChatChannel.getChatChannels()) {
			if (commandLabel.toLowerCase().equals(channel.getAlias())) {
				if (args.length == 0) {
					mcp.getPlayer()
							.sendMessage(LocalizedMessage.SET_CHANNEL.toString().replace("{channel_color}", channel.getColor() + "").replace("{channel_name}", channel.getName()));
					if (mcp.hasConversation()) {
						for (MineverseChatPlayer p : MineverseChatAPI.getOnlineMineverseChatPlayers()) {
							if (p.isSpy()) {
								p.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION_SPY.toString().replace("{player_sender}", mcp.getName())
										.replace("{player_receiver}", MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName()));
							}
						}
						mcp.getPlayer().sendMessage(LocalizedMessage.EXIT_PRIVATE_CONVERSATION.toString().replace("{player_receiver}",
								MineverseChatAPI.getMineverseChatPlayer(mcp.getConversation()).getName()));
						mcp.setConversation(null);
					}
					mcp.addListening(channel.getName());
					mcp.setCurrentChannel(channel);
					if (channel.getBungee()) {
						MineverseChat.synchronize(mcp, true);
					}
					return true;
				} else {
					mcp.setQuickChat(true);
					mcp.setQuickChannel(channel);
					mcp.addListening(channel.getName());
					if (channel.getBungee()) {
						MineverseChat.synchronize(mcp, true);
					}
					String msg = "";
					for (int x = 0; x < args.length; x++) {
						if (args[x].length() > 0)
							msg += " " + args[x];
					}
					mcp.getPlayer().chat(msg);
					return true;
				}
			}
		}
		return true;
	}
}
