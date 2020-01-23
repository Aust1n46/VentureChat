package mineverse.Aust1n46.chat.bungee.command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import mineverse.Aust1n46.chat.bungee.MineverseChatBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GlobalUnmuteAll extends Command {
	private MineverseChatBungee plugin;
	private String alias;

	public GlobalUnmuteAll(MineverseChatBungee plugin, String alias) {
		super(alias);
		this.plugin = plugin;
		this.alias = alias;
	}

	@Override
	public void execute(CommandSender commandSender, String[] args) {
		if(!(commandSender instanceof ProxiedPlayer)) {
			return;
		}
		if(commandSender.hasPermission("venturechat.mute")) {
			if(args.length < 2) {
				commandSender.sendMessage(new TextComponent(ChatColor.RED + "Invalid command: /" + alias + " [server] [player]"));
				return;
			}
			ProxiedPlayer player = plugin.getProxy().getPlayer(args[1]);
			if(player != null) {
				if(plugin.getProxy().getServers().containsKey(args[0])) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(stream);
					try {
						out.writeUTF("Unmuteall");
						out.writeUTF(commandSender.getName());
						out.writeUTF(args[1]);
						out.writeUTF(args[0]);
						if(plugin.getProxy().getServers().get(args[0]).getPlayers().size() > 0) {
							plugin.getProxy().getServers().get(args[0]).sendData(MineverseChatBungee.PLUGIN_MESSAGING_CHANNEL, stream.toByteArray());
							return;
						}
						commandSender.sendMessage(new TextComponent(ChatColor.RED + "Player: " + ChatColor.GOLD + args[1] + ChatColor.RED + " is not connected to server: " + args[0]));
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					return;
				}
				commandSender.sendMessage(new TextComponent(ChatColor.RED + "Invalid server: " + args[0]));
				return;
			}
			commandSender.sendMessage(new TextComponent(ChatColor.RED + "Player: " + ChatColor.GOLD + args[1] + ChatColor.RED + " is not online."));
			return;
		}
		commandSender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission for this command."));
		return;
	}
}