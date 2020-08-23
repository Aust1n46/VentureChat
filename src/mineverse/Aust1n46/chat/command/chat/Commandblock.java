package mineverse.Aust1n46.chat.command.chat;

import java.util.List;

import org.bukkit.command.CommandSender;

import mineverse.Aust1n46.chat.MineverseChat;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.command.MineverseCommand;
import mineverse.Aust1n46.chat.localization.LocalizedMessage;

public class Commandblock extends MineverseCommand {
	private MineverseChat plugin;

	public Commandblock(String name) {
		super(name);
		this.plugin = MineverseChat.getInstance();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args) {
		if(sender.hasPermission("venturechat.commandblock")) {
			if(args.length > 1) {
				MineverseChatPlayer player = MineverseChatAPI.getMineverseChatPlayer(args[0]);
				if(player == null) {
					sender.sendMessage(LocalizedMessage.PLAYER_OFFLINE.toString()
							.replace("{args}", args[0]));
					return;
				}
				boolean match = false;
				for(String cb : (List<String>) plugin.getConfig().getStringList("blockablecommands")) 
					if(args[1].equals("/" + cb)) 
						match = true;
				if(match || player.isBlockedCommand(args[1])) {
					if(!player.isBlockedCommand(args[1])) {
						player.addBlockedCommand(args[1]);
						player.getPlayer().sendMessage(LocalizedMessage.BLOCK_COMMAND_PLAYER.toString()
								.replace("{command}", args[1]));
						sender.sendMessage(LocalizedMessage.BLOCK_COMMAND_SENDER.toString()
								.replace("{player}", player.getName())
								.replace("{command}", args[1]));
						return;
					}
					player.removeBlockedCommand(args[1]);
					player.getPlayer().sendMessage(LocalizedMessage.UNBLOCK_COMMAND_PLAYER.toString()
							.replace("{command}", args[1]));
					sender.sendMessage(LocalizedMessage.UNBLOCK_COMMAND_SENDER.toString()
							.replace("{player}", player.getName())
							.replace("{command}", args[1]));
					return;
				}
				sender.sendMessage(LocalizedMessage.COMMAND_NOT_BLOCKABLE.toString());
				return;
			}
			sender.sendMessage(LocalizedMessage.COMMAND_INVALID_ARGUMENTS.toString()
					.replace("{command}", "/commandblock")
					.replace("{args}", "[player] [command]"));
			return;
		}
		sender.sendMessage(LocalizedMessage.COMMAND_NO_PERMISSION.toString());
	}
}